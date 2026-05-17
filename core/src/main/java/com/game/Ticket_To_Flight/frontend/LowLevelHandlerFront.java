package com.game.Ticket_To_Flight.frontend;

import com.esotericsoftware.kryonet.Connection;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.commonFrontAndBack.LowLevelHandler;
import com.game.Ticket_To_Flight.network.Network;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class LowLevelHandlerFront extends LowLevelHandler {
    private GameClient gameClient = new GameClient(this);
    private Connection serverCon = null;
    private Integer myId = null;

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
            NO_PLAYER_STATE,
            WAITING_FOR_PLAYER_CHOICE,
            WAITING_FOR_SERVER_RESPONSE
        }
        public volatile CurrentStateState currentStateState = CurrentStateState.NOT_IN_GAME;
    }

    public Flags flags = new Flags();

    public LowLevelHandlerFront(GameData data){super(data);}

    //------------------------------------- data changes part
    private final ExecutorService validationExecutor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "GameData-Validator-Thread");
        t.setDaemon(true);
        return t;
    });
    private volatile GameData.DataChanges checkedChanges;
    private final AtomicBoolean isValidationRunning = new AtomicBoolean(false);
    public final Queue<GameData.DataChanges> changesQueue = new ConcurrentLinkedQueue<>();

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

        validationExecutor.execute(() -> {
            boolean isValid = false;
            GameData.DataChanges change = null;
            try {
                gameData.acquireReadLock();
                change = changesQueue.poll();
                while(!changesQueue.isEmpty()) {
                    change.merge(changesQueue.poll());
                }
                isValid = gameData.checkChanges(change);

            } catch (Exception e) {
                System.out.println("checking exception " + e.toString());
                e.printStackTrace();
            } finally {
                gameData.releaseReadLock();
                isValidationRunning.set(false);
            }

            if (isValid) {
                checkedChanges = change;
            }
            else{
                dataInconsistent();
            }

        });
    }

    private void dataInconsistent(){
        checkedChanges = null;
        changesQueue.clear();
        addMessage(serverCon, new Network.ReloadGameDataRequest());
    }

    private void changeFlagDependingOnNewState(GameData.State st){
        if(st == null) return;
        if(st == GameData.State.WORLD_UPDATE ||
            st == GameData.State.INCOME||
            st == GameData.State.TAXES ||
            st == GameData.State.EVENT
        )
            flags.currentStateState = Flags.CurrentStateState.NO_PLAYER_STATE;
        else
            flags.currentStateState = Flags.CurrentStateState.WAITING_FOR_PLAYER_CHOICE;
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
                myId = resp.id;
            }
            else{
                flags.gamePreparationsState = Flags.GamePreparationsState.READY_TO_JOIN_THE_GAME;
            }
        }
        else if(message instanceof Network.StartGameMessage){
            flags.gamePreparationsState = Flags.GamePreparationsState.RUNNING;
            System.out.println("game is running");
        }
        else if(message instanceof Network.ReloadGameDataResponse){
            //will later be reload
        }
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
            addMessage(serverCon, new Network.JoinGameRequest(name));
            flags.gamePreparationsState = Flags.GamePreparationsState.WAITING_FOR_SERVER_RESPONSE;
            return true;
        }
        return false;
    }

    public void sendInvestmentResponse(Integer shares){
        addMessage(serverCon, new Network.PlayerInvestmentChoiceResponse(shares));
        flags.currentStateState = Flags.CurrentStateState.WAITING_FOR_SERVER_RESPONSE;
    }


    public int getMyId(){
        return myId;
    }

    public boolean isConnected(){
        return serverCon!=null && serverCon.isConnected();
    }
    //------------------------------------- for use from MainClient

    public void setNewMessage(Network.GameMessage gm) {
        System.out.println("Accepted investment!");
        addMessage(serverCon, gm);
    }
}
