package com.game.Ticket_To_Flight.frontend;

import com.esotericsoftware.kryonet.Connection;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airline;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PlaneType;
import com.game.Ticket_To_Flight.commonFrontAndBack.DTO.RouteDTO;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.commonFrontAndBack.LowLevelHandler;
import com.game.Ticket_To_Flight.commonFrontAndBack.RatingRecord;
import com.game.Ticket_To_Flight.commonFrontAndBack.Route;
import com.game.Ticket_To_Flight.network.Network;

import java.util.List;
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
            READY_TO_JOIN_THE_GAME,
            WAITING_FOR_SERVER_RESPONSE,
            WAITING_FOR_OTHER_PLAYERS_TO_JOIN,
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
            RECEIVED_ERROR_MESSAGE,
            SERVER_DISCONNECTED,
            GAME_FINISHED
        }
        public volatile CurrentStateState currentStateState = CurrentStateState.NOT_IN_GAME;
        public volatile String errorMessage = null;
        public volatile List<RatingRecord> gameFinishRating = null;
    }

    private Flags flags = new Flags();

    public Flags.GamePreparationsState getGamePreparationState(){
        return flags.gamePreparationsState;
    }

    public Flags.CurrentStateState getCurrentStateState(){
        return flags.currentStateState;
    }
    public Network.JoinGameResponse.Response getJoinGameResponse(){
        return flags.joinGameResponse;
    }
    public List<RatingRecord> getGameFinishRating(){return flags.gameFinishRating;}

    public void truncateJoinGameResponse(){
        flags.joinGameResponse = null;
    }

    public void setWaitingForPlayerChoiceFlag(){
        //System.out.println("Changing current state state flag to " + Flags.CurrentStateState.WAITING_FOR_PLAYER_CHOICE);
        flags.currentStateState = Flags.CurrentStateState.WAITING_FOR_PLAYER_CHOICE;
    }

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
            if(dataChanges != null) {
                gameData.applyChangesUnsafe(dataChanges);
                changeFlagDependingOnNewState(dataChanges.currentState);
                mainClient.gameDataWasUpdated();
                //System.out.println("Resetting game data");
                //showGameData();
            }
        } catch (Exception e) {
            System.err.println("Error during game data reload. Error " + e.getMessage());
            e.printStackTrace();
            dataInconsistent();
        } finally {
            gameData.releaseWriteLock();
        }
    }


    public void updateChanges() {
        if(!isConnected()){
            resetGameData(null);
            return;
        }
        if (checkedChanges != null) {
            gameData.acquireWriteLock();
            try {
                //System.out.println("Applying changes");
                gameData.applyChangesUnsafe(checkedChanges);
                changeFlagDependingOnNewState(checkedChanges.currentState);
                mainClient.gameDataWasUpdated();
                checkedChanges = null;
                //showGameData();
            }
            catch (Exception e){
                System.err.println("Error during data changing. Error " + e.getMessage());
                e.printStackTrace();
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

                isValid = gameData.checkChangesDebug(change);

                if (interruptValidator.compareAndSet(true, false)) {
                    return;
                }

                if (isValid) {
                    checkedChanges = change;
                } else {
                    System.err.println("Data changes isnt valid.");
                    dataInconsistent();
                }

            } catch (Exception e) {

                if (interruptValidator.compareAndSet(true, false)) {
                    return;
                }
                System.err.println("Error during data changes checking in separate thread. Error " + e.getMessage());
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
        if(st == null) st = gameData.currentState;
        Flags.CurrentStateState res;
        if(st == GameData.State.GAME_FINISHED)
            res = Flags.CurrentStateState.GAME_FINISHED;
        else if(st == GameData.State.WORLD_UPDATE ||
            st == GameData.State.INCOME_AND_TAXES ||
            st == GameData.State.EVENT
        )
             res = Flags.CurrentStateState.NO_PLAYER_STAGE;
        else
            res  = Flags.CurrentStateState.PLAYER_STAGE;
        //System.out.println("Changing current state state flag to " + res);
        flags.currentStateState = res;
    }

    //------------------------------------- data changes part

    private void showGameData(){
        System.out.println("------------------------------------------------\nCurrent game data:");
        System.out.println("Current state = " + gameData.currentState);
        System.out.println("Current player = " + gameData.currentPlayer);
        System.out.println("Players:");
        gameData.players.printAllToConsole();
        if(gameData.turnOrder!=null) {
            System.out.println("Current turn order:");
            for (int i = 0; i < gameData.turnOrder.size() - 1; ++i) {
                System.out.print(gameData.turnOrder.get(i).name + " -> ");
            }
            System.out.println(gameData.turnOrder.getLast().name);
        }
        else
            System.out.println("No turn order yet");
        System.out.println("Airports:");
        for(Airport airport : gameData.airports){
            System.out.println(airport.toString());
        }
       System.out.println("------------------------------------------------\n");
    }

    //------------------------------------- messages part

    @Override
    public void handleNewConnection(Connection con){
        serverCon = con;
        flags.gamePreparationsState = Flags.GamePreparationsState.READY_TO_JOIN_THE_GAME;
    }

    @Override
    public void handleDisconnection(Connection con){
        if(con.equals(serverCon)){
            serverCon = null;
            flags.currentStateState = Flags.CurrentStateState.SERVER_DISCONNECTED;
            flags.gamePreparationsState = Flags.GamePreparationsState.WAITING_FOR_CONNECT_CALL;
        }
    }

    @Override
    protected void handleIncomingMessage(Connection con, Network.GameMessage message){
        if(!con.equals(serverCon)) return;

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
            //System.out.println("game is running");
        }
        else if(message instanceof Network.ReloadGameDataResponse){
            Network.ReloadGameDataResponse resp = (Network.ReloadGameDataResponse) message;
            resetGameData(resp.dc);
        }
        else if(message instanceof Network.ErrorMessage){
            flags.currentStateState = Flags.CurrentStateState.RECEIVED_ERROR_MESSAGE;
            flags.errorMessage = ((Network.ErrorMessage) message).getMessage();
        }
        else if(message instanceof Network.GameFinishedMessage){
            flags.currentStateState = Flags.CurrentStateState.GAME_FINISHED;
            flags.gameFinishRating = ((Network.GameFinishedMessage) message).playerRating;
            System.out.println("Received game finished message");
            sendMessageToServer(new Network.GameFinishedConfirmation());
        }
        else if(message instanceof Network.BankruptcyMessage){
            mainClient.bankruptcyMessage();
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
        flags.currentStateState = Flags.CurrentStateState.NOT_IN_GAME;
        return gameClient.connect();
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

    public void sendRouteResponse(Route route, boolean isFinished){
        if(route == null) throw new NullPointerException("Route should bot be null. To pass call sendRoutePass()");
        if(!route.canFinishRoute()) throw new IllegalArgumentException("Rout should be finishable before sending it to the server");
        sendMessageToServer(new Network.PlayerRouteChoiceResponse(new RouteDTO(route), isFinished));
        flags.currentStateState = Flags.CurrentStateState.WAITING_FOR_SERVER_RESPONSE;
    }

    public void sendRoutePass(){
        sendMessageToServer(new Network.PlayerRouteChoiceResponse());
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

    public MainClient getMainClient() {
        return mainClient;
    }
    //------------------------------------- for use from MainClient
}
