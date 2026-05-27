package com.game.Ticket_To_Flight.commonFrontAndBack;

import com.game.Ticket_To_Flight.Utilities.SetHolder;
import com.game.Ticket_To_Flight.Utilities.Identifiable;
import com.game.Ticket_To_Flight.Utilities.MapHolder;
import com.game.Ticket_To_Flight.Utilities.TemporarySetHolder;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airline;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Player;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.*;
import com.game.Ticket_To_Flight.commonFrontAndBack.DTO.AirlineDTO;
import com.game.Ticket_To_Flight.commonFrontAndBack.DTO.AirportDTO;
import com.game.Ticket_To_Flight.commonFrontAndBack.DTO.PlayerDTO;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class GameData {
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public void acquireReadLock() {
        lock.readLock().lock();
    }

    public void releaseReadLock() {
        lock.readLock().unlock();
    }

    public void acquireWriteLock() {
        lock.writeLock().lock();
    }

    public void releaseWriteLock() {
        lock.writeLock().unlock();
    }


    public enum State {
        NO_STATE,
        WORLD_UPDATE,
        INVESTMENTS,
        AUCTION,
        ABILITIES,
        PLANES,
        AIRLINES,
        EVENT,
        FLIGHTS,
        INCOME,
        TAXES
    }
    public Integer roundNumber = 0;
    public State currentState = State.NO_STATE;
    public Integer currentPlayer = -1; // id of current player. -1 if it is no player turn
    public List<Player> turnOrder = null;
    public int currentBet = 0;

    public SetHolder<WorldEventType> worldEvents = new SetHolder<>();
    public SetHolder<Airport> airports = new SetHolder<>();
    public SetHolder<Airline> airlines = new SetHolder<>();
    public SetHolder<Player> players = new SetHolder<>();
    public SetHolder<Airline> availableAirlines = new SetHolder<>();
    public SetHolder<AbilityType> availableAbilities = new SetHolder<>();
    public MapHolder<PlaneType, Integer> availablePlanes = new MapHolder<>(StaticGameData.planeTypes);


    public static class DataChanges extends Identifiable {
        private static final AtomicInteger idGenerator = new AtomicInteger(0);

        public  Set<PlayerDTO> playersToAdd = null;
        public  Set<Integer> playersToRemove= null;
        public  Set<AirportDTO> airportsToAdd= null;
        public  Set<Integer> airportsToRemove= null;
        public  Set<AirlineDTO> airlinesToAdd= null;
        public  Set<Integer> airlinesToRemove= null;
        public  Set<Integer> availableAirlinesToAdd= null;
        public  Set<Integer> availableAirlinesToRemove= null;
        public  Set<Integer> availableAbilitiesToAdd= null;
        public  Set<Integer> availableAbilitiesToRemove= null;

        public Integer roundNumber = null;
        public  GameData.State currentState= null;
        public  Integer currentPlayer= null;
        public  Set<Integer> newWorldEvents= null;
        public List<Integer> turnOrder = null;
        public Integer currentBet = null;

        public  Map<Integer, Integer> availablePlanesChange= null;
        public  Map<Integer, Map<Integer, Integer>> airportPassengersChange= null;


        public  Map<Integer, Boolean> playerHasPassedSet = null;
        public  Map<Integer, Integer> playerMoneyChange= null;
        public  Map<Integer, Integer> playerIncomeChange= null;
        public  Map<Integer, Integer> playerActionPointsChange= null;
        public  Map<Integer, Integer> playerAmountOfSharesChange= null;
        public  Map<Integer, Integer> playerAbilityChoice = null;
        public  Map<Integer, Integer> playerAuctionBetChanges = null;

        public  Map<Integer, Set<Integer>> playerAirlinesToAdd= null;
        public  Map<Integer, Set<Integer>> playerAirlinesToRemove= null;
        public  Map<Integer, Map<Integer, Integer>> playerPlanesToAdd= null;
        public  Map<Integer, Map<Integer, Integer>> playerPlanesToRemove= null;

        public DataChanges(){super(idGenerator.incrementAndGet());}

        public DataChanges merge(DataChanges other) {
            if (other == null) return this;
            if(other.roundNumber != null) this.roundNumber = other.roundNumber;
            if (other.currentState != null) this.currentState = other.currentState;
            if (other.currentPlayer != null) this.currentPlayer = other.currentPlayer;
            if(other.turnOrder != null) this.turnOrder = other.turnOrder;
            if(other.currentBet != null) this.currentBet = other.currentBet;

            this.newWorldEvents = SetHolder.merge(this.newWorldEvents, other.newWorldEvents);
            this.availableAirlinesToAdd = SetHolder.merge(this.availableAirlinesToAdd, other.availableAirlinesToAdd);
            this.availableAirlinesToRemove = SetHolder.merge(this.availableAirlinesToRemove, other.availableAirlinesToRemove);
            this.availableAbilitiesToAdd = SetHolder.merge(this.availableAbilitiesToAdd, other.availableAbilitiesToAdd);
            this.availableAbilitiesToRemove = SetHolder.merge(this.availableAbilitiesToRemove, other.availableAbilitiesToRemove);
            this.playersToAdd = SetHolder.merge(this.playersToAdd, other.playersToAdd);
            this.playersToRemove = SetHolder.merge(this.playersToRemove, other.playersToRemove);
            this.airportsToAdd = SetHolder.merge(this.airportsToAdd, other.airportsToAdd);
            this.airportsToRemove = SetHolder.merge(this.airportsToRemove, other.airportsToRemove);
            this.airlinesToAdd = SetHolder.merge(this.airlinesToAdd, other.airlinesToAdd);
            this.airlinesToRemove = SetHolder.merge(this.airlinesToRemove, other.airlinesToRemove);

            this.airportPassengersChange = MapHolder.merge(
                this.airportPassengersChange, other.airportPassengersChange, v->v,
                (f,s)->MapHolder.merge(f,s,v->v, DataChanges::sumIntOrNull)
            );

            this.availablePlanesChange = MapHolder.merge(
                this.availablePlanesChange, other.availablePlanesChange, v -> v, DataChanges::sumIntOrNull);

            this.playerMoneyChange = MapHolder.merge(
                this.playerMoneyChange, other.playerMoneyChange, v -> v, DataChanges::sumIntOrNull);

            this.playerAuctionBetChanges = MapHolder.merge(
                this.playerAuctionBetChanges, other.playerAuctionBetChanges, v -> v, DataChanges::sumIntOrNull);

            this.playerIncomeChange = MapHolder.merge(
                this.playerIncomeChange, other.playerIncomeChange, v -> v, DataChanges::sumIntOrNull);

            this.playerHasPassedSet = MapHolder.merge(
                this.playerHasPassedSet, other.playerHasPassedSet, (f)->f, (f,s)->s);

            this.playerActionPointsChange = MapHolder.merge(
                this.playerActionPointsChange, other.playerActionPointsChange, v -> v, DataChanges::sumIntOrNull);

            this.playerAmountOfSharesChange = MapHolder.merge(
                this.playerAmountOfSharesChange, other.playerAmountOfSharesChange, v -> v, DataChanges::sumIntOrNull);

            this.playerAbilityChoice = MapHolder.merge(
                this.playerAbilityChoice, other.playerAbilityChoice, v -> v, (o, n) -> (n==null) ? o : n);

            this.playerAirlinesToAdd = MapHolder.merge(
                this.playerAirlinesToAdd, other.playerAirlinesToAdd, v->v,
                SetHolder::merge
            );

            this.playerAirlinesToRemove = MapHolder.merge(
                this.playerAirlinesToRemove, other.playerAirlinesToRemove, v->v,
                SetHolder::merge
            );

            this.playerPlanesToAdd = MapHolder.merge(
                this.playerPlanesToAdd, other.playerPlanesToAdd, v->v,
                (f,s)->MapHolder.merge(f,s,v->v, Integer::sum)
            );

            this.playerPlanesToRemove = MapHolder.merge(
                this.playerPlanesToRemove, other.playerPlanesToRemove, v->v,
                (f,s)->MapHolder.merge(f,s,v->v, Integer::sum)
            );
            return this;
        }


        private static Integer sumIntOrNull(Integer a, Integer b) {
            int valA = (a != null) ? a : 0;
            int valB = (b != null) ? b : 0;
            int res = valA + valB;
            return res == 0 ? null : res;
        }

        private static Double sumDoubleOrNull(Double a, Double b) {
            double valA = (a != null) ? a : 0;
            double valB = (b != null) ? b : 0;
            double res = valA + valB;
            return Math.abs(res) < 1e-9 ? null : res;
        }
    }

    public void applyChangesUnsafe(DataChanges changes){
        if(changes.roundNumber!=null) this.roundNumber = changes.roundNumber;
        if (changes.currentState != null) this.currentState = changes.currentState;
        if (changes.currentPlayer != null) this.currentPlayer = changes.currentPlayer;
        if (changes.currentBet != null) this.currentBet = changes.currentBet;



        worldEvents.clearAndAddAllFromLookUp(changes.newWorldEvents, StaticGameData.worldEventTypes);
        airports.changeSetDTOI(changes.airportsToAdd, changes.airportsToRemove,
            AirportDTO::restore);
        airlines.changeSetDTOI(changes.airlinesToAdd, changes.airlinesToRemove,
            (dto)->dto.restore(this.airports, this.players));
        players.changeSetDTOI(changes.playersToAdd, changes.playersToRemove,
            (dto)->dto.restore(this.airlines));
        availableAirlines.changeSetII(changes.availableAirlinesToAdd, changes.availableAirlinesToRemove, this.airlines);
        availableAirlines.retainAll(airlines);
        availableAbilities.changeSetII(changes.availableAbilitiesToAdd, changes.availableAbilitiesToRemove, StaticGameData.abilityTypes);

        if(changes.turnOrder != null){
            this.turnOrder = new ArrayList<>(players.size());
            for(Integer i : changes.turnOrder){
                this.turnOrder.add(this.players.get(i));
            }
        }

        availablePlanes.merge(changes.availablePlanesChange, v->v, DataChanges::sumIntOrNull);

        players.changeAsStructWithSetter(Player::setHasPassed, Player::getHasPassed, changes.playerHasPassedSet,
            (f,s) -> (s==null) ? f : s);

        players.changeAsStructWithSetter(Player::setAbility, Player::getAbility,
            (i)-> StaticGameData.abilityTypes.get(i), changes.playerAbilityChoice,
            (f,s)->(s==null) ? f : s);
        players.changeAsStructWithSetter(Player::setActionPoints, Player::getActionPoints,
            changes.playerActionPointsChange, Integer::sum);
        players.changeAsStructWithSetter(Player::setAmountOfShares, Player::getAmountOfShares,
            changes.playerAmountOfSharesChange, Integer::sum);
        players.changeAsStructWithSetter(Player::setIncome, Player::getIncome,
            changes.playerIncomeChange, Integer::sum);
        players.changeAsStructWithSetter(Player::setMoney, Player::getMoney,
            changes.playerMoneyChange, Integer::sum);
        players.changeAsStructWithSetter(Player::setAuctionBet, Player::getAuctionBet,
            changes.playerAuctionBetChanges, Integer::sum);

        players.changeAsStruct((pl) -> pl,
            Arrays.asList(changes.playerAirlinesToAdd, changes.playerAirlinesToRemove),
            (cur, params)-> {
            cur.airlines.changeSetII(params.get(0), params.get(1), airlines,
                (line)->line.player=null,
                (line)->{line.player = cur; return line;});
            cur.airlines.retainAll(airlines);
        });
        players.changeAsStruct((pl) -> pl.planes,
            Arrays.asList(changes.playerPlanesToAdd, changes.playerPlanesToRemove),
            (f, s)-> f.merge(s,
                (params)-> {int res = params.get(0) - params.get(1); return res == 0 ? null : res;},
                (old, params)->{int res = old+params.get(0) - params.get(1); return res == 0 ? null : res;},
                (i)->0
            )
        );

        airports.changeAsStruct(pl->pl.passengers, changes.airportPassengersChange,
            (f,s)->f.merge(s, v->v, DataChanges::sumIntOrNull));

    }

    public boolean checkChanges(DataChanges changes){
        if( !StaticGameData.worldEventTypes.containsAll(changes.newWorldEvents) ||
            !players.checkChangeSetTI(changes.playersToAdd, changes.playersToRemove) ||
            !airports.checkChangeSetTI(changes.airportsToAdd, changes.airportsToRemove) ||
            !airlines.checkChangeSetTI(changes.airlinesToAdd, changes.airlinesToRemove)
        ) return false;

        SetHolder<Airport> airportsTmp = TemporarySetHolder.generateTemporarySetHolder(
          airports, changes.airportsToAdd, changes.airportsToRemove,
            AirportDTO::restore);
        SetHolder<Airline> airlinesTmp = TemporarySetHolder.generateTemporarySetHolder(
            airlines, changes.airlinesToAdd, changes.airlinesToRemove,
            (dto)->dto.restore(airportsTmp, this.players));
        SetHolder<Player> playersTmp = TemporarySetHolder.generateTemporarySetHolder(
            players, changes.playersToAdd, changes.playersToRemove,
            (dto)->dto.restore(airlinesTmp));

        if (changes.currentPlayer != null && changes.currentPlayer != -1 && !playersTmp.contains(changes.currentPlayer)) return false;
        if(!playersTmp.containsAll(changes.turnOrder)) return false;

        return availableAirlines.checkChangeSetIILookUp(
            changes.availableAirlinesToAdd, changes.availableAirlinesToRemove, airlinesTmp) &&
            availableAbilities.checkChangeSetIILookUp(
                changes.availableAbilitiesToAdd, changes.availableAbilitiesToRemove, StaticGameData.abilityTypes) &&
            StaticGameData.planeTypes.containsAllKeys(changes.availablePlanesChange) &&
            availablePlanes.checkMergeElements(
                changes.availablePlanesChange,
                v->v>=0,
                (f,s)->f+s>=0
            ) &&
            playersTmp.containsAllKeys(changes.playerHasPassedSet) &&
            playersTmp.checkChangeAsStruct(Player::getMoney, changes.playerMoneyChange,
                (current, change) -> current + change >= 0) &&
            playersTmp.checkChangeAsStruct(Player::getAuctionBet, changes.playerAuctionBetChanges,
                (current, change) -> current + change >= 0) &&
            playersTmp.checkChangeAsStruct(Player::getAmountOfShares, changes.playerAmountOfSharesChange,
                (o, n) -> o + n >= 0 && o + n <= StaticGameData.maxAmountOfShares) &&
            playersTmp.checkChangeAsStruct(Player::getActionPoints, changes.playerActionPointsChange,
                (o, n) -> o + n >= 0 && o + n <= StaticGameData.maxActionsPerTurn) &&
            playersTmp.checkChangeAsStruct((pl) -> pl.airlines,
                Arrays.asList(changes.playerAirlinesToAdd, changes.playerAirlinesToRemove),
                (f, s) -> f.checkChangeSetIILookUp(s.get(0), s.get(1), airlinesTmp)
            ) &&
            playersTmp.containsAllKeys(changes.playerAbilityChoice) &&
            StaticGameData.abilityTypes.containsAllValues(changes.playerAbilityChoice) &&
            playersTmp.checkChangeAsStruct((pl) -> pl.planes,
                Arrays.asList(changes.playerPlanesToAdd, changes.playerPlanesToRemove),
                (f, s) -> f.checkMergeElements(s,
                    (params) -> params.get(0) - params.get(1) >= 0,
                    (old, params) -> old + params.get(0) - params.get(1) >= 0,
                    (i) -> 0
                ) && StaticGameData.planeTypes.containsAll(s.get(0).keySet())
            ) &&
            airportsTmp.checkChangeAsStruct((port) -> port.passengers,
                changes.airportPassengersChange,
                (f, s) -> f.checkMergeElements(s,
                    v-> v>= 0,
                    (o,n)-> o+n>=0
                ) && StaticGameData.passengerTypes.containsAll(s.keySet())
            );
    }

    public boolean checkChangesDebug(DataChanges changes) {
        // 1. Initial integrity checks
        if (!StaticGameData.worldEventTypes.containsAll(changes.newWorldEvents)) {
            System.err.println("Validation failed: newWorldEvents contain invalid event types.");
            return false;
        }
        if (!players.checkChangeSetTI(changes.playersToAdd, changes.playersToRemove)) {
            System.err.println("Validation failed: players change set (playersToAdd/playersToRemove) is invalid.");
            return false;
        }
        if (!airports.checkChangeSetTI(changes.airportsToAdd, changes.airportsToRemove)) {
            System.err.println("Validation failed: airports change set (airportsToAdd/airportsToRemove) is invalid.");
            return false;
        }
        if (!airlines.checkChangeSetTI(changes.airlinesToAdd, changes.airlinesToRemove)) {
            System.err.println("Validation failed: airlines change set (airlinesToAdd/airlinesToRemove) is invalid.");
            return false;
        }

        // Generating temporary sets for deep validation
        SetHolder<Airport> airportsTmp = TemporarySetHolder.generateTemporarySetHolder(
            airports, changes.airportsToAdd, changes.airportsToRemove,
            AirportDTO::restore);
        SetHolder<Airline> airlinesTmp = TemporarySetHolder.generateTemporarySetHolder(
            airlines, changes.airlinesToAdd, changes.airlinesToRemove,
            (dto) -> dto.restore(airportsTmp, this.players));
        SetHolder<Player> playersTmp = TemporarySetHolder.generateTemporarySetHolder(
            players, changes.playersToAdd, changes.playersToRemove,
            (dto) -> dto.restore(airlinesTmp));

        // 2. Turn and player presence checks
        if (changes.currentPlayer != null && changes.currentPlayer != -1 && !playersTmp.contains(changes.currentPlayer)) {
            System.err.println("Validation failed: currentPlayer is not present in the temporary player set. Current player = " + changes.currentPlayer + " tmpPlayerSet.size() = " + playersTmp.size());
            return false;
        }
        if (!playersTmp.containsAll(changes.turnOrder)) {
            System.err.println("Validation failed: turnOrder contains players not present in the temporary player set.");
            return false;
        }

        // 3. Market and global availability checks
        if (!availableAirlines.checkChangeSetIILookUp(changes.availableAirlinesToAdd, changes.availableAirlinesToRemove, airlinesTmp)) {
            System.err.println("Validation failed: availableAirlines changes are invalid relative to airlinesTmp.");
            return false;
        }
        if (!availableAbilities.checkChangeSetIILookUp(changes.availableAbilitiesToAdd, changes.availableAbilitiesToRemove, StaticGameData.abilityTypes)) {
            System.err.println("Validation failed: availableAbilities changes are invalid relative to StaticGameData.abilityTypes.");
            return false;
        }
        if (!StaticGameData.planeTypes.containsAllKeys(changes.availablePlanesChange)) {
            System.err.println("Validation failed: availablePlanesChange contains keys missing from StaticGameData.planeTypes.");
            return false;
        }
        if (!availablePlanes.checkMergeElements(changes.availablePlanesChange, v -> v >= 0, (f, s) -> f + s >= 0)) {
            System.err.println("Validation failed: availablePlanes merge check failed (negative values or invalid state).");
            return false;
        }

        // 4. Player state modification checks
        if (!playersTmp.containsAllKeys(changes.playerHasPassedSet)) {
            System.err.println("Validation failed: playerHasPassedSet contains keys missing from playersTmp.");
            return false;
        }
        if (!playersTmp.checkChangeAsStruct(Player::getMoney, changes.playerMoneyChange, (current, change) -> current + change >= 0)) {
            System.err.println("Validation failed: playerMoneyChange resulted in negative balance for a player.");
            return false;
        }
        if (!playersTmp.checkChangeAsStruct(Player::getAuctionBet, changes.playerAuctionBetChanges, (current, change) -> current + change >= 0)) {
            System.err.println("Validation failed: playerAuctionBetChanges resulted in negative auction bet.");
            return false;
        }
        if (!playersTmp.checkChangeAsStruct(Player::getAmountOfShares, changes.playerAmountOfSharesChange,
            (o, n) -> o + n >= 0 && o + n <= StaticGameData.maxAmountOfShares)) {
            System.err.println("Validation failed: playerAmountOfSharesChange went out of bounds [0, maxAmountOfShares].");
            return false;
        }
        if (!playersTmp.checkChangeAsStruct(Player::getActionPoints, changes.playerActionPointsChange,
            (o, n) -> o + n >= 0 && o + n <= StaticGameData.maxActionsPerTurn)) {
            System.err.println("Validation failed: playerActionPointsChange went out of bounds [0, maxActionsPerTurn].");
            return false;
        }

        // 5. Player sub-entities checks (Airlines, Abilities, Planes)
        if (!playersTmp.checkChangeAsStruct((pl) -> pl.airlines,
            Arrays.asList(changes.playerAirlinesToAdd, changes.playerAirlinesToRemove),
            (f, s) -> f.checkChangeSetIILookUp(s.get(0), s.get(1), airlinesTmp))) {
            System.err.println("Validation failed: player airlines change validation failed against airlinesTmp.");
            return false;
        }
        if (!playersTmp.containsAllKeys(changes.playerAbilityChoice)) {
            System.err.println("Validation failed: playerAbilityChoice contains player keys missing from playersTmp.");
            return false;
        }
        if (!StaticGameData.abilityTypes.containsAllValues(changes.playerAbilityChoice)) {
            System.err.println("Validation failed: playerAbilityChoice contains values missing from StaticGameData.abilityTypes.");
            return false;
        }
        if (!playersTmp.checkChangeAsStruct((pl) -> pl.planes,
            Arrays.asList(changes.playerPlanesToAdd, changes.playerPlanesToRemove),
            (f, s) -> f.checkMergeElements(s,
                (params) -> params.get(0) - params.get(1) >= 0,
                (old, params) -> old + params.get(0) - params.get(1) >= 0,
                (i) -> 0
            ) && StaticGameData.planeTypes.containsAll(s.get(0).keySet()))) {
            System.err.println("Validation failed: player planes change validation or plane type validation failed.");
            return false;
        }

        // 6. Airport passengers checks
        if (!airportsTmp.checkChangeAsStruct((port) -> port.passengers,
            changes.airportPassengersChange,
            (f, s) -> f.checkMergeElements(s,
                v -> v >= 0,
                (o, n) -> o + n >= 0
            ) && StaticGameData.passengerTypes.containsAll(s.keySet()))) {
            System.err.println("Validation failed: airportPassengersChange validation or passenger type validation failed.");
            return false;
        }

        // All checks passed successfully
        return true;
    }


    public void clearGameData(){
        roundNumber = null;
        currentState = State.NO_STATE;
        currentPlayer = null;
        turnOrder = null;
        currentBet = 0;
        worldEvents.clear();
        airports.clear();
        airlines.clear();
        players.clear();
        availableAirlines.clear();
        availablePlanes.clear();
    }

    public DataChanges createDataChangesFromThis(){
        GameData.DataChanges res = new DataChanges();
        res.roundNumber = roundNumber;
        res.currentState = currentState;
        res.currentPlayer = currentPlayer;
        res.currentBet = currentBet;

        if(this.turnOrder != null) {
            res.turnOrder = new ArrayList<>(this.turnOrder.size());
            for(Player pl : this.turnOrder)
                res.turnOrder.add(pl.getId());
        }
        res.playersToAdd = new HashSet<>();
        for(Player pl : players){
            res.playersToAdd.add(new PlayerDTO(pl)); // all player field are here
        }
        res.airportsToAdd = new HashSet<>();
        for(Airport port : airports){
            res.airportsToAdd.add(new AirportDTO(port)); // airport passengers to add is here
        }
        res.airlinesToAdd = new HashSet<>();
        for(Airline line : airlines){
            res.airlinesToAdd.add(new AirlineDTO(line));
        }
        res.availableAirlinesToAdd = new HashSet<>();
        for(Airline line : availableAirlines){
            res.availableAirlinesToAdd.add(line.getId());
        }
        res.availableAbilitiesToAdd = new HashSet<>();
        for(AbilityType ab : availableAbilities){
            res.availableAbilitiesToAdd.add(ab.getId());
        }
        res.availablePlanesChange = new HashMap<>();
        res.availablePlanesChange.putAll(availablePlanes);
        res.newWorldEvents = new HashSet<>();
        for(WorldEventType event : worldEvents){
            res.newWorldEvents.add(event.getId());
        }

        return res;
    }


}
