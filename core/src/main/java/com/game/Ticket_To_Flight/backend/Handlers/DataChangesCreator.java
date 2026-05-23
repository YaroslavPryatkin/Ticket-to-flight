package com.game.Ticket_To_Flight.backend.Handlers;

import com.badlogic.gdx.math.Vector2;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Player;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.AbilityType;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.AirlineType;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.AirportType;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.commonFrontAndBack.StaticGameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

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

    public void addAirport(int id, Integer type, Integer x, Integer y, String airportName){
        addAirport(id, type, new Vector2(x,y), airportName);
    }

    public void addAirport(int id, Integer type, Vector2 position, String airportName){
        if(dataChanges.airportsToAdd == null){
            dataChanges.airportsToAdd = new HashSet<>();
        }
        dataChanges.airportsToAdd.add(new GameData.AirportDTO(id, type, position, airportName));
    }


    public void addAirline(Integer type, Integer portA, Integer portB){
        if(dataChanges.airlinesToAdd == null){
            dataChanges.airlinesToAdd = new HashSet<>();
        }
        if(dataChanges.availableAirlinesToAdd == null){
            dataChanges.availableAirlinesToAdd = new HashSet<>();
        }
        GameData.AirlineDTO newLine = new GameData.AirlineDTO( type,  portA,  portB);
        dataChanges.airlinesToAdd.add(newLine);
        dataChanges.availableAirlinesToAdd.add(newLine.getId());
    }

    public boolean sellAirline(Integer line){
        if(gameData.availableAirlines.contains(line)){
            if(dataChanges.availableAirlinesToRemove == null){
                dataChanges.availableAirlinesToRemove = new HashSet<>();
            }
            dataChanges.availableAirlinesToRemove.add(line);
            if(dataChanges.playerAirlinesToAdd == null){
                dataChanges.playerAirlinesToAdd = new HashMap<>();
            }
            dataChanges.playerAirlinesToAdd.computeIfAbsent(gameData.currentPlayer, k -> new HashSet<>());
            return dataChanges.playerAirlinesToAdd.get(gameData.currentPlayer).add(line);
        }
        return false;
    }

    public void addPassengers (Integer airport, Integer type){
        if(dataChanges.airportPassengersToAdd == null){
            dataChanges.airportPassengersToAdd = new HashMap<>();
        }
        dataChanges.airportPassengersToAdd.computeIfAbsent(airport, k -> new HashMap<>());
        dataChanges.airportPassengersToAdd.get(airport)
            .compute(type, (k,v)-> v == null ? 1 : v + 1);

    }

    public void removePassengers (Integer airport, Integer type, Integer amount){
        if(dataChanges.airportPassengersToRemove == null){
            dataChanges.airportPassengersToRemove = new HashMap<>();
        }
        dataChanges.airportPassengersToRemove.computeIfAbsent(airport, k -> new HashMap<>());
        dataChanges.airportPassengersToRemove.get(airport)
            .compute(type, (k,v)-> v == null ? amount : v + amount);

    }

    public void addAvailablePlanes(Integer type, Integer amount){
        if(dataChanges.availablePlanesToAdd == null) dataChanges.availablePlanesToAdd = new HashMap<>();
        dataChanges.availablePlanesToAdd.compute(type, (k,v) -> (v==null) ? amount : v + amount );
    }

    public boolean sellPlane(Integer plane){
        if(gameData.availablePlanes.containsKey(plane)) {
            if (dataChanges.availablePlanesToRemove == null) dataChanges.availablePlanesToRemove = new HashMap<>();
            if (dataChanges.playerPlanesToAdd == null) dataChanges.playerPlanesToAdd = new HashMap<>();
            dataChanges.playerPlanesToAdd.computeIfAbsent(gameData.currentPlayer, k -> new HashMap<>());
            dataChanges.playerPlanesToAdd.get(gameData.currentPlayer)
                .compute(plane, (k,v)->(v==null) ? 1 : v + 1);
            return true;
        }
        return false;
    }

    public void addAmountOfShares (Integer amountOfShares){
        if(dataChanges.playerAmountOfSharesChange == null)
            dataChanges.playerAmountOfSharesChange = new HashMap<>();
        if(dataChanges.playerMoneyChange == null)
            dataChanges.playerMoneyChange = new HashMap<>();
        if(dataChanges.playerIncomeChange == null)
            dataChanges.playerIncomeChange = new HashMap<>();

        dataChanges.playerAmountOfSharesChange
            .compute(gameData.currentPlayer, (k,v)->(v==null) ?
                amountOfShares : amountOfShares+v);
        dataChanges.playerIncomeChange
            .compute(gameData.currentPlayer, (k,v)->(v==null) ?
                -amountOfShares * StaticGameData.minusIncomePerShare :
                v - amountOfShares * StaticGameData.minusIncomePerShare);
        dataChanges.playerMoneyChange
            .compute(gameData.currentPlayer, (k,v)->(v==null) ?
                amountOfShares * StaticGameData.plusMoneyPerShare :
                v + amountOfShares * StaticGameData.plusMoneyPerShare);
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

    public void setTurnOrder(List<Integer> newTurnOrder){
        if(dataChanges.turnOrder == null) dataChanges.turnOrder = new ArrayList<>(gameData.players.size());
        else dataChanges.turnOrder.clear();
        dataChanges.turnOrder.addAll(newTurnOrder);
    }

    public void setDefaultTurnOrder(){
        if(dataChanges.turnOrder == null) dataChanges.turnOrder = new ArrayList<>(gameData.players.size());
        else dataChanges.turnOrder.clear();
        for(Player pl : gameData.players){
            dataChanges.turnOrder.add(pl.getId());
        }
    }

    public void addHasPassed(){
        if(dataChanges.playerHasPassedSet == null) dataChanges.playerHasPassedSet = new HashMap<>();
        dataChanges.playerHasPassedSet.put(gameData.currentPlayer, true);
    }

    public void removeAllPassed(){
        if(dataChanges.playerHasPassedSet == null) dataChanges.playerHasPassedSet = new HashMap<>();
        for(Player pl : gameData.players){
            dataChanges.playerHasPassedSet.put(pl.getId(), false);
        }
    }

    public void playerMakeBet(Integer player, Integer playerBetChange){
        if(dataChanges.playerAuctionBetChanges == null) dataChanges.playerAuctionBetChanges = new HashMap<>();
        if(dataChanges.playerMoneyChange == null) dataChanges.playerMoneyChange = new HashMap<>();
        dataChanges.playerMoneyChange.compute(player,
            (k, v) -> (v==null) ? -playerBetChange : v - playerBetChange);
        dataChanges.playerAuctionBetChanges.compute(player,
            (k, v) -> (v==null) ? playerBetChange : v + playerBetChange);

    }

    public void returnBetPercent(Integer player, Double percentage){
        if(dataChanges.playerAuctionBetChanges == null) dataChanges.playerAuctionBetChanges = new HashMap<>();
        if(dataChanges.playerMoneyChange == null) dataChanges.playerMoneyChange = new HashMap<>();

        dataChanges.playerMoneyChange.compute(player,
            (k, v) ->  {
            Integer amountOfMoney = (int) Math.floor(gameData.players.get(k).auctionBet * percentage);
            return (v==null) ? amountOfMoney : v + amountOfMoney;
            });
        dataChanges.playerAuctionBetChanges.compute(player,
            (k, v) ->  -gameData.players.get(k).auctionBet);
    }

    public void setCurrentBet(Integer newCurrentBet){
        dataChanges.currentBet = newCurrentBet;
    }

    public void resetCurrentBet(){
        dataChanges.currentBet = 0;
    }

    public void giveAbility(Integer ability){
        if(dataChanges.availableAbilitiesToRemove == null) dataChanges.availableAbilitiesToRemove = new HashSet<>();
        if(dataChanges.playerAbilityChoice == null) dataChanges.playerAbilityChoice = new HashMap<>();
        dataChanges.availableAbilitiesToRemove.add(ability);
        dataChanges.playerAbilityChoice.put(gameData.currentPlayer, ability);
    }

    public void resetAllAbilities(){
        if(dataChanges.availableAbilitiesToAdd == null) dataChanges.availableAbilitiesToAdd = new HashSet<>();
        if(dataChanges.playerAbilityChoice == null) dataChanges.playerAbilityChoice = new HashMap<>();
        for(Player pl : gameData.players){
            dataChanges.playerAbilityChoice.put(pl.getId(), 0);
        }
        for(AbilityType type : StaticGameData.abilityTypes){
            if(type.getId() != 0){
                if(!gameData.availableAbilities.contains(type))
                    dataChanges.availableAbilitiesToAdd.add(type.getId());
            }
        }
    }

    public void takeActionPoint(){
        if(dataChanges.playerActionPointsChange == null) dataChanges.playerActionPointsChange = new HashMap<>();
        dataChanges.playerActionPointsChange.compute(gameData.currentPlayer,
            (k,v) -> (v==null) ? -1 : v-1);
    }

    public void resetActionPoints(){
        if(dataChanges.playerActionPointsChange == null) dataChanges.playerActionPointsChange = new HashMap<>();
        for(Player pl : gameData.players){
            dataChanges.playerActionPointsChange.compute(pl.getId(),
                (k,v) -> StaticGameData.maxActionsPerTurn - pl.actionPoints );
        }
    }

    public void moneyChange(int moneyChange){
        if(dataChanges.playerMoneyChange == null) dataChanges.playerMoneyChange=new HashMap<>();
        dataChanges.playerMoneyChange.compute(gameData.currentPlayer, (k,v) -> (v==null) ?
            moneyChange : v + moneyChange);
    }

    public void incomeChange(int incomeChange){
        if(dataChanges.playerIncomeChange == null) dataChanges.playerIncomeChange=new HashMap<>();
        dataChanges.playerIncomeChange.compute(gameData.currentPlayer, (k,v) -> (v==null) ?
            incomeChange : v + incomeChange);
    }
}
