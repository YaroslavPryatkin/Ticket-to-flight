package com.game.Ticket_To_Flight.backend.Handlers;

import com.badlogic.gdx.math.Vector2;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airline;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Player;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.AbilityType;
import com.game.Ticket_To_Flight.commonFrontAndBack.DTO.AirlineDTO;
import com.game.Ticket_To_Flight.commonFrontAndBack.DTO.AirportDTO;
import com.game.Ticket_To_Flight.commonFrontAndBack.DTO.PlayerDTO;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.commonFrontAndBack.StaticGameData;

import java.util.*;

public class DataChangesCreator {
    private GameData.DataChanges dataChanges = new GameData.DataChanges();
    private final GameData gameData;

    DataChangesCreator(GameData gameData) {this.gameData = gameData;}

    GameData.DataChanges takeDataChanges(){
        GameData.DataChanges res = this.dataChanges;
        this.dataChanges = new GameData.DataChanges();
        return res;
    }

    Integer addPlayer(String name){
        if(dataChanges.playersToAdd == null)
            dataChanges.playersToAdd = new HashSet<>();
        PlayerDTO dto = new PlayerDTO(name, ColorSupplier.getColor());
        dataChanges.playersToAdd.add(dto);
        return dto.getId();
    }

    void removeAllAirlinesFromThePlayer(int id){
        Player pl  = gameData.players.get(id);
        if(pl.airlines != null && !pl.airlines.isEmpty()) {
            if(dataChanges.playerAirlinesToRemove == null) dataChanges.playerAirlinesToRemove = new HashMap<>();
            dataChanges.playerAirlinesToRemove.putIfAbsent(id, new HashSet<>());
            Set<Integer> target = dataChanges.playerAirlinesToRemove.get(id);
            for (Airline line : pl.airlines) {
                target.add(line.getId());
            }
        }
    }

    Integer removePlayerAndGetTurnInd(int id){
        Integer indexInTurnOrder = null;
        List<Integer> newTurnOrder = null;
        if(dataChanges.turnOrder != null){
            newTurnOrder = new ArrayList<>();
            for(int i=0;i<dataChanges.turnOrder.size();++i){
                int curId = dataChanges.turnOrder.get(i);
                if( curId == id) indexInTurnOrder = i;
                else newTurnOrder.add(curId);
            }
        }
        else if(gameData.turnOrder != null){
            newTurnOrder = new ArrayList<>();
            for(int i=0;i<gameData.turnOrder.size();++i){
                int curId = gameData.turnOrder.get(i).getId();
                if( curId == id) indexInTurnOrder = i;
                else newTurnOrder.add(curId);
            }
        }
        dataChanges.turnOrder = newTurnOrder;
        if(dataChanges.playersToRemove == null) dataChanges.playersToRemove = new HashSet<>();
        dataChanges.playersToRemove.add(id);
        return indexInTurnOrder;
    }

    void addAirport(int id, Integer type, Integer x, Integer y, String airportName){
        addAirport(id, type, new Vector2(x,y), airportName);
    }

    void addAirport(int id, Integer type, Vector2 position, String airportName){
        if(dataChanges.airportsToAdd == null){
            dataChanges.airportsToAdd = new HashSet<>();
        }
        dataChanges.airportsToAdd.add(new AirportDTO(id, type, position, airportName));
    }


