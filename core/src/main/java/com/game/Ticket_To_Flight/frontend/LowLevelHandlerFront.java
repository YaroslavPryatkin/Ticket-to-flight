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
            SEARCHING_FOR_SERVER,
            READY_TO_JOIN_THE_GAME,
            WAITING_FOR_CONFIRMATION,
            RUNNING
        }
        public GamePreparationsState gamePreparationsState = GamePreparationsState.SEARCHING_FOR_SERVER;
        public Network.JoinGameResponse.Response gamePreparationResponse = null;
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
                gameData.applyChangesUnsafe(checkedChanges);

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
            GameData.DataChanges nextToValidate = changesQueue.poll();
            if (nextToValidate != null) {
                startAsyncValidation(nextToValidate);
            }
        }
    }

    private void startAsyncValidation(final GameData.DataChanges change) {
        isValidationRunning.set(true);

        validationExecutor.execute(() -> {
            boolean isValid = false;
            try {
                gameData.acquireReadLock();
                try {
                    isValid = gameData.checkChanges(change);
                } finally {
                    gameData.releaseReadLock();
                }

            } catch (Exception e) {
                dataInconsistent();
            } finally {
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
            flags.gamePreparationResponse = resp.response;
            if(resp.response == Network.JoinGameResponse.Response.SUCCESS) {
                flags.gamePreparationsState = Flags.GamePreparationsState.WAITING_FOR_CONFIRMATION;
                myId = resp.id;
            }
        }
        else if(message instanceof Network.ReloadGameDataResponse){
            //will later be reload
        }
    }

    //------------------------------------- messages part

    //------------------------------------- update part
    @Override
    public boolean update(){
        if(!serverCon.isConnected()) return false;

        handleAllIncomingMessages();
        updateChanges();
        sendAllWaitingMessages();

        return true;
    }
    //------------------------------------- update part

    //------------------------------------- for use from MainClient

    public boolean sendJoinRequest(String name){
        if(flags.gamePreparationsState == Flags.GamePreparationsState.READY_TO_JOIN_THE_GAME && serverCon.isConnected()){
            addMessage(serverCon, new Network.JoinGameRequest(name));
            return true;
        }
        return false;
    }

    public int getMyId(){
        return myId;
    }
    //------------------------------------- for use from MainClient

}
