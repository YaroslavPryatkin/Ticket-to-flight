package com.game.Ticket_To_Flight.commonFrontAndBack;

import com.game.Ticket_To_Flight.Utilities.SetHolder;
import com.game.Ticket_To_Flight.Utilities.Identifiable;
import com.game.Ticket_To_Flight.Utilities.MapHolder;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airline;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Passenger;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Player;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class GameData {
    public static SetHolder<AirportType> airportTypes = new SetHolder<>();
    public static SetHolder<AirlineType> airlineTypes = new SetHolder<>();
    public static SetHolder<PlaneType> planeTypes = new SetHolder<>();
    public static SetHolder<CityType> cityTypes = new SetHolder<>();
    public static SetHolder<PassengerType> passengerTypes = new SetHolder<>();

    public SetHolder<Airport> airports = new SetHolder<>();
    public SetHolder<Airline> airlines = new SetHolder<>();
    public SetHolder<Passenger> passengers = new SetHolder<>();
    public SetHolder<Player> players = new SetHolder<>();
    public MapHolder<PlaneType, Integer> availablePlanes = new MapHolder<>();

    public static class DataChanges extends Identifiable {
        private static final AtomicInteger idGenerator = new AtomicInteger(0);

        public SetHolder<Player> playersToAdd = null;
        public SetHolder<Player> playersToRemove = null;
        public SetHolder<Airport> airportsToAdd = null;
        public SetHolder<Airport> airportsToRemove = null;
        public SetHolder<Airline> airlinesToAdd = null;
        public SetHolder<Airline> airlinesToRemove= null;
        public MapHolder<PlaneType, Integer> availablePlanesToRemove = null;
        public MapHolder<PlaneType, Integer> availablePlanesToAdd = null;
        public SetHolder<Passenger> passengersToRemove = null;
        public SetHolder<Passenger> passengersToAdd= null;
        public MapHolder<Player, Double> playerMoneyChange = null;
        public MapHolder<Player, Double> playerIncomeChange = null;
        public MapHolder<Player, SetHolder<Airline>> ownersAirlinesToAdd = null;
        public MapHolder<Player, SetHolder<Airline>> ownersAirlinesToRemove = null;
        public MapHolder<Player, MapHolder<PlaneType, Integer>> ownersPlanesToAdd = null;
        public MapHolder<Player, MapHolder<PlaneType, Integer>> ownersPlanesToRemove = null;

        public DataChanges(){super(idGenerator.incrementAndGet());}

        public void merge(DataChanges other){

        }
    }

    public void applyChangesUnsafe(DataChanges changes){
        players.changeSet(changes.playersToAdd, changes.playersToRemove);
        airports.changeSet(changes.airportsToAdd, changes.airportsToRemove);
        airlines.changeSet(changes.airlinesToAdd, changes.airlinesToRemove);
        passengers.changeSet(changes.passengersToAdd, changes.passengersToRemove);
        availablePlanes.changeElements(Arrays.asList(changes.availablePlanesToAdd, changes.availablePlanesToRemove),
            (current, params)->{
            Integer tmp = current + params.get(0) - params.get(1);
            if(tmp==0) return null;
            return tmp;
        });
        players.changeAsStructWithSetter(Player::setIncome, Player::getIncome,
            Arrays.asList(changes.playerIncomeChange), (f, s)->f+s.get(0));
        players.changeAsStructWithSetter(Player::setMoney, Player::getMoney,
            Arrays.asList(changes.playerMoneyChange), (f, s)->f+s.get(0));
        players.changeAsStruct((pl) -> pl.airlines,
            Arrays.asList(changes.ownersAirlinesToAdd, changes.ownersAirlinesToRemove),
            (f, s)-> ((SetHolder<Airline>) f).changeSet(s.get(0), s.get(1)) );
        players.changeAsStruct((pl) -> pl.planes,
            Arrays.asList(changes.ownersPlanesToAdd, changes.ownersPlanesToRemove),
            (f, s)-> f.changeElements(s,
                (current, params)->{
                    Integer tmp = current + params.get(0) - params.get(1);
                    if(tmp==0) return null;
                    return tmp;
                }) );
    }

    public boolean checkChanges(DataChanges changes){
        if( !players.checkChangeSet(changes.playersToAdd, changes.playersToRemove) ||
            !airports.checkChangeSet(changes.airportsToAdd, changes.airportsToRemove) ||
            !airlines.checkChangeSet(changes.airlinesToAdd, changes.airlinesToRemove) ||
            !passengers.checkChangeSet(changes.passengersToAdd, changes.passengersToRemove) ||
            !availablePlanes.checkChangeElements(Arrays.asList(changes.availablePlanesToAdd, changes.availablePlanesToRemove),
            (current, params)-> current + params.get(0) - params.get(1)>=0)
        ) return false;
        SetHolder<Player> tmpPlayers = new SetHolder<>(players);
        tmpPlayers.changeSet(changes.playersToAdd, changes.playersToRemove);
        if (!players.checkChangeAsStruct(Player::getIncome,
                Arrays.asList(changes.playerIncomeChange), (f, s)->true) ||
            !players.checkChangeAsStruct(Player::getMoney,
                Arrays.asList(changes.playerMoneyChange), (f, s)->true) ||
            !players.checkChangeAsStruct((pl) -> pl.airlines,
                Arrays.asList(changes.ownersAirlinesToAdd, changes.ownersAirlinesToRemove),
                (f, s)-> ((SetHolder<Airline>) f).checkChangeSet(s.get(0), s.get(1)) ) ||
            !players.checkChangeAsStruct((pl) -> pl.planes,
                Arrays.asList(changes.ownersPlanesToAdd, changes.ownersPlanesToRemove),
                (f, s)-> f.checkChangeElements(s,
                    (current, params)-> current + params.get(0) - params.get(1)>=0
                )
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
        resultChanges.playerIncomeChange = new MapHolder<>();
        resultChanges.passengersToRemove = new SetHolder<>();

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
                    resultChanges.passengersToRemove.add(psg);
                }
                else
                    break;
            }

            Airline line = airlineIterator.next();

            Double checkRes = checkLine(line, boardedPassengers.getPassengers(), plane);
            if(checkRes == null) return null;

            resultChanges.playerIncomeChange.put(line.player, resultChanges.playerIncomeChange.get(line.player) + checkRes);

            currentAirport = line.getAnotherEnd(currentAirport);
            if(currentAirport==null) return null;
        }
        boardedPassengers.nextPort();
        if(!boardedPassengers.removeArrivedAndCheck(currentAirport) ||
            !boardedPassengers.isEmpty()) return null;
        return resultChanges;
    }
}
