package com.game.Ticket_To_Flight.frontend;

import com.esotericsoftware.kryonet.Connection;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airline;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.AbilityType;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PlaneType;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.commonFrontAndBack.LowLevelHandler;
import com.game.Ticket_To_Flight.network.Network;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

public class LowLevelHandlerFront extends LowLevelHandler {
    private final GameClient gameClient = new GameClient(this);
    private Connection serverCon = null;
    private Integer myId = null;
    private final MainClient mainClient;

    public class Flags{
        public enum GamePreparationsState{
            WAITING_FOR_CONNECT_CALL,
            SEARCHING_FOR_SERVER,
            READY_TO_JOIN_THE_GAME,
            WAITING_FOR_SERVER_RESPONSE,
            WAITING_FOR_OTHER_PLAYERS_TO_JOIN, // waiting for other players
            RUNNING
        }
        public volatile GamePreparationsState gamePreparationsState = GamePreparationsState.WAITING_FOR_CONNECT_CALL;
        public volatile Network.JoinGameResponse.Response joinGameResponse = null;
        public enum CurrentStateState{
            NOT_IN_GAME,
            NO_PLAYER_STAGE,
            PLAYER_STAGE,
            WAITING_FOR_PLAYER_CHOICE,
            WAITING_FOR_SERVER_RESPONSE,
            RECEIVED_ERROR_MESSAGE
        }
        public volatile CurrentStateState currentStateState = CurrentStateState.NOT_IN_GAME;
        public volatile String errorMessage = null;
    }

    public Flags flags = new Flags();

    public LowLevelHandlerFront(GameData data,  MainClient mainClient){
        super(data); this.mainClient = mainClient;}

