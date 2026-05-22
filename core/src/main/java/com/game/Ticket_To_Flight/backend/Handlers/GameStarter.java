package com.game.Ticket_To_Flight.backend.Handlers;

import com.esotericsoftware.kryonet.Connection;
import com.game.Ticket_To_Flight.network.Network;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GameStarter {
    private final Queue<Connection> playersToAdd = new ConcurrentLinkedQueue<>();
    private final Map<Connection, String> playersBeforeGame = new HashMap<>();

    public void registerPlayer(Connection con){
        playersToAdd.offer(con);
    }

    public Network.JoinGameResponse handleJoinGameRequest(Connection con, String playerName){
        if (playersBeforeGame.containsValue(playerName))
            return new Network.JoinGameResponse(
                Network.JoinGameResponse.Response.NAME_ALREADY_EXISTS);
        else{
            playersBeforeGame.put(con, playerName);
            return  new Network.JoinGameResponse(
                Network.JoinGameResponse.Response.SUCCESS);
        }
    }

    public void addAllPayersToAdd(){
        while(!playersToAdd.isEmpty()){
            System.out.println("Adding player");
            playersBeforeGame.putIfAbsent(playersToAdd.poll(), null);
        }
    }

    public boolean areAllPlayersReadyToStart(){
        Iterator<Map.Entry<Connection, String>> iterator = playersBeforeGame.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<Connection, String> entry = iterator.next();
            Connection con = entry.getKey();

            if (!con.isConnected()) {
                System.out.println("removed a player due to con.isConnected() == false");
                iterator.remove();
            }
        }

        //System.out.println("Checking if players are ready. Amount of players == " + playersBeforeGame.size());

        if (playersBeforeGame.isEmpty()) return false;

        for (String player : playersBeforeGame.values()) {
            if (player == null) {
                return false;
            }
        }

        return true;
    }

    public Map<Connection, String> getPlayersBeforeGame(){return playersBeforeGame;}
}
