package com.game.Ticket_To_Flight.commonFrontAndBack;

import com.game.Ticket_To_Flight.Utilities.SetHolder;
import com.game.Ticket_To_Flight.Utilities.Identifiable;
import com.game.Ticket_To_Flight.Utilities.MapHolder;
import com.game.Ticket_To_Flight.Utilities.TemporarySetHolder;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airline;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Passenger;
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


    public static SetHolder<AirportType> airportTypes = new SetHolder<>();
    public static SetHolder<AirlineType> airlineTypes = new SetHolder<>();
    public static SetHolder<PlaneType> planeTypes = new SetHolder<>();
    public static SetHolder<CityType> cityTypes = new SetHolder<>();
    public static SetHolder<PassengerType> passengerTypes = new SetHolder<>();
    public static SetHolder<WorldEventType> worldEventTypes = new SetHolder<>();
    public static SetHolder<AbilityType> abilityTypes = new SetHolder<>();
    public static Integer maxActionsPerTurn = 5;

    public enum State {
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
    public State currentState;
    public Player currentPlayer;

    public SetHolder<WorldEventType> worldEvents = new SetHolder<>();
    public SetHolder<Airport> airports = new SetHolder<>();
    public SetHolder<Airline> airlines = new SetHolder<>();
    public SetHolder<Passenger> passengers = new SetHolder<>();
    public SetHolder<Player> players = new SetHolder<>();
    public SetHolder<Airline> availableAirlines = new SetHolder<>();
    public MapHolder<PlaneType, Integer> availablePlanes = new MapHolder<>(GameData.planeTypes);
    public MapHolder<Player, Integer> actionPoints = new MapHolder<>(players);

    public static class DataChanges extends Identifiable {



        private static final AtomicInteger idGenerator = new AtomicInteger(0);

        public  Set<Player> playersToAdd = null;
        public  Set<Integer> playersToRemove= null;
        public  Set<Airport> airportsToAdd= null;
        public  Set<Integer> airportsToRemove= null;
        public  Set<Airline> airlinesToAdd= null;
        public  Set<Integer> airlinesToRemove= null;
        public  Set<Passenger> passengersToAdd= null;
        public  Set<Integer> passengersToRemove= null;
        public  Set<Integer> availableAirlinesToAdd= null;
        public  Set<Integer> availableAirlinesToRemove= null;


        public  GameData.State currentState= null;
        public  Integer currentPlayer= null;
        public  Set<Integer> newWorldEvents= null;


        public  Map<Integer, Integer> availablePlanesToRemove= null;
        public  Map<Integer, Integer> availablePlanesToAdd= null;

        public  Map<Integer, Double> playerMoneyChange= null;
        public  Map<Integer, Double> playerIncomeChange= null;
        public  Map<Integer, Integer> playerActionPointsChange= null;

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

            if (other.currentState != null) this.currentState = other.currentState;
            if (other.currentPlayer != null) this.currentPlayer = other.currentPlayer;

            this.newWorldEvents = SetHolder.merge(this.newWorldEvents, other.newWorldEvents);
            this.availableAirlinesToAdd = SetHolder.merge(this.availableAirlinesToAdd, other.availableAirlinesToAdd);
            this.availableAirlinesToRemove = SetHolder.merge(this.availableAirlinesToRemove, other.availableAirlinesToRemove);
            this.playersToAdd = SetHolder.merge(this.playersToAdd, other.playersToAdd);
            this.playersToRemove = SetHolder.merge(this.playersToRemove, other.playersToRemove);
            this.airportsToAdd = SetHolder.merge(this.airportsToAdd, other.airportsToAdd);
            this.airportsToRemove = SetHolder.merge(this.airportsToRemove, other.airportsToRemove);
            this.airlinesToAdd = SetHolder.merge(this.airlinesToAdd, other.airlinesToAdd);
            this.airlinesToRemove = SetHolder.merge(this.airlinesToRemove, other.airlinesToRemove);
            this.passengersToAdd = SetHolder.merge(this.passengersToAdd, other.passengersToAdd);
            this.passengersToRemove = SetHolder.merge(this.passengersToRemove, other.passengersToRemove);


            this.availablePlanesToRemove = MapHolder.merge(
                this.availablePlanesToRemove, other.availablePlanesToRemove, v -> v, DataChanges::sumIntOrNull);

            this.availablePlanesToAdd = MapHolder.merge(
                this.availablePlanesToAdd, other.availablePlanesToAdd, v -> v, DataChanges::sumIntOrNull);

            this.playerMoneyChange = MapHolder.merge(
                this.playerMoneyChange, other.playerMoneyChange, v -> v, DataChanges::sumDoubleOrNull);

            this.playerIncomeChange = MapHolder.merge(
                this.playerIncomeChange, other.playerIncomeChange, v -> v, DataChanges::sumDoubleOrNull);

            this.playerActionPointsChange = MapHolder.merge(
                this.playerActionPointsChange, other.playerActionPointsChange, v -> v, DataChanges::sumIntOrNull);

            this.playerAirlinesToAdd = MapHolder.merge(
                this.playerAirlinesToAdd, other.playerAirlinesToAdd, v->v,
                (f,s)-> SetHolder.merge(f,s)
            );

            this.playerAirlinesToRemove = MapHolder.merge(
                this.playerAirlinesToRemove, other.playerAirlinesToRemove, v->v,
                (f,s)-> SetHolder.merge(f,s)
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
        if (changes.currentState != null) this.currentState = changes.currentState;
        if (changes.currentPlayer != null) this.currentPlayer = players.get(changes.currentPlayer);
        worldEvents.clearAndAddAllFromLookUp(changes.newWorldEvents, GameData.worldEventTypes);
        players.changeSetTI(changes.playersToAdd, changes.playersToRemove);
        airports.changeSetTI(changes.airportsToAdd, changes.airportsToRemove);
        airlines.changeSetTI(changes.airlinesToAdd, changes.airlinesToRemove);
        passengers.changeSetTI(changes.passengersToAdd, changes.passengersToRemove);
        availableAirlines.changeSetII(changes.availableAirlinesToAdd, changes.availableAirlinesToRemove, this.airlines);
        availableAirlines.retainAll(airlines);

        availablePlanes.merge(Arrays.asList(changes.availablePlanesToAdd, changes.availablePlanesToRemove),
            (params)-> {Integer res = params.get(0) - params.get(1); return res == 0 ? null : res;},
            (old, params)->{Integer res = old+params.get(0) - params.get(1); return res == 0 ? null : res;},
            (i)->0);

        actionPoints.merge(changes.playerActionPointsChange, (o)->o, (o, n)->n+o);
        actionPoints.removeAllRefsToNotExistingObjects();

        players.changeAsStructWithSetterInteger(Player::setIncome, Player::getIncome,
            changes.playerIncomeChange, (f, s)->f+s);
        players.changeAsStructWithSetterInteger(Player::setMoney, Player::getMoney,
            changes.playerMoneyChange, (f, s)->f+s);
        players.changeAsStructInteger((pl) -> pl.airlines,
            Arrays.asList(changes.playerAirlinesToAdd, changes.playerAirlinesToRemove),
            (cur, params)-> {
            cur.changeSetII(params.get(0), params.get(1), airlines); cur.retainAll(airlines);
        });
        players.changeAsStructInteger((pl) -> pl.planes,
            Arrays.asList(changes.playerPlanesToAdd, changes.playerPlanesToRemove),
            (f, s)-> f.merge(s,
                (params)-> {Integer res = params.get(0) - params.get(1); return res == 0 ? null : res;},
                (old, params)->{Integer res = old+params.get(0) - params.get(1); return res == 0 ? null : res;},
                (i)->0
            )
        );

    }

    public boolean checkChanges(DataChanges changes){
        if( !GameData.worldEventTypes.containsAll(changes.newWorldEvents) ||
            !players.checkChangeSetTI(changes.playersToAdd, changes.playersToRemove) ||
            !airports.checkChangeSetTI(changes.airportsToAdd, changes.airportsToRemove) ||
            !airlines.checkChangeSetTI(changes.airlinesToAdd, changes.airlinesToRemove) ||
            !passengers.checkChangeSetTI(changes.passengersToAdd, changes.passengersToRemove)
        ) return false;

        SetHolder<Airline> airlinesTmp = TemporarySetHolder.generateTemporarySetHolder(
            airlines, changes.airlinesToAdd, changes.airlinesToRemove);
        SetHolder<Player> playersTmp = TemporarySetHolder.generateTemporarySetHolder(
            players, changes.playersToAdd, changes.playersToRemove);

        if (changes.currentPlayer != null && !playersTmp.contains(changes.currentPlayer)) return false;
        if( !availableAirlines.checkChangeSetIILookUp(
            changes.availableAirlinesToAdd, changes.availableAirlinesToRemove, airlinesTmp) ||
            !GameData.planeTypes.containsAll(changes.availablePlanesToAdd.keySet()) ||
            !availablePlanes.checkMergeElements(
                Arrays.asList(changes.availablePlanesToAdd, changes.availablePlanesToRemove),
                (params)->params.get(0)-params.get(1)>=0,
                (old, params)->old+params.get(0)-params.get(1)>=0,
                (i)->0
            ) ||
            !playersTmp.checkChangeAsStructInteger(Player::getMoney, changes.playerMoneyChange,
                (current, change) -> current + change >= 0) ||
            !playersTmp.containsAll(changes.playerActionPointsChange.keySet()) ||
            !actionPoints.checkMergeElements(changes.playerActionPointsChange, null,
                (o, n)->o+n>=0 && o+n<=GameData.maxActionsPerTurn) ||
            !playersTmp.checkChangeAsStructInteger((pl)->pl.airlines,
                Arrays.asList(changes.playerAirlinesToAdd, changes.playerAirlinesToRemove),
                (f,s)->f.checkChangeSetIILookUp(s.get(0), s.get(1), airlinesTmp)
            )||
            !playersTmp.checkChangeAsStructInteger((pl)->pl.planes,
                Arrays.asList(changes.playerPlanesToAdd, changes.playerPlanesToRemove),
                (f,s)->f.checkMergeElements(s,
                    (params)->params.get(0)-params.get(1)>=0,
                    (old, params)->old+params.get(0)-params.get(1)>=0,
                    (i)->0
                ) && GameData.planeTypes.containsAll(s.get(0).keySet())
            )
        ) return false;


        return true;
    }

    public boolean applyChanges(DataChanges changes) {
        if (changes == null) return true;

        if(!checkChanges(changes)) return false;

        applyChangesUnsafe(changes);

        return true;
    }

    //returns null if requirements not met, otherwise returns total income
    public static Double checkLine(Airline line, List<Passenger> psg, PlaneType plane){
        if(!line.type.luxuryRange.contains(plane.luxury) ||
            !line.type.capacityRange.contains(plane.capacity) ||
            !plane.distRange.contains(line.getDistance()) ||
            !plane.gateRange.contains(line.type.gateA) ||
            !plane.gateRange.contains(line.type.gateB) ||
            line.player == null)
            return null;
        int amountOfPeople = 0;
        double solvencySum = 0;

        for(Passenger pt : psg) {
            if(!pt.type.luxuryRange.contains(plane.luxury) ||
                !pt.type.capacityRange.contains(plane.capacity) ||
                !pt.type.yieldRange.contains(line.type.yield))
                return null;
            amountOfPeople += pt.type.size;
            solvencySum += pt.type.solvency;
        }

        if(plane.capacity< amountOfPeople) return null;

        return plane.price * solvencySum * line.type.yield;
    }

    //does not check if a player has a plane and does not add removing the plane from the player
    public static DataChanges checkRoute(Airport start, List<Airline> route, List<Passenger> passengers, PlaneType plane){
        if(start == null || route == null || passengers == null ||
            route.isEmpty() || passengers.isEmpty() || plane==null) return null;

        if(route.size() > plane.stations + 1) return null;

        double amountOfFuel = 0;
        for(Airline line : route)
            amountOfFuel += line.getDistance();
        if(plane.fuel < amountOfFuel) return null;

        DataChanges resultChanges = new DataChanges();
        resultChanges.playerIncomeChange = new HashMap<>();
        resultChanges.passengersToRemove = new HashSet<>();

        Iterator<Airline> airlineIterator = route.listIterator();
        Iterator<Passenger> passengersIterator = passengers.listIterator();

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
}