    void addAirline(Integer type, Integer portA, Integer portB){
        if(dataChanges.airlinesToAdd == null){
            dataChanges.airlinesToAdd = new HashSet<>();
        }
        if(dataChanges.availableAirlinesToAdd == null){
            dataChanges.availableAirlinesToAdd = new HashSet<>();
        }
        AirlineDTO newLine = new AirlineDTO( type,  portA,  portB);
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

    public boolean takeGates(Integer port, int amount){
        if(gameData.airports.contains(port)){
            if(dataChanges.airportTakenGateChange == null)
                dataChanges.airportTakenGateChange = new HashMap<>();
            dataChanges.airportTakenGateChange.compute(port, (k,v)-> v == null ? amount : v + amount);
            return true;
        }
        return false;
    }

    void addPassenger(Integer airport, Integer type){
        if(dataChanges.airportPassengersChange == null){
            dataChanges.airportPassengersChange = new HashMap<>();
        }
        dataChanges.airportPassengersChange.computeIfAbsent(airport, k -> new HashMap<>());
        dataChanges.airportPassengersChange.get(airport)
            .compute(type, (k,v)-> v == null ? 1 : v + 1);

    }

    public void removePassenger (Integer airport, Integer type){
        if(dataChanges.airportPassengersChange == null){
            dataChanges.airportPassengersChange = new HashMap<>();
        }
        dataChanges.airportPassengersChange.computeIfAbsent(airport, k -> new HashMap<>());
        dataChanges.airportPassengersChange.get(airport)
            .compute(type, (k,v)-> v == null ? -1 : v - 1);

    }

    void addAvailablePlanes(Integer type, Integer amount){
        if(dataChanges.availablePlanesChange == null) dataChanges.availablePlanesChange = new HashMap<>();
        dataChanges.availablePlanesChange.compute(type, (k,v) -> (v==null) ? amount : v + amount );
    }

    public boolean sellPlane(Integer plane){
        if(gameData.availablePlanes.containsKey(plane)) {
            if (dataChanges.availablePlanesChange == null) dataChanges.availablePlanesChange = new HashMap<>();
            if (dataChanges.playerPlanesToAdd == null) dataChanges.playerPlanesToAdd = new HashMap<>();
            dataChanges.availablePlanesChange.compute(plane, (k,v) -> v==null ? -1 : v-1);
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

    void setCurrentPLayer(Integer pl){
        dataChanges.currentPlayer = pl;
    }

    void setCurrentState(GameData.State gameState){
        dataChanges.currentState = gameState;
    }

    void setCurrentRound(Integer roundNumber){
        dataChanges.roundNumber = roundNumber;
    }

    void setTurnOrder(List<Integer> newTurnOrder){
        if(dataChanges.turnOrder == null) dataChanges.turnOrder = new ArrayList<>(gameData.players.size());
        else dataChanges.turnOrder.clear();
        dataChanges.turnOrder.addAll(newTurnOrder);
    }

    void setDefaultTurnOrder(){
        if(dataChanges.turnOrder == null) dataChanges.turnOrder = new ArrayList<>(gameData.players.size());
        else dataChanges.turnOrder.clear();
        for(Player pl : gameData.players){
            dataChanges.turnOrder.add(pl.getId());
        }
    }

    void addHasPassed(){
        if(dataChanges.playerHasPassedSet == null) dataChanges.playerHasPassedSet = new HashMap<>();
        dataChanges.playerHasPassedSet.put(gameData.currentPlayer, true);
    }

    void removeAllPassed(){
        if(dataChanges.playerHasPassedSet == null) dataChanges.playerHasPassedSet = new HashMap<>();
        for(Player pl : gameData.players){
            dataChanges.playerHasPassedSet.put(pl.getId(), false);
        }
    }

    void playerMakeBet(Integer player, Integer playerBetChange){
        if(dataChanges.playerAuctionBetChanges == null) dataChanges.playerAuctionBetChanges = new HashMap<>();
        if(dataChanges.playerMoneyChange == null) dataChanges.playerMoneyChange = new HashMap<>();
        dataChanges.playerMoneyChange.compute(player,
            (k, v) -> (v==null) ? -playerBetChange : v - playerBetChange);
        dataChanges.playerAuctionBetChanges.compute(player,
            (k, v) -> (v==null) ? playerBetChange : v + playerBetChange);

    }

    void returnBetPercent(Integer player, Double percentage){
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

    void setCurrentBet(Integer newCurrentBet){
        dataChanges.currentBet = newCurrentBet;
    }

    void resetCurrentBet(){
        dataChanges.currentBet = 0;
    }

    public void giveAbility(Integer ability){
        if(dataChanges.availableAbilitiesToRemove == null) dataChanges.availableAbilitiesToRemove = new HashSet<>();
        if(dataChanges.playerAbilityChoice == null) dataChanges.playerAbilityChoice = new HashMap<>();
        dataChanges.availableAbilitiesToRemove.add(ability);
        dataChanges.playerAbilityChoice.put(gameData.currentPlayer, ability);
    }

    void resetAllAbilities(){
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

    void resetActionPoints(){
        if(dataChanges.playerActionPointsChange == null) dataChanges.playerActionPointsChange = new HashMap<>();
        for(Player pl : gameData.players){
            dataChanges.playerActionPointsChange.compute(pl.getId(),
                (k,v) -> StaticGameData.maxActionsPerTurn - pl.actionPoints );
        }
    }

    public void moneyGain(int moneyGain){
        if(dataChanges.playerMoneyChange == null) dataChanges.playerMoneyChange=new HashMap<>();
        dataChanges.playerMoneyChange.compute(gameData.currentPlayer, (k,v) -> (v==null) ?
            moneyGain : v + moneyGain);
    }

    public void moneyLoss(int moneyLoss){
        if(dataChanges.playerMoneyChange == null) dataChanges.playerMoneyChange=new HashMap<>();
        dataChanges.playerMoneyChange.compute(gameData.currentPlayer, (k,v) -> (v==null) ?
            -moneyLoss : v - moneyLoss);
    }

    public void incomeGain(int incomeGain){
        if(dataChanges.playerIncomeChange == null) dataChanges.playerIncomeChange=new HashMap<>();
        dataChanges.playerIncomeChange.compute(gameData.currentPlayer, (k,v) -> (v==null) ?
            incomeGain : v + incomeGain);
    }

    public void incomeLoss(int incomeLoss){
        if(dataChanges.playerIncomeChange == null) dataChanges.playerIncomeChange=new HashMap<>();
        dataChanges.playerIncomeChange.compute(gameData.currentPlayer, (k,v) -> (v==null) ?
            -incomeLoss : v - incomeLoss);
    }

    public void addIncomeToMoneyForEveryPlayer(){
        if(dataChanges.playerMoneyChange == null) dataChanges.playerMoneyChange=new HashMap<>();
        for(Player pl : gameData.players){
            dataChanges.playerMoneyChange.compute(pl.getId(), (k,v) -> (v==null) ?
                -pl.income : v - pl.income);
        }
    }

    public void takeTaxesFromIncomeForEveryPlayer(){
        if(dataChanges.playerIncomeChange == null) dataChanges.playerIncomeChange=new HashMap<>();
        for(Player pl : gameData.players){
            int toLoss = progressiveTaxationFunction(pl.income);
            dataChanges.playerIncomeChange.compute(gameData.currentPlayer, (k,v) -> (v==null) ?
                -toLoss : v - toLoss);
        }
    }

    private int progressiveTaxationFunction(int income){
        int frac = income/20;
        return frac*2;
    }


}
