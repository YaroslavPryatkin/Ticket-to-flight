package com.game.Ticket_To_Flight.backend;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryonet.Connection;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Player;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.AirlineType;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.AirportType;
import com.game.Ticket_To_Flight.backend.server.GameServer;
import com.game.Ticket_To_Flight.backend.server.MainLoopBack;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.commonFrontAndBack.LowLevelHandler;
import com.game.Ticket_To_Flight.network.Network;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LowLevelHandlerBack extends LowLevelHandler {
    private final GameServer gameClient = new GameServer(this);
    private final MainLoopBack logic;
    public class Flags {
        public enum GamePreparationsState {
            WAITING_FOR_PLAYERS,
            RUNNING
        }
        public volatile GamePreparationsState gamePreparationsState = GamePreparationsState.WAITING_FOR_PLAYERS;
    }

    public LowLevelHandlerBack.Flags flags = new LowLevelHandlerBack.Flags();

    private final Map<Integer, Connection> players = new HashMap<>();

    private final Queue<Connection> playersToAdd = new ConcurrentLinkedQueue<>();
    private final Map<Connection, GameData.PlayerDTO> playersBeforeGame = new HashMap<>();
    private final Set<String> chosenNames = new HashSet<>();

    private GameData.DataChanges dataChanges = new GameData.DataChanges();

    public LowLevelHandlerBack(GameData data,  MainLoopBack logic){super(data); this.logic = logic;}


    //------------------------------------- messages part

    @Override
    public void handleNewConnection(Connection con){
            playersToAdd.add(con);
    }

    @Override
    protected void handleIncomingMessage(Connection con, Network.GameMessage message){
        if(message instanceof Network.JoinGameRequest) {
            Network.JoinGameRequest req = (Network.JoinGameRequest) message;
            handleJoinGameRequest(con, req.playerName);
        }
        else if(message instanceof Network.PlayerInvestmentChoiceResponse){

        }
        else if(message instanceof Network.PlayerAbilityChoiceResponse){

        }
        else throw new IllegalArgumentException("Unknown message");
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

        if(flags.gamePreparationsState == Flags.GamePreparationsState.WAITING_FOR_PLAYERS) {
            addAllPayersToAdd();
        }
        handleAllIncomingMessages();
        sendAllWaitingMessages();

        return true;
    }
    //------------------------------------- update part

    //------------------------------------- starting the game

    private void handleJoinGameRequest(Connection con, String playerName){
        if (chosenNames.contains(playerName))
            addMessage(con, new Network.JoinGameResponse(
                Network.JoinGameResponse.Response.NAME_ALREADY_EXISTS, null));
        else{
            chosenNames.add(playerName);
            GameData.PlayerDTO dto = new GameData.PlayerDTO(playerName);
            playersBeforeGame.put(con, dto);
            addMessage(con, new Network.JoinGameResponse(
                Network.JoinGameResponse.Response.SUCCESS, dto.getId()));
        }
    }

    private void addAllPayersToAdd(){
        while(!playersToAdd.isEmpty()){
            System.out.println("Adding player");
            playersBeforeGame.putIfAbsent(playersToAdd.poll(), null);
        }
    }

    public boolean areAllPlayersReadyToStart(){
        Iterator<Map.Entry<Connection, GameData.PlayerDTO>> iterator = playersBeforeGame.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<Connection, GameData.PlayerDTO> entry = iterator.next();
            Connection con = entry.getKey();

            if (!con.isConnected()) {
                System.out.println("removed a player due to con.isConnected() == false");
                iterator.remove();
            }
        }

        //System.out.println("Checking if players are ready. Amount of players == " + playersBeforeGame.size());

        if (playersBeforeGame.isEmpty()) return false;

        for (GameData.PlayerDTO player : playersBeforeGame.values()) {
            if (player == null) {
                return false;
            }
        }

        return true;
    }

    public void startGame(){
        if(dataChanges.playersToAdd == null) {
            dataChanges.playersToAdd = new HashSet<>();
        }
        for(Map.Entry<Connection, GameData.PlayerDTO> e : playersBeforeGame.entrySet()){
            if(e.getKey().isConnected()) {
                players.put(e.getValue().getId(), e.getKey());
                dataChanges.playersToAdd.add(e.getValue());
            }
        }
        applyAndSendDataChanges();
        sendToAllPlayers(new Network.StartGameMessage());
        flags.gamePreparationsState = Flags.GamePreparationsState.RUNNING;
    }

    //------------------------------------- starting the game

    //------------------------------------- for use in main logic

    public boolean applyAndSendDataChanges(){
        boolean res = true;
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
        this.dataChanges = new GameData.DataChanges();
        return res;
    }

    public boolean addAirport(int id, AirportType type, Vector2 position, String airportName){
        return addAirport(id, type.getId(), position, airportName);
    }

    public boolean addAirport(int id, Integer type, Vector2 position, String airportName){
        if(dataChanges.airportsToAdd == null){
            dataChanges.airportsToAdd = new HashSet<>();
        }
        return dataChanges.airportsToAdd.add(new GameData.AirportDTO(id, type, position, airportName));
    }

    public boolean addAirline(AirlineType type, Airport portA, Airport portB){
        return addAirline(type.getId(), portA.getId(), portB.getId());
    }

    public boolean addAirline(Integer type, Integer portA, Integer portB){
        if(dataChanges.airlinesToAdd == null){
            dataChanges.airlinesToAdd = new HashSet<>();
        }
        if(dataChanges.availableAirlinesToAdd == null){
            dataChanges.availableAirlinesToAdd = new HashSet<>();
        }
        GameData.AirlineDTO newLine = new GameData.AirlineDTO( type,  portA,  portB);
        if(dataChanges.airlinesToAdd.add(newLine)){
            dataChanges.availableAirlinesToAdd.add(newLine.getId());
            return true;
        }
        return false;
    }

    public boolean sellAirlineToThePlayer(Integer line, Integer player){
        if(gameData.availableAirlines.contains(line)){
            if(dataChanges.availableAirlinesToRemove == null){
                dataChanges.availableAirlinesToRemove = new HashSet<>();
            }
            dataChanges.availableAirlinesToRemove.add(line);
            if(dataChanges.playerAirlinesToAdd == null){
                dataChanges.playerAirlinesToAdd = new HashMap<>();
            }
            dataChanges.playerAirlinesToAdd.computeIfAbsent(player, k -> new HashSet<>());
            return dataChanges.playerAirlinesToAdd.get(player).add(line);
        }
        return false;
    }

    public void addPassengers (Integer airport, Integer type, Integer amount){
        if(dataChanges.airportPassengersToAdd == null){
            dataChanges.airportPassengersToAdd = new HashMap<>();
        }
        dataChanges.airportPassengersToAdd.computeIfAbsent(airport, k -> new HashMap<>());
        dataChanges.airportPassengersToAdd.get(airport)
            .compute(type, (k,v)-> v == null ? amount : v + amount);

    }

    public void removePassengers (Integer airport, Integer type, Integer amount){
        if(dataChanges.airportPassengersToRemove == null){
            dataChanges.airportPassengersToRemove = new HashMap<>();
        }
        dataChanges.airportPassengersToRemove.computeIfAbsent(airport, k -> new HashMap<>());
        dataChanges.airportPassengersToRemove.get(airport)
            .compute(type, (k,v)-> v == null ? amount : v + amount);

    }

    public void setCurrentPLayer(Player pl){
        setCurrentPLayer(pl.getId());
    }
    public void setCurrentPLayer(Integer pl){
        dataChanges.currentPlayer = pl;
    }

    public void setCurrentState(GameData.State gameState){
        dataChanges.currentState = gameState;
    }
    //------------------------------------- for use in main logic
}
