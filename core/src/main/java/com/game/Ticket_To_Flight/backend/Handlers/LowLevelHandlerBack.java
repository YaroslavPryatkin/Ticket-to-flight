package com.game.Ticket_To_Flight.backend.Handlers;

import com.esotericsoftware.kryonet.Connection;
import com.game.Ticket_To_Flight.backend.server.GameServer;
import com.game.Ticket_To_Flight.backend.server.MainLoopBack;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.commonFrontAndBack.LowLevelHandler;
import com.game.Ticket_To_Flight.network.Network;

import java.util.*;

public class LowLevelHandlerBack extends LowLevelHandler {
    private final GameServer gameClient = new GameServer(this);
    private final MainLoopBack logic;
    public class Flags {
        public enum GamePreparationsState {
            WAITING_FOR_PLAYERS,
            RUNNING
        }
        public volatile GamePreparationsState gamePreparationsState = GamePreparationsState.WAITING_FOR_PLAYERS;
        public enum CurrentPlayerState {
            NO_PLAYER_STAGE,
            WAITING_FOR_RESPONSE,
            ANSWERED,
            BAD_RESPONSE
        }
        public volatile CurrentPlayerState currentPlayerState = CurrentPlayerState.NO_PLAYER_STAGE;
    }

    public LowLevelHandlerBack.Flags flags = new LowLevelHandlerBack.Flags();

    private final Map<Integer, Connection> int2con = new HashMap<>();
    private final Map<Connection, Integer> con2int = new HashMap<>();

    public final DataChangesCreator dataChangesCreator = new DataChangesCreator(gameData);
    private final GameStarter gameStarter = new GameStarter();


    public LowLevelHandlerBack(GameData data,  MainLoopBack logic){super(data); this.logic = logic;}


    //------------------------------------- messages part

    @Override
    public void handleNewConnection(Connection con){
            gameStarter.registerPlayer(con);
    }

    @Override
    protected void handleIncomingMessage(Connection con, Network.GameMessage message){
        if(message instanceof Network.JoinGameRequest) {
            Network.JoinGameRequest req = (Network.JoinGameRequest) message;
            gameStarter.handleJoinGameRequest(con, req.playerName);
        }
        else if(message instanceof Network.ReloadGameDataRequest){
            if(con2int.containsKey(con)){
                GameData.DataChanges reloadDC = gameData.createDataChangesFromThis();
                addMessage(con, new Network.ReloadGameDataResponse(reloadDC));
            }
        }
        else if(message instanceof Network.PlayerInvestmentChoiceResponse){
            if(playerTurnCheck(con)) {
                flags.currentPlayerState = Flags.CurrentPlayerState.ANSWERED;
                Network.PlayerInvestmentChoiceResponse resp = (Network.PlayerInvestmentChoiceResponse) message;
                logic.handleInvestmentResponse(resp.amountOfShares);
            }
        }
        else if(message instanceof Network.PlayerAuctionChoiceResponse){
            if(playerTurnCheck(con)){
                flags.currentPlayerState = Flags.CurrentPlayerState.ANSWERED;
                Network.PlayerAuctionChoiceResponse resp = (Network.PlayerAuctionChoiceResponse) message;
                logic.handleAuctionResponse(resp.betAmount);
            }
        }
        else throw new IllegalArgumentException("Unknown message");
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

    private boolean playerTurnCheck(Connection con){
        Integer player = con2int.get(con);
        if(player == null) return false;
        if(player == gameData.currentPlayer) return true;
        addMessage(con, Network.ErrorMessage.NOT_YOUR_TURN);
        return false;
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

    public void applyAndSendDataChanges(){
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
        sendToAllPlayers(new Network.DataChangesMessage(dataChanges));
    }


    public void sendError(String error){
        flags.currentPlayerState = Flags.CurrentPlayerState.BAD_RESPONSE;
        Connection con = int2con.get(gameData.currentPlayer);
        if(con!=null){
            addMessage(con, new Network.ErrorMessage(error));
        }
    }

    //------------------------------------- for use in main logic
}
