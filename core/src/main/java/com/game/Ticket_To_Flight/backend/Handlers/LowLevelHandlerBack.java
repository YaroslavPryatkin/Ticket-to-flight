package com.game.Ticket_To_Flight.backend.Handlers;

import com.esotericsoftware.kryonet.Connection;
import com.game.Ticket_To_Flight.backend.server.GameServer;
import com.game.Ticket_To_Flight.backend.server.MainLoopBack;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.commonFrontAndBack.LowLevelHandler;
import com.game.Ticket_To_Flight.network.Network;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class LowLevelHandlerBack extends LowLevelHandler {
    private final GameServer gameClient = new GameServer(this);
    private final MainLoopBack logic;
    public static class Flags {
        public enum GamePreparationsState {
            WAITING_FOR_PLAYERS,
            RUNNING
        }
        public volatile GamePreparationsState gamePreparationsState = GamePreparationsState.WAITING_FOR_PLAYERS;
        public enum CurrentPlayerState {
            NO_PLAYER_STAGE,
            WAITING_FOR_RESPONSE,
            ANSWERED,
            GOOD_RESPONSE,
            BAD_RESPONSE
        }
        public volatile CurrentPlayerState currentPlayerState = CurrentPlayerState.NO_PLAYER_STAGE;
    }

    private final LowLevelHandlerBack.Flags flags = new LowLevelHandlerBack.Flags();

    final Map<Integer, Connection> int2con = new ConcurrentHashMap<>();
    final Map<Connection, Integer> con2int = new ConcurrentHashMap<>();

    public final DataChangesCreator dataChangesCreator = new DataChangesCreator(gameData);
    private final GameStarter gameStarter = new GameStarter();
    private final StateIterator stateIterator = new StateIterator(gameData, dataChangesCreator, flags, this);

    public LowLevelHandlerBack(GameData data,  MainLoopBack logic){super(data); this.logic = logic;}


    //------------------------------------- messages part

    @Override
    public void handleNewConnection(Connection con){
            gameStarter.registerPlayer(con);
    }

    @Override
    public void handleDisconnection(Connection con) {
        Integer playerId = con2int.remove(con);
        int2con.remove(con);

        if (playerId == null) {
            return;
        }

        if (gameData.currentPlayer != null && gameData.currentPlayer.equals(playerId)) {
            flags.currentPlayerState = Flags.CurrentPlayerState.GOOD_RESPONSE;
            dataChangesCreator.addHasPassed();
        }

        System.out.println("Player " + playerId + " disconnected");
    }

        @Override
    protected void handleIncomingMessage(Connection con, Network.GameMessage message){
        if(message instanceof Network.JoinGameRequest) {
            Network.JoinGameRequest req = (Network.JoinGameRequest) message;
            addMessage(con ,  gameStarter.handleJoinGameRequest(con, req.playerName));
        }
        else if(message instanceof Network.ReloadGameDataRequest){
            if(con2int.containsKey(con)){
                GameData.DataChanges reloadDC = gameData.createDataChangesFromThis();
                addMessage(con, new Network.ReloadGameDataResponse(reloadDC));
            }
        }
        else{
            if(playerTurnCheck(con)){
                flags.currentPlayerState = Flags.CurrentPlayerState.ANSWERED;
                logic.handlePlayerResponse(message);
            }
        }


    }

   private boolean sendToAllPlayers(Network.GameMessage message) {
        boolean res = true;
        for (Connection con : int2con.values()) {
            if (con != null && con.isConnected()) {
                addMessage(con, message);
            }
            else
                res = false;
        }
        return res;
    }

    /**
     * Sends error "NOT YOUR TURN" if wrong turn
     * @param con connection to check
     * @return true if it is current player
     */
    private boolean playerTurnCheck(Connection con){
        Integer player = con2int.get(con);
        if(player == null) return false;
        if(!player.equals(gameData.currentPlayer)){
            addMessage(con, Network.ErrorMessage.NOT_YOUR_TURN);
            return false;
        }
        if(flags.currentPlayerState != Flags.CurrentPlayerState.WAITING_FOR_RESPONSE &&
            flags.currentPlayerState != Flags.CurrentPlayerState.BAD_RESPONSE){
            addMessage(con, Network.ErrorMessage.ALREADY_ANSWERED);
            return false;
        }

        return true;
    }



    void applyAndSendDataChanges(){
        GameData.DataChanges dataChanges = dataChangesCreator.takeDataChanges();
        gameData.applyChangesUnsafe(dataChanges);
        System.out.println("Changes were applied, current game data:");
        System.out.println("Current state = " + gameData.currentState);
        System.out.println("Current player = " + gameData.currentPlayer);
        System.out.println("Players:");
        gameData.players.printAllToConsole();
        System.out.println("Airports:");
        gameData.airports.printAllToConsole();
        System.out.println("Airlines:");
        gameData.airlines.printAllToConsole();
        System.out.println("Available Planes");
        gameData.availablePlanes.printToConsole();
        sendToAllPlayers(new Network.DataChangesMessage(dataChanges));
    }

    //------------------------------------- messages part

    //------------------------------------- update part
    @Override
    public boolean update(){

        if(flags.gamePreparationsState == Flags.GamePreparationsState.WAITING_FOR_PLAYERS) {
            gameStarter.addAllPayersToAdd();
        }
        handleAllIncomingMessages();
        sendAllWaitingMessages();

        return true;
    }
    //------------------------------------- update part

    //------------------------------------- for use in main logic

    public void beforeStartCycle(){
        if(gameStarter.areAllPlayersReadyToStart()){
            for(Map.Entry<Connection, String> e : gameStarter.getPlayersBeforeGame().entrySet()){
                if(e.getKey().isConnected()) {
                    Integer id = dataChangesCreator.addPlayer(e.getValue());
                    int2con.put(id, e.getKey());
                    con2int.put(e.getKey(), id);
                    addMessage(e.getKey(), new Network.StartGameMessage(id));
                }
            }
            applyAndSendDataChanges();
            flags.gamePreparationsState = LowLevelHandlerBack.Flags.GamePreparationsState.RUNNING;
        }
    }

    public void finishTurnSuccessfully(){
        if(!stateIterator.nextState())
            System.out.println("Game finished");
    }

    public void sendError(String error){
        flags.currentPlayerState = Flags.CurrentPlayerState.BAD_RESPONSE;
        Connection con = int2con.get(gameData.currentPlayer);
        if(con!=null){
            addMessage(con, new Network.ErrorMessage(error));
        }
    }
    public void sendWrongStateError(){
        flags.currentPlayerState = Flags.CurrentPlayerState.BAD_RESPONSE;
        Connection con = int2con.get(gameData.currentPlayer);
        if(con!=null){
            addMessage(con, Network.ErrorMessage.WRONG_STATE);
        }
    }
    public void sendUnknownMessageError(){
        flags.currentPlayerState = Flags.CurrentPlayerState.BAD_RESPONSE;
        Connection con = int2con.get(gameData.currentPlayer);
        if(con!=null){
            addMessage(con, Network.ErrorMessage.UNKNOWN_MESSAGE);
        }
    }

    public Flags.GamePreparationsState getGamePreparationState(){
        return flags.gamePreparationsState;
    }

    public Flags.CurrentPlayerState getCurrentPlayerState(){
        return flags.currentPlayerState;
    }

    public void playerFinished(){
        dataChangesCreator.addHasPassed();
        flags.currentPlayerState = Flags.CurrentPlayerState.GOOD_RESPONSE;
    }
    public void playerNotFinished(){
        flags.currentPlayerState = Flags.CurrentPlayerState.GOOD_RESPONSE;
    }

    //temporary. When route handling is added, should be deleted
    public void setWaitingForResponseFlag(){
        flags.currentPlayerState = Flags.CurrentPlayerState.WAITING_FOR_RESPONSE;
    }

    //------------------------------------- for use in main logic
}