    //------------------------------------- data changes part
    private final ExecutorService validationExecutor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "GameData-Validator-Thread");
        t.setDaemon(true);
        return t;
    });
    private volatile GameData.DataChanges checkedChanges;
    private final AtomicBoolean isValidationRunning = new AtomicBoolean(false);
    public final Queue<GameData.DataChanges> changesQueue = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean interruptValidator = new AtomicBoolean(false);
    private volatile Future<?> currentValidationTask = null;

    public void resetGameData(GameData.DataChanges dataChanges) {
        if (isValidationRunning.get()) {
            interruptValidator.set(true);
            if (currentValidationTask != null) {
                currentValidationTask.cancel(true);
            }
        }
        changesQueue.clear();

        gameData.acquireWriteLock();
        try {
            checkedChanges = null;
            gameData.clearGameData();
            gameData.applyChangesUnsafe(dataChanges);
            changeFlagDependingOnNewState(dataChanges.currentState);
        } catch (Exception e) {
            dataInconsistent();
        } finally {
            gameData.releaseWriteLock();
        }
    }


    public void updateChanges() {
        if (checkedChanges != null) {
            gameData.acquireWriteLock();
            try {
                //System.out.println("Applying changes");
                gameData.applyChangesUnsafe(checkedChanges);
                changeFlagDependingOnNewState(checkedChanges.currentState);
                checkedChanges = null;
            }
            catch (Exception e){
                dataInconsistent();
            }
            finally {
                gameData.releaseWriteLock();
            }
        }
        if (!isValidationRunning.get() && !changesQueue.isEmpty()) {
            startAsyncValidation();
        }
    }

    private void startAsyncValidation() {
        isValidationRunning.set(true);

        currentValidationTask = validationExecutor.submit(() -> {
            boolean isValid;
            GameData.DataChanges change;

            try {
                gameData.acquireReadLock();

                if (interruptValidator.compareAndSet(true, false)) {
                    return;
                }

                change = changesQueue.poll();
                if (change != null) {
                    while (!changesQueue.isEmpty()) {
                        GameData.DataChanges newDataChanges = changesQueue.poll();
                        if (newDataChanges != null) {
                            change.merge(newDataChanges);
                        }
                    }
                }

                isValid = gameData.checkChanges(change);

                if (interruptValidator.compareAndSet(true, false)) {
                    return;
                }

                if (isValid) {
                    checkedChanges = change;
                } else {
                    dataInconsistent();
                }

            } catch (Exception e) {

                if (interruptValidator.compareAndSet(true, false)) {
                    return;
                }
                System.out.println("Error: " + e.toString());
                dataInconsistent();
            } finally {
                gameData.releaseReadLock();
                isValidationRunning.set(false);
            }
        });
    }

    private void dataInconsistent(){
        checkedChanges = null;
        changesQueue.clear();
        sendMessageToServer(new Network.ReloadGameDataRequest());
    }

    private void changeFlagDependingOnNewState(GameData.State st){
        if(st == null) return;
        if(st == GameData.State.WORLD_UPDATE ||
            st == GameData.State.INCOME||
            st == GameData.State.TAXES ||
            st == GameData.State.EVENT
        )
            flags.currentStateState = Flags.CurrentStateState.NO_PLAYER_STAGE;
        else
            flags.currentStateState = Flags.CurrentStateState.PLAYER_STAGE;
    }

    //------------------------------------- data changes part

    //------------------------------------- messages part

    @Override
    public void handleNewConnection(Connection con){
        serverCon = con;
        flags.gamePreparationsState = Flags.GamePreparationsState.READY_TO_JOIN_THE_GAME;
    }

    @Override
    protected void handleIncomingMessage(Connection con, Network.GameMessage message){
        if(con!=serverCon) return;

        if(message instanceof Network.DataChangesMessage){
            changesQueue.offer(((Network.DataChangesMessage) message).dc);
        }
        else if(message instanceof Network.JoinGameResponse){
            Network.JoinGameResponse resp = (Network.JoinGameResponse) message;

            flags.joinGameResponse = resp.response;
            if(resp.response == Network.JoinGameResponse.Response.SUCCESS) {
                flags.gamePreparationsState = Flags.GamePreparationsState.WAITING_FOR_OTHER_PLAYERS_TO_JOIN;
            }
            else{
                flags.gamePreparationsState = Flags.GamePreparationsState.READY_TO_JOIN_THE_GAME;
            }
        }
        else if(message instanceof Network.StartGameMessage){
            myId = ((Network.StartGameMessage) message).myId;
            mainClient.changeScreenToRunning();
            flags.gamePreparationsState = Flags.GamePreparationsState.RUNNING;
            System.out.println("game is running");
        }
        else if(message instanceof Network.ReloadGameDataResponse){
            Network.ReloadGameDataResponse resp = (Network.ReloadGameDataResponse) message;
            resetGameData(resp.dc);
        }
        else if(message instanceof Network.ErrorMessage){
            flags.currentStateState = Flags.CurrentStateState.RECEIVED_ERROR_MESSAGE;
            flags.errorMessage = ((Network.ErrorMessage) message).getMessage();
        }
        else throw new IllegalArgumentException("Unknown message");
    }

    private void sendMessageToServer(Network.GameMessage message){
        addMessage(serverCon, message);
    }

    //------------------------------------- messages part

    //------------------------------------- update part
    @Override
    public boolean update(){
        if(serverCon==null || !serverCon.isConnected()) return false;

        handleAllIncomingMessages();
        updateChanges();
        sendAllWaitingMessages();

        return true;
    }
    //------------------------------------- update part

    //------------------------------------- for use from MainClient

    public boolean connectToServer(){
        if(flags.gamePreparationsState != Flags.GamePreparationsState.WAITING_FOR_CONNECT_CALL) return false;
        //System.out.println("Looking for server");
        flags.gamePreparationsState = Flags.GamePreparationsState.SEARCHING_FOR_SERVER;
        gameClient.connect();

        if(!isConnected()) {
            flags.gamePreparationsState = Flags.GamePreparationsState.WAITING_FOR_CONNECT_CALL;
            return false;
        }
        return true;
    }

    public boolean sendJoinRequest(String name){
        //System.out.println("Trying to add join game request message");
        if(flags.gamePreparationsState == Flags.GamePreparationsState.READY_TO_JOIN_THE_GAME && serverCon.isConnected()){
            //System.out.println("Adding join game request message");
            sendMessageToServer(new Network.JoinGameRequest(name));
            flags.gamePreparationsState = Flags.GamePreparationsState.WAITING_FOR_SERVER_RESPONSE;
            return true;
        }
        return false;
    }

    public void sendInvestmentResponse(Integer shares){
        sendMessageToServer(new Network.PlayerInvestmentChoiceResponse(shares));
        flags.currentStateState = Flags.CurrentStateState.WAITING_FOR_SERVER_RESPONSE;
    }

    public void sendAbilityResponse(int ability){
        sendMessageToServer(new Network.PlayerAbilityChoiceResponse(ability));
        flags.currentStateState = Flags.CurrentStateState.WAITING_FOR_SERVER_RESPONSE;
    }

    public void sendAuctionResponse(int betAmount) {
        sendMessageToServer(new Network.PlayerAuctionChoiceResponse(betAmount));
        flags.currentStateState = Flags.CurrentStateState.WAITING_FOR_SERVER_RESPONSE;
    }

    public void sendAuctionPass() {
        sendMessageToServer(new Network.PlayerAuctionChoiceResponse());
        flags.currentStateState = Flags.CurrentStateState.WAITING_FOR_SERVER_RESPONSE;
    }

    public void sendAirlineResponse(Airline airline,  boolean isFinished){
        if(airline == null) throw new NullPointerException("Airline should bot be null. To pass call sendAirlinePass()");
        sendAirlineResponse(airline.getId(), isFinished);
    }
    public void sendAirlineResponse(int airline, boolean isFinished) {
        sendMessageToServer(new Network.PlayerAirlineChoiceResponse(airline, isFinished));
        flags.currentStateState = Flags.CurrentStateState.WAITING_FOR_SERVER_RESPONSE;
    }
    public void sendAirlinePass(){
        sendMessageToServer(new Network.PlayerAirlineChoiceResponse());
        flags.currentStateState = Flags.CurrentStateState.WAITING_FOR_SERVER_RESPONSE;
    }

    public void sendPlaneResponse(PlaneType plane, boolean isFinished){
        if(plane == null) throw new NullPointerException("Plane should bot be null. To pass call sendPlanePass()");
        sendPlaneResponse(plane.getId(), isFinished);
    }
    public void sendPlaneResponse(int plane,  boolean isFinished) {
        sendMessageToServer(new Network.PlayerPlaneChoiceResponse(plane, isFinished));
        flags.currentStateState = Flags.CurrentStateState.WAITING_FOR_SERVER_RESPONSE;
    }
    public void sendPlanePass(){
        sendMessageToServer(new Network.PlayerPlaneChoiceResponse());
        flags.currentStateState = Flags.CurrentStateState.WAITING_FOR_SERVER_RESPONSE;
    }

    public int getMyId(){
        return myId;
    }

    public boolean isConnected(){
        return serverCon!=null && serverCon.isConnected();
    }
    //------------------------------------- for use from MainClient
}
