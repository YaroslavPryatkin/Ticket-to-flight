package com.game.Ticket_To_Flight.commonFrontAndBack;

import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airline;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Passenger;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Player;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.*;

import javax.print.attribute.IntegerSyntax;
import javax.xml.crypto.Data;
import java.util.*;

public class GameData {
    public static Map<Integer, AirportType> airportTypes = new HashMap<>();
    public static Map<Integer, AirlineType> airlineTypes = new HashMap<>();
    public static Map<Integer, PlaneType> planeTypes = new HashMap<>();
    public static Map<Integer, CityType> cityTypes = new HashMap<>();
    public static Map<Integer, PassengerType> passengerTypes = new HashMap<>();
    public Map<Integer, Airport> airports = new HashMap<>();
    public Map<Integer, Airline> airlines = new HashMap<>();
    public Map<Integer, Passenger> passengers = new HashMap<>();
    public Map<Integer, Player> players = new HashMap<>();
    public Map<Integer, PlaneType> availablePlanes = new HashMap<>();

    public static class DataChanges {
        public Set<Player> playersToAdd = null;
        public Map<Integer, Double> playerIncomeChange = null;
        public Set<Airport> airportsToAdd = null;
        public Set<Airline> airlinesToAdd = null;
        public Set<Integer> passengerToRemove = null;
        public Set<Passenger> passengerToAdd= null;
        public Map<Integer, Integer> airlinesOwnersToAdd = null;
        public Map<Integer, Integer> planesOwnersToAdd = null;
    }

    public boolean applyChanges(DataChanges changes) {
        if (changes == null) return true;

        //--------
        //verifying changes
        //---------

        //checking all ids are unique
        if (changes.playersToAdd != null) {
            for (Player p : changes.playersToAdd) {
                if (players.containsKey(p.id)) return false;
            }
        }
        if (changes.airportsToAdd != null) {
            for (Airport a : changes.airportsToAdd) {
                if (airports.containsKey(a.id)) return false;
            }
        }
        if (changes.airlinesToAdd != null) {
            for (Airline a : changes.airlinesToAdd) {
                if (airlines.containsKey(a.id)) return false;
            }
        }
        if (changes.passengerToAdd != null) {
            for (Passenger p : changes.passengerToAdd) {
                if (passengers.containsKey(p.id)) return false;
            }
        }

        //checking map.get() != null
        if (changes.passengerToRemove != null) {
            for (Integer passId : changes.passengerToRemove) {
                if (!passengers.containsKey(passId)) return false;
            }
        }

        if (changes.playerIncomeChange != null) {
            for (Integer playerId : changes.playerIncomeChange.keySet()) {
                if (getPlayerOrNew(playerId, changes) == null) return false;
            }
        }

        //also checking line.player == null
        if (changes.airlinesOwnersToAdd != null) {
            for (Map.Entry<Integer, Integer> entry : changes.airlinesOwnersToAdd.entrySet()) {
                Integer airlineId = entry.getKey();
                Integer playerId = entry.getValue();

                if (getPlayerOrNew(playerId, changes) == null) return false;

                Airline airline = getAirlineOrNew(airlineId, changes);
                if (airline == null) return false;

                if (airline.player != null) return false;
            }
        }

        if (changes.planesOwnersToAdd != null) {
            for (Map.Entry<Integer, Integer> entry : changes.planesOwnersToAdd.entrySet()) {
                Integer planeId = entry.getKey();
                Integer playerId = entry.getValue();

                if (getPlayerOrNew(playerId, changes) == null) return false;

                if (!availablePlanes.containsKey(planeId)) return false;
            }
        }


        //-------
        //making changes
        //-------
        if (changes.playersToAdd != null) {
            for (Player p : changes.playersToAdd) players.put(p.id, p);
        }
        if (changes.airportsToAdd != null) {
            for (Airport a : changes.airportsToAdd) airports.put(a.id, a);
        }
        if (changes.airlinesToAdd != null) {
            for (Airline a : changes.airlinesToAdd) airlines.put(a.id, a);
        }
        if (changes.passengerToAdd != null) {
            for (Passenger p : changes.passengerToAdd) passengers.put(p.id, p);
        }

        if (changes.passengerToRemove != null) {
            for (Integer passId : changes.passengerToRemove) {
                passengers.remove(passId);
            }
        }

        if (changes.playerIncomeChange != null) {
            for (Map.Entry<Integer, Double> entry : changes.playerIncomeChange.entrySet()) {
                Player player = players.get(entry.getKey());
                player.income += entry.getValue();
            }
        }

        if (changes.airlinesOwnersToAdd != null) {
            for (Map.Entry<Integer, Integer> entry : changes.airlinesOwnersToAdd.entrySet()) {
                Integer airlineId = entry.getKey();
                Integer playerId = entry.getValue();

                Airline airline = airlines.get(airlineId);
                Player player = players.get(playerId);

                airline.player = player;
                player.airlines.add(airline);
            }
        }

        if (changes.planesOwnersToAdd != null) {
            for (Map.Entry<Integer, Integer> entry : changes.planesOwnersToAdd.entrySet()) {
                Integer planeId = entry.getKey();
                Integer playerId = entry.getValue();

                PlaneType plane = availablePlanes.remove(planeId);
                Player player = players.get(playerId);

                player.planes.add(plane);
            }
        }

        return true;
    }

    //check if object exists in old or new data
    private Player getPlayerOrNew(Integer id, DataChanges changes) {
        if (players.containsKey(id)) return players.get(id);
        if (changes.playersToAdd != null) {
            for (Player p : changes.playersToAdd) {
                if (p.id == id) return p;
            }
        }
        return null;
    }

    private Airline getAirlineOrNew(Integer id, DataChanges changes) {
        if (airlines.containsKey(id)) return airlines.get(id);
        if (changes.airlinesToAdd != null) {
            for (Airline a : changes.airlinesToAdd) {
                if (a.id == id) return a;
            }
        }
        return null;
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
        resultChanges.passengerToRemove = new HashSet<>();

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
                    resultChanges.passengerToRemove.add(psg.id);
                }
                else
                    break;
            }

            Airline line = airlineIterator.next();

            Double checkRes = checkLine(line, boardedPassengers.getPassengers(), plane);
            if(checkRes == null) return null;

            resultChanges.playerIncomeChange.put(line.player.id, resultChanges.playerIncomeChange.get(line.player.id) + checkRes);

            currentAirport = line.getAnotherEnd(currentAirport);
            if(currentAirport==null) return null;
        }
        boardedPassengers.nextPort();
        if(!boardedPassengers.removeArrivedAndCheck(currentAirport) ||
            !boardedPassengers.isEmpty()) return null;
        return resultChanges;
    }
}
