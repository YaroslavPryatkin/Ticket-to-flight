package com.game.Ticket_To_Flight.backend;

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
        public GamePreparationsState gamePreparationsState = GamePreparationsState.WAITING_FOR_PLAYERS;
    }

    public LowLevelHandlerBack.Flags flags = new LowLevelHandlerBack.Flags();

    private final Map<Integer, Connection> players = new HashMap<>();

    private final Map<Connection, GameData.PlayerDTO> playersBeforeGame = new HashMap<>();
    private final Set<String> chosenNames = new HashSet<>();

    private GameData.DataChanges dataChanges = new GameData.DataChanges();

    public LowLevelHandlerBack(GameData data,  MainLoopBack logic){super(data); this.logic = logic;}


    //------------------------------------- messages part

    @Override
    public void handleNewConnection(Connection con){
            playersBeforeGame.put(con, null);
    }

    @Override
    protected void handleIncomingMessage(Connection con, Network.GameMessage message){
        if(message instanceof Network.JoinGameRequest) {
            Network.JoinGameRequest req = (Network.JoinGameRequest) message;
            if (chosenNames.contains(req.playerName))
                addMessage(con, new Network.JoinGameResponse(
                    Network.JoinGameResponse.Response.NAME_ALREADY_EXISTS, null));
            else{
                chosenNames.add(req.playerName);
                GameData.PlayerDTO dto = new GameData.PlayerDTO(req.playerName);
                playersBeforeGame.put(con, dto);
                addMessage(con, new Network.JoinGameResponse(
                    Network.JoinGameResponse.Response.SUCCESS, dto.getId()));
            }
        }
    }

    private boolean sendToAllPlayers(Network.GameMessage message) {
        boolean res = true;
        for (Connection con : players.values()) {
            if (con != null && con.isConnected()) {
                addMessage(con, message);
            }
            else
                res = false;
        }
        return res;
    }

    //------------------------------------- messages part

    //------------------------------------- update part
    @Override
    public boolean update(){

        handleAllIncomingMessages();
        sendAllWaitingMessages();

        return true;
    }
    //------------------------------------- update part

    //------------------------------------- for use in main logic
    public boolean applyAndSendDataChanges(){
        boolean res = true;
        gameData.applyChangesUnsafe(dataChanges);
        for(Connection con : players.values()){
            if(con.isConnected()) {
                addMessage(con, new Network.DataChangesMessage(dataChanges));
            }
            else{
                res = false;
            }
        }
        return res;
    }

    public boolean areAllPlayersReadyToStart(){
        Iterator<Map.Entry<Connection, GameData.PlayerDTO>> iterator = playersBeforeGame.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<Connection, GameData.PlayerDTO> entry = iterator.next();
            Connection con = entry.getKey();

            if (!con.isConnected()) {
                iterator.remove();
            }
        }

        if (playersBeforeGame.isEmpty()) return false;

        for (GameData.PlayerDTO player : playersBeforeGame.values()) {
            if (player == null) {
                return false;
            }
        }

            return true;
        }

        public void startGame(){
            for(Map.Entry<Connection, GameData.PlayerDTO> e : playersBeforeGame.entrySet()){
                players.put(e.getValue().getId(), e.getKey());
            }
            sendToAllPlayers(new Network.StartGameMessage());
            flags.gamePreparationsState = Flags.GamePreparationsState.RUNNING;
        }

    //------------------------------------- for use in main logic
}
