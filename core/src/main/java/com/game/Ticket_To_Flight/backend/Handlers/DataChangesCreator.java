package com.game.Ticket_To_Flight.backend.Handlers;

import com.badlogic.gdx.math.Vector2;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Player;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.AirlineType;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.AirportType;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;

import java.util.HashMap;
import java.util.HashSet;

public class DataChangesCreator {
    private GameData.DataChanges dataChanges = new GameData.DataChanges();
    private final GameData gameData;

    public DataChangesCreator(GameData gameData) {this.gameData = gameData;}

    public GameData.DataChanges takeDataChanges(){
        GameData.DataChanges res = this.dataChanges;
        this.dataChanges = new GameData.DataChanges();
        return res;
    }

    public Integer addPlayer(String name){
        if(dataChanges.playersToAdd == null)
            dataChanges.playersToAdd = new HashSet<>();
        GameData.PlayerDTO dto = new GameData.PlayerDTO(name, ColorSupplier.getColor());
        dataChanges.playersToAdd.add(dto);
        return dto.getId();
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

    public void addAmountOfShares (Integer amountOfShares){
        if(dataChanges.playerAmountOfSharesChange == null)
            dataChanges.playerAmountOfSharesChange = new HashMap<>();
        dataChanges.playerAmountOfSharesChange
            .compute(gameData.currentPlayer, (k,v)->(v==null) ? amountOfShares : amountOfShares+v);
    }

    public void setCurrentPLayer(Player pl){
        if(pl == null) setCurrentPLayer((Integer)null);
        else setCurrentPLayer(pl.getId());
    }

    public void setCurrentPLayer(Integer pl){
        dataChanges.currentPlayer = pl;
    }

    public void setCurrentState(GameData.State gameState){
        dataChanges.currentState = gameState;
    }

    public void setCurrentRound(Integer roundNumber){
        dataChanges.roundNumber = roundNumber;
    }
}
