package com.game.Ticket_To_Flight.backend;

import com.esotericsoftware.kryonet.Connection;
import com.game.Ticket_To_Flight.backend.server.GameServer;
import com.game.Ticket_To_Flight.backend.server.MainLoopBack;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.commonFrontAndBack.LowLevelHandler;
import com.game.Ticket_To_Flight.network.Network;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LowLevelHandlerBack extends LowLevelHandler {
    private final GameServer gameClient = new GameServer(this);
    private final MainLoopBack logic;

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
        if(message instanceof Network.JoinGameRequest){
            Network.JoinGameRequest req = (Network.JoinGameRequest) message;
            if(chosenNames.contains(req.playerName))
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
    //------------------------------------- for use in main logic
}
