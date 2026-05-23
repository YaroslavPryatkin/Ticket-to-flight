package com.game.Ticket_To_Flight.commonFrontAndBack;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.game.Ticket_To_Flight.Utilities.SetHolder;
import com.game.Ticket_To_Flight.Utilities.Identifiable;
import com.game.Ticket_To_Flight.Utilities.MapHolder;
import com.game.Ticket_To_Flight.Utilities.TemporarySetHolder;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airline;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Player;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.*;

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

    public SetHolder<WorldEventType> worldEvents = new SetHolder<>();
    public SetHolder<Airport> airports = new SetHolder<>();
    public SetHolder<Airline> airlines = new SetHolder<>();
    public SetHolder<Player> players = new SetHolder<>();
    public SetHolder<Airline> availableAirlines = new SetHolder<>();
    public SetHolder<AbilityType> availableAbilities = new SetHolder<>();
    public MapHolder<PlaneType, Integer> availablePlanes = new MapHolder<>(StaticGameData.planeTypes);


    public static class AirlineDTO extends Identifiable {
        private AirlineDTO(){super(0); type = 0; portA = 0; portB = 0; player = null;}
        private static final AtomicInteger idGenerator = new AtomicInteger(0);
        private final int type;
        private final int portA;
        private final int portB;
        private final Integer player;

        public AirlineDTO(Airline line){
            super(line.getId());
            this.type = line.type.getId();
            this.portA = line.portA.getId();
            this.portB = line.portB.getId();
            if(line.player!=null){
                this.player=line.player.getId();
            }
            else {
                this.player=null;
            }
        }

        /**
         * Should not be called anywhere except Low Level Handler
         */
        public AirlineDTO(AirlineType type, Airport portA, Airport portB) {
            super(idGenerator.incrementAndGet());
            this.type = type.getId();
            this.portA = portA.getId();
            this.portB = portB.getId();
            this.player = null;
        }

        /**
         * Should not be called anywhere except Low Level Handler
         * UNSAFE
         */
        public AirlineDTO(Integer type, Integer portA, Integer portB) {
            super(idGenerator.incrementAndGet());
            this.type = type;
            this.portA = portA;
            this.portB = portB;
            this.player = null;
        }

        public Airline restore(SetHolder<Airport> lookUpAirports, SetHolder<Player> lookUpPlayers){
            AirlineType type = StaticGameData.airlineTypes.get(this.type);
            Airport portA = lookUpAirports.get(this.portA);
            Airport portB = lookUpAirports.get(this.portB);
            if(type == null || portA == null || portB == null) return null;

            if(this.player==null)
                return new Airline(this.getId(), type, portA, portB, null);

            Player player = lookUpPlayers.get(this.player);
            if(player == null) return null;

            return new Airline(this.getId(), type, portA, portB, player);
        }
    }
    public static class AirportDTO extends Identifiable{
        private AirportDTO(){super(0); type = null; position = null; passengers = null; name = null;}
        private final Integer type;
        private final Vector2 position;
        private final Map<Integer, Integer> passengers;
        private final String name;
        public AirportDTO(Airport port) {
            super(port.getId());
            this.type = port.type.getId();
            this.position = port.position;
            this.passengers=new HashMap<>();
            this.passengers.putAll(port.passengers);
            this.name = port.airportName;
        }

        /**
         * Should not be called anywhere except Low Level Handler
         */
        public AirportDTO(int id, AirportType type, Vector2 position, String AirportName) {
            super(id);
            if(type == null || position == null) throw new IllegalArgumentException("Null arguments in constructor.");
            this.type = type.getId();
            this.position = position;
            this.name = AirportName;
            this.passengers = new HashMap<>();
        }

        /**
         * Should not be called anywhere except Low Level Handler
         * UNSAFE
         */
        public AirportDTO(int id, Integer type, Vector2 position, String AirportName) {
            super(id);
            if(type == null || position == null) throw new IllegalArgumentException("Null arguments in constructor.");
            this.type = type;
            this.position = position;
            this.name = AirportName;
            this.passengers = new HashMap<>();
        }

        public Airport restore(){
            AirportType type = StaticGameData.airportTypes.get(this.type);
            if(type == null) return null;
            MapHolder<PassengerType, Integer> passengers = new MapHolder<>(StaticGameData.passengerTypes);
            try {
                passengers.putAll(this.passengers);
            }
            catch(Exception e){
                return null;
            }
            return new Airport(this.getId(), type, this.position, this.name);
        }
    }
    public static class PlayerDTO extends Identifiable{
        private PlayerDTO(){
            super(0); name = null; money = 0; income = 0; amountOfShares=0;
            actionPoints=0; planes = null; airlines = null; ability = null; color = null;
            hasPassed = false; auctionBet=null;}
        private static final AtomicInteger idGenerator = new AtomicInteger(0);

        private final String name;
        private final double money;
        private final double income;
        private final int amountOfShares;
        private final int actionPoints;
        private final  boolean hasPassed;
        private final Integer auctionBet;
        private final Map<Integer, Integer> planes;
        private final Set<Integer> airlines;
        private final Integer ability;
        private final  Color color;

        public PlayerDTO(Player player) {
            super(player.getId());
            this.name = player.name;
            this.money = player.money;
            this.income = player.income;
            this.amountOfShares = player.amountOfShares;
            this.actionPoints = player.actionPoints;
            this.airlines = new HashSet<>();
            for(Airline line : player.airlines){
                this.airlines.add(line.getId());
            }
            this.planes = new HashMap<>();
            this.planes.putAll(player.planes);
            if(player.ability != null)
                this.ability = player.ability.getId();
            else
                this.ability = null;
            this.color = player.color;
            this.hasPassed = player.hasPassed;
            this.auctionBet = player.auctionBet;
        }

        /**
         * Should not be called anywhere except Low Level Handler
         * Creates player in default state
         */
        public PlayerDTO(String name, Color color){
            super( idGenerator.incrementAndGet());
            this.name = name;
            money = 0;
            income = 0;
            amountOfShares = 0;
            actionPoints=0;
            planes = new HashMap<>();
            airlines = new HashSet<>();
            ability = null;
            this.color = color;
            this.hasPassed = false;
            this.auctionBet = null;
        }

        public Player restore(SetHolder<Airline> lookUpAirlines){
            SetHolder<Airline> lines = new SetHolder<>();
            for(Integer id : this.airlines ){
                Airline line = lookUpAirlines.get(id);
                if(line == null) return null;
                lines.add(line);
            }
            MapHolder<PlaneType, Integer> planes = new MapHolder<>(StaticGameData.planeTypes);
            try {
                planes.putAll(this.planes);
            }
            catch(Exception e){
                return null;
            }
            return new Player(
                this.getId(), this.money, this.income, this.amountOfShares,
                this.actionPoints, planes, lines, this.name, StaticGameData.abilityTypes.get(this.ability),
                this.color, this.hasPassed, this.auctionBet);
        }
    }

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

        public  Map<Integer, Integer> availablePlanesToAdd= null;
        public  Map<Integer, Integer> availablePlanesToRemove= null;
        public  Map<Integer, Map<Integer, Integer>> airportPassengersToAdd= null;
        public  Map<Integer, Map<Integer, Integer>> airportPassengersToRemove= null;


        public  Map<Integer, Boolean> playerHasPassedSet = null;
        public  Map<Integer, Double> playerMoneyChange= null;
        public  Map<Integer, Double> playerIncomeChange= null;
        public  Map<Integer, Integer> playerActionPointsChange= null;
        public  Map<Integer, Integer> playerAmountOfSharesChange= null;
        public  Map<Integer, Integer> playerAbilityChoice = null;
        public  Map<Integer, Integer> playerAuctionBetChanges = null;

        public  Map<Integer, Set<Integer>> playerAirlinesToAdd= null;
        public  Map<Integer, Set<Integer>> playerAirlinesToRemove= null;
        public  Map<Integer, Map<Integer, Integer>> playerPlanesToAdd= null;
        public  Map<Integer, Map<Integer, Integer>> playerPlanesToRemove= null;

        public DataChanges(){super(idGenerator.incrementAndGet());}

        /**
         * Merges DataChanges. "other" should not be used after merging
         * @param other - source
         * @return this
         */
        public DataChanges merge(DataChanges other) {
            if (other == null) return this;
            if(other.roundNumber != null) this.roundNumber = other.roundNumber;
            if (other.currentState != null) this.currentState = other.currentState;
            if (other.currentPlayer != null) this.currentPlayer = other.currentPlayer;
            if(other.turnOrder != null) this.turnOrder = other.turnOrder;

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

            this.airportPassengersToAdd = MapHolder.merge(
                this.airportPassengersToAdd, other.airportPassengersToAdd, v->v,
                (f,s)->MapHolder.merge(f,s,v->v,(o,n)->o+n)
            );

            this.airportPassengersToRemove = MapHolder.merge(
                this.airportPassengersToRemove, other.airportPassengersToRemove, v->v,
                (f,s)->MapHolder.merge(f,s,v->v,(o,n)->o+n)
            );

            this.availablePlanesToRemove = MapHolder.merge(
                this.availablePlanesToRemove, other.availablePlanesToRemove, v -> v, DataChanges::sumIntOrNull);

            this.availablePlanesToAdd = MapHolder.merge(
                this.availablePlanesToAdd, other.availablePlanesToAdd, v -> v, DataChanges::sumIntOrNull);

            this.playerMoneyChange = MapHolder.merge(
                this.playerMoneyChange, other.playerMoneyChange, v -> v, DataChanges::sumDoubleOrNull);

            this.playerAuctionBetChanges = MapHolder.merge(
                this.playerAuctionBetChanges, other.playerAuctionBetChanges, v -> v, DataChanges::sumIntOrNull);

            this.playerIncomeChange = MapHolder.merge(
                this.playerIncomeChange, other.playerIncomeChange, v -> v, DataChanges::sumDoubleOrNull);

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
                (f,s)->MapHolder.merge(f,s,v->v,(o,n)->o+n)
            );

            this.playerPlanesToRemove = MapHolder.merge(
                this.playerPlanesToRemove, other.playerPlanesToRemove, v->v,
                (f,s)->MapHolder.merge(f,s,v->v,(o,n)->o+n)
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



        worldEvents.clearAndAddAllFromLookUp(changes.newWorldEvents, StaticGameData.worldEventTypes);
        airports.changeSetDTOI(changes.airportsToAdd, changes.airportsToRemove,
            (dto)->dto.restore());
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

        availablePlanes.merge(Arrays.asList(changes.availablePlanesToAdd, changes.availablePlanesToRemove),
            (params)-> {int res = params.get(0) - params.get(1); return res == 0 ? null : res;},
            (old, params)->{int res = old+params.get(0) - params.get(1); return res == 0 ? null : res;},
            (i)->0);

        players.changeAsStructWithSetter(Player::setHasPassed, Player::getHasPassed, changes.playerHasPassedSet,
            (f,s) -> (s==null) ? f : s);

        players.changeAsStructWithSetter(Player::setAbility, Player::getAbility,
            (i)-> StaticGameData.abilityTypes.get(i), changes.playerAbilityChoice,
            (f,s)->(s==null) ? f : s);
        players.changeAsStructWithSetter(Player::setActionPoints, Player::getActionPoints,
            changes.playerActionPointsChange, (f, s)->f+s);
        players.changeAsStructWithSetter(Player::setAmountOfShares, Player::getAmountOfShares,
            changes.playerAmountOfSharesChange, (f, s)->f+s);
        players.changeAsStructWithSetter(Player::setIncome, Player::getIncome,
            changes.playerIncomeChange, (f, s)->f+s);
        players.changeAsStructWithSetter(Player::setMoney, Player::getMoney,
            changes.playerMoneyChange, (f, s)->f+s);
        players.changeAsStructWithSetter(Player::setAuctionBet, Player::getAuctionBet,
            changes.playerAuctionBetChanges, (f, s)->f+s);

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
                (params)-> {Integer res = params.get(0) - params.get(1); return res == 0 ? null : res;},
                (old, params)->{Integer res = old+params.get(0) - params.get(1); return res == 0 ? null : res;},
                (i)->0
            )
        );

        airports.changeAsStruct((pl) -> pl.passengers,
            Arrays.asList(changes.airportPassengersToAdd, changes.airportPassengersToRemove),
            (f, s)-> f.merge(s,
                (params)-> {Integer res = params.get(0) - params.get(1); return res == 0 ? null : res;},
                (old, params)->{Integer res = old+params.get(0) - params.get(1); return res == 0 ? null : res;},
                (i)->0
            )
        );

    }

    public boolean checkChanges(DataChanges changes){
        if( !StaticGameData.worldEventTypes.containsAll(changes.newWorldEvents) ||
            !players.checkChangeSetTI(changes.playersToAdd, changes.playersToRemove) ||
            !airports.checkChangeSetTI(changes.airportsToAdd, changes.airportsToRemove) ||
            !airlines.checkChangeSetTI(changes.airlinesToAdd, changes.airlinesToRemove)
        ) return false;

        SetHolder<Airport> airportsTmp = TemporarySetHolder.generateTemporarySetHolder(
          airports, changes.airportsToAdd, changes.airportsToRemove,
            (dto)->dto.restore());
        SetHolder<Airline> airlinesTmp = TemporarySetHolder.generateTemporarySetHolder(
            airlines, changes.airlinesToAdd, changes.airlinesToRemove,
            (dto)->dto.restore(airportsTmp, this.players));
        SetHolder<Player> playersTmp = TemporarySetHolder.generateTemporarySetHolder(
            players, changes.playersToAdd, changes.playersToRemove,
            (dto)->dto.restore(airlinesTmp));

        if (changes.currentPlayer != null && !playersTmp.contains(changes.currentPlayer)) return false;
        if(!playersTmp.containsAll(changes.turnOrder)) return false;

        if( !availableAirlines.checkChangeSetIILookUp(
            changes.availableAirlinesToAdd, changes.availableAirlinesToRemove, airlinesTmp) ||
            !availableAbilities.checkChangeSetIILookUp(
                changes.availableAbilitiesToAdd, changes.availableAbilitiesToRemove, StaticGameData.abilityTypes) ||
            !StaticGameData.planeTypes.containsAllKeys(changes.availablePlanesToAdd) ||
            !availablePlanes.checkMergeElements(
                Arrays.asList(changes.availablePlanesToAdd, changes.availablePlanesToRemove),
                (params)->params.get(0)-params.get(1)>=0,
                (old, params)->old+params.get(0)-params.get(1)>=0,
                (i)->0
            ) ||
            !playersTmp.containsAllKeys(changes.playerHasPassedSet) ||
            !playersTmp.checkChangeAsStruct(Player::getMoney, changes.playerMoneyChange,
                (current, change) -> current + change >= 0) ||
            !playersTmp.checkChangeAsStruct(Player::getAuctionBet, changes.playerAuctionBetChanges,
                (current, change) -> current + change >= 0) ||
            !playersTmp.checkChangeAsStruct(Player::getAmountOfShares, changes.playerAmountOfSharesChange,
                (o, n)->o+n>=0 && o+n<= StaticGameData.maxAmountOfShares) ||
            !playersTmp.checkChangeAsStruct(Player::getActionPoints, changes.playerActionPointsChange,
                (o, n)->o+n>=0 && o+n<= StaticGameData.maxActionsPerTurn) ||
            !playersTmp.checkChangeAsStruct((pl)->pl.airlines,
                Arrays.asList(changes.playerAirlinesToAdd, changes.playerAirlinesToRemove),
                (f,s)->f.checkChangeSetIILookUp(s.get(0), s.get(1), airlinesTmp)
            )||
            !playersTmp.containsAllKeys(changes.playerAbilityChoice) ||
            !StaticGameData.abilityTypes.containsAllValues(changes.playerAbilityChoice)||
            !playersTmp.checkChangeAsStruct((pl)->pl.planes,
                Arrays.asList(changes.playerPlanesToAdd, changes.playerPlanesToRemove),
                (f,s)->f.checkMergeElements(s,
                    (params)->params.get(0)-params.get(1)>=0,
                    (old, params)->old+params.get(0)-params.get(1)>=0,
                    (i)->0
                ) && StaticGameData.planeTypes.containsAll(s.get(0).keySet())
            ) ||
            !airportsTmp.checkChangeAsStruct((port)->port.passengers,
                Arrays.asList(changes.airportPassengersToAdd, changes.airportPassengersToRemove),
                (f,s)->f.checkMergeElements(s,
                    (params)->params.get(0)-params.get(1)>=0,
                    (old, params)->old+params.get(0)-params.get(1)>=0,
                    (i)->0
                ) && StaticGameData.passengerTypes.containsAll(s.get(0).keySet())
            )
        ) return false;


        return true;
    }


    public void clearGameData(){
        roundNumber = null;
        currentState = State.NO_STATE;
        currentPlayer = null;
        turnOrder = null;
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
        res.availablePlanesToAdd = new HashMap<>();
        res.availablePlanesToAdd.putAll(availablePlanes);
        res.newWorldEvents = new HashSet<>();
        for(WorldEventType event : worldEvents){
            res.newWorldEvents.add(event.getId());
        }

        return res;
    }











    //returns null if requirements not met, otherwise returns total income
    public static Double checkLine(Airline line, MapHolder<PassengerType, Integer> passengers, PlaneType plane){
        if(!line.type.luxuryRange.contains(plane.luxury) ||
            !line.type.capacityRange.contains(plane.capacity) ||
            !plane.distRange.contains(line.getDistance()) ||
            !plane.gateRange.contains(line.type.gateA) ||
            !plane.gateRange.contains(line.type.gateB) ||
            line.player == null)
            return null;
        int amountOfPeople = 0;
        double solvencySum = 0;

        Iterator<Map.Entry<PassengerType, Integer>> id = MapHolder.viewAsEntrySet(passengers);
        Map.Entry<PassengerType, Integer> e;
        while((e=id.next()) != null) {
            PassengerType type = e.getKey();
            int count = e.getValue();
            if(!type.luxuryRange.contains(plane.luxury) ||
                !type.capacityRange.contains(plane.capacity) ||
                !type.yieldRange.contains(line.type.yield))
                return null;
            amountOfPeople += type.size * count;
            solvencySum += type.solvency * count;
        }

        if(plane.capacity< amountOfPeople) return null;

        return plane.price * solvencySum * line.type.yield;
    }

    //does not check if a player has a plane and does not add removing the plane from the player
    /*
    public static DataChanges checkRoute(Airport start, List<Airline> route, MapHolder<Airport, MapHolder<PassengerType, Integer>> passengers, PlaneType plane){
        if(start == null || route == null || passengers == null ||
            route.isEmpty() || passengers.isEmpty() || plane==null) return null;

        if(route.size() > plane.stations + 1) return null;

        double amountOfFuel = 0;
        for(Airline line : route)
            amountOfFuel += line.getDistance();
        if(plane.fuel < amountOfFuel) return null;

        DataChanges resultChanges = new DataChanges();
        resultChanges.playerIncomeChange = new HashMap<>();
        resultChanges.airportPassengersToRemove = new HashMap<>();

        Iterator<Airline> airlineIterator = route.listIterator();

        class BoardedPassengers{
            private final List<Passenger> psgs = new LinkedList<>();
            private final List<Integer> arpts = new LinkedList<>();
            private int currentPort = 0;

            public void nextPort() {currentPort++;}

            public void add(Passenger p) {
                psgs.add(p);
                arpts.add(currentPort);
            }

            //true if everything good
            public boolean removeArrivedAndCheck(Airport current) {
                ListIterator<Passenger> pIt = psgs.listIterator();
                ListIterator<Integer> aIt = arpts.listIterator();
                while (pIt.hasNext()) {
                    Passenger p = pIt.next();
                    Integer port = aIt.next();
                    if (p.doesAirportFit(current)) {
                        if(!p.type.stationsRange.contains(currentPort - port - 1))
                            return false;
                        pIt.remove();
                        aIt.remove();
                    }
                }
                return true;
            }

            public List<Passenger> getPassengers(){
                return psgs;
            }

            public boolean isEmpty(){
                return psgs.isEmpty();
            }
        }
        BoardedPassengers boardedPassengers = new BoardedPassengers();


        Airport currentAirport = start;
        while(airlineIterator.hasNext()){

            boardedPassengers.nextPort();
            if(!boardedPassengers.removeArrivedAndCheck(currentAirport)) return null;

            while(passengersIterator.hasNext()){
                Passenger psg = passengersIterator.next();
                if(psg.portFrom == currentAirport) {
                    boardedPassengers.add(psg);
                    resultChanges.passengersToRemove.add(psg.getId());
                }
                else
                    break;
            }

            Airline line = airlineIterator.next();

            Double checkRes = checkLine(line, boardedPassengers.getPassengers(), plane);
            if(checkRes == null) return null;

            resultChanges.playerIncomeChange.put(line.player.getId(), resultChanges.playerIncomeChange.get(line.player.getId()) + checkRes);

            currentAirport = line.getAnotherEnd(currentAirport);
            if(currentAirport==null) return null;
        }
        boardedPassengers.nextPort();
        if(!boardedPassengers.removeArrivedAndCheck(currentAirport) ||
            !boardedPassengers.isEmpty()) return null;
        return resultChanges;
    }
    */

}
