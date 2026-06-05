package com.game.Ticket_To_Flight.commonFrontAndBack;

import com.game.Ticket_To_Flight.Utilities.MapHolder;
import com.game.Ticket_To_Flight.Utilities.SetHolder;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airline;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Player;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PassengerType;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PlaneType;

import java.util.*;

public class Route {

    public class BoarderPassenger {
        PassengerType type;
        int amountOfStations; // actual values + 1
        int myAirportVisited;
        final Airport boardedAt; // Track where the passenger boarded

        BoarderPassenger(PassengerType type) {
            this.type = type;
            amountOfStations = 0;
            myAirportVisited = 0;
            this.boardedAt = current; // Contextual field access from Route
        }

        String canFlyToTheNextPort(Airport next, Airline line) {
            if (!type.stationsRange.lessThenMax(amountOfStations))
                return "Passenger's route have too many stations.";
            if (!type.yieldRange.contains(line.type.yield))
                return "Line's yield does not fit the passenger desires.";

            if (myAirportVisited == 0 &&
                next.type.cityType.equals(type.typeTo) &&
                !type.stationsRange.contains(amountOfStations))
                return "Passenger's route does not fit their desired station amount.";
            return null;
        }

        void flyToTheNextPort(Airport next) {
            ++amountOfStations;
            if (next.type.cityType.equals(type.typeTo)) {
                if(myAirportVisited == 0){
                    currentAmountOfPassengers-=type.size;
                    currentSolvency-=type.solvency;
                }
                ++myAirportVisited;
            }
        }

        int returnToPreviousAirport() {
            --amountOfStations;
            // 'current' here refers to Route.this.current (the port we are rolling back FROM)
            if (current.type.cityType.equals(type.typeTo)) {
                --myAirportVisited;
                if(myAirportVisited == 0){
                    currentAmountOfPassengers+=type.size;
                    currentSolvency+=type.solvency;
                }
            }
            return amountOfStations;
        }

        public boolean isFinished(){
            return myAirportVisited>0;
        }

        public boolean canBeRemoved() {
            // Can only be removed if they haven't traveled anywhere and we are at the boarding port
            return amountOfStations == 0 && boardedAt.equals(current);
        }
    }

    public final Airport startingPort;
    private Airport current;
    public final PlaneType plane;
    private int currentSolvency = 0;
    private int currentAmountOfPassengers = 0;
    private double fuelSpent = 0;
    private final List<BoarderPassenger> passengers = new ArrayList<>();
    private final MapHolder<Airport, MapHolder<PassengerType, Integer>> boardedPerPort = new MapHolder<>();
    private final SetHolder<Airport> usedPorts = new SetHolder<>();
    private final MapHolder<Player, Integer> incomeChange = new MapHolder<>();
    private final List<Airline> lines = new ArrayList<>();
    private final GameData gameData;

    public Route(PlaneType plane, GameData gameData, Airport startAirport) {
        this.plane = plane;
        this.gameData = gameData;
        this.current = startAirport;
        this.startingPort = startAirport;
        usedPorts.add(startAirport);
    }


    /**
     * @return error string if error occurred, else null
     */
    public String checkPassengerAdding(PassengerType type) {
        if (current == null)
            return "Current airport is null";
        if (current.passengers.getOrDefault(type, 0) -
            boardedPerPort.getOrDefault(current, new MapHolder<>()).getOrDefault(type, 0) <= 0
        )
            return "Current airport does not have that passenger";
        if (currentAmountOfPassengers + type.size > plane.capacity)
            return "Passenger's do not fit in the plane capacity.";
        if (!type.luxuryRange.contains(plane.luxury))
            return "Plane luxury does not fit passenger's desired luxury range.";
        if (!type.capacityRange.contains(plane.capacity))
            return "Plane capacity does not fit passenger's desired capacity range.";
        return null;
    }

    public String checkPassengerAdding(PassengerType candidate, List<PassengerType> currentlySelectedInUI) {
        if (this.plane == null) {
            return "Plane is not selected yet.";
        }

        int totalPersons = 0;

        if (currentlySelectedInUI != null) {
            for (PassengerType p : currentlySelectedInUI) {
                totalPersons += p.size;
            }
        }

        totalPersons += candidate.size;

        if (totalPersons > this.plane.capacity) {
            return "Plane capacity doesn't fit for all selected passengers.";
        }
        return checkPassengerAdding(candidate);
    }

    private void unsafeAddPassenger(PassengerType type) {
        currentSolvency += type.solvency;
        currentAmountOfPassengers += type.size;
        passengers.add(new BoarderPassenger(type));
        boardedPerPort.computeIfAbsent(current.getId(), (k) -> new MapHolder<>());
        boardedPerPort.get(current).compute(type.getId(), (k, v) -> v == null ? 1 : v + 1);
    }

    /**
     * @return errormap of i -> error such that if res[i] != null then passenger
     * passengers[res[i]] can not fly to the next because of "error"
     */
    private Map<Integer, String> findWhoCanNotFlyToTheNextPort(Airport next, Airline line) {
        Map<Integer, String> res = new HashMap<>();
        for (int i = 0; i < passengers.size(); ++i) {
            String error = passengers.get(i).canFlyToTheNextPort(next, line);
            if (error != null) {
                res.put(i, error);
            }
        }
        if (res.isEmpty())
            return null;
        return res;
    }

    private void flyToTheNextPortPassengers(Airport next, Airline line) {
        if (line.player != null) {
            incomeChange.compute(line.player.getId(), (k, v) -> v == null ?
                (int) Math.round(currentSolvency * line.type.yield * plane.luxury) :
                v + (int) Math.round(currentSolvency * line.type.yield * plane.luxury));
        }

        for (BoarderPassenger psg : passengers) {
            psg.flyToTheNextPort(next);
        }
    }

    /**
     * @return error map of i -> error such that if res[i] != null then passenger
     * passengers[res[i]] can not fly to the next because of "error".
     * error at res[-1] is error caused by new airline itself
     */
    public Map<Integer, String> checkMakeFlight(Airline line) {
        if (current == null) {
            Map<Integer, String> res = new HashMap<>();
            res.put(-1, "Current airport is null");
            return res;
        }
        Airport next = line.getAnotherEnd(current);
        if (next == null) {
            Map<Integer, String> res = new HashMap<>();
            res.put(-1, "New line is not connected to current airport.");
            return res;
        }
        if(usedPorts.contains(next)){
            Map<Integer, String> res = new HashMap<>();
            res.put(-1, "Next airport was already used.");
            return res;
        }
        String error = checkAddLine(line);
        if (error != null) {
            Map<Integer, String> res = new HashMap<>();
            res.put(-1, error);
            return res;
        }
        Map<Integer, String> res = findWhoCanNotFlyToTheNextPort(next, line);
        return res;
    }

    /**
     * @return error string if error occurred
     */
    private String checkAddLine(Airline line) {
        if(lines.contains(line))
            return "This line is already a part of the route";
        if(lines.size() >= plane.stations + 2)
            return "Plane has reached it's maximum amount of stations.";
        if(line.getDistance() + fuelSpent > plane.fuel)
            return "Plane doesn't have enough fuel.";
        if (!line.type.luxuryRange.contains(plane.luxury))
            return "Plane luxury doesnt fit in airline luxury range.";
        if (!line.type.capacityRange.contains(plane.capacity))
            return "Plane capacity doesn't fit in airline capacity range.";
        if (!plane.distRange.contains(line.getDistance()))
            return "Line distanse doesn't fit in the plane's distanse range = " + line.getDistance();
        if (!plane.gateRange.contains(line.type.gateA) || !plane.gateRange.contains(line.type.gateB))
            return "Amount of gates at one of the ends of the airline does not fit the planes amount of gates range.";
        return null;
    }

    private void returnToPreviousAirportPassengers(Airline linetoBack) {
        for (int i = passengers.size() - 1; i >= 0; --i) {
            BoarderPassenger psg = passengers.get(i);
            int tmp = psg.returnToPreviousAirport();

            if (tmp < 0 || psg.canBeRemoved()) {
                removePassenger(i);
            }
        }

        if (linetoBack.player != null) {
            incomeChange.compute(linetoBack.player.getId(), (k, v) -> v == null ?
                - (int) Math.round(currentSolvency * linetoBack.type.yield * plane.luxury) :
                v - (int) Math.round(currentSolvency * linetoBack.type.yield * plane.luxury));
        }
    }

    // -- interface--

    /**
     * @return error string if error occurred, else null and adds the passenger
     */
    public String addPassenger(PassengerType type) {
        String error = checkPassengerAdding(type);
        if (error == null) {
            unsafeAddPassenger(type);
            return null;
        }
        else return error;
    }

    /**
     * @return true if succeeded
     */
    public boolean removePassenger(int ind) {
        if (ind >= 0 && ind < passengers.size() && passengers.get(ind).canBeRemoved()) {
            PassengerType type = passengers.get(ind).type;
            currentSolvency -= type.solvency;
            currentAmountOfPassengers -= type.size;
            boardedPerPort.get(current).compute(type.getId(), (k, v) -> { if (--v == 0) return null; else return v; });
            passengers.remove(ind);
            return true;
        }
        else return false;
    }

    /**
     * @return error map of i -> error such that if res[i] != null then passenger
     * passengers[res[i]] can not fly to the next because of "error"
     * error at res[-1] is error caused by new airline itself
     */
    public Map<Integer, String> makeFlight(Airline line) {
        Map<Integer, String> error = checkMakeFlight(line);
        if (error != null)
            return error;
        Airport next = line.getAnotherEnd(current);
        usedPorts.add(next);
        flyToTheNextPortPassengers(next, line);
        current = next;
        fuelSpent += line.getDistance();
        lines.add(line);
        return null;
    }

    /**
     * @return true if returned
     */
    public boolean undoFlight() {
        if (lines.isEmpty()) return false;
        Airline lineBack = lines.getLast();
        lines.removeLast();
        // 'current' is still pointing to the destination port here, which is correct for rollback logic
        usedPorts.remove(current);
        returnToPreviousAirportPassengers(lineBack);
        current = lineBack.getAnotherEnd(current);
        fuelSpent -= lineBack.getDistance();
        return true;
    }

    public Airport getCurrentAirport() { return current; }

    /**
     * to show on the ui. You should only show those passengers where psg.isFinished() == false
     */
    public List<BoarderPassenger> getPassengers() {
        return Collections.unmodifiableList(passengers);
    }

    /**
     * to show on the ui. Airlines of the route
     */
    public List<Airline> getLines() {
        return Collections.unmodifiableList(lines);
    }

    public Iterator<Map.Entry<Player, Integer>> getIncomeChangeIterator(){
        return MapHolder.viewAsEntrySet(incomeChange);
    }

    /**
     * @return passengers that can be picked in current airport
     */
    public MapHolder<PassengerType, Integer> getSuitablePassengers(){
        MapHolder<PassengerType, Integer> res = new MapHolder<>(StaticGameData.passengerTypes);
        for(Map.Entry<Integer,Integer> e : current.passengers.entrySet()){
            int takenAmount = boardedPerPort.getOrDefault(current.getId(),new MapHolder<>()).getOrDefault(e.getKey(), 0);
            if(e.getValue() - takenAmount > 0){
                PassengerType type = StaticGameData.passengerTypes.get(e.getKey());
                if(checkPassengerAdding(type)!=null)
                    res.put(type,e.getValue() - takenAmount );
            }
        }
        return res;
    }

    public int getLinesCount() {
        return lines.size();
    }

    public boolean canFinishRoute(){
        for(BoarderPassenger psg : passengers){
            if(!psg.isFinished())
                return false;
        }
        return true;
    }

    public Map<Integer, Map<Integer,Integer>> passengersForDTO(){
        Map<Integer, Map<Integer,Integer>> res = new HashMap<>();
        for(Map.Entry<Integer, MapHolder<PassengerType, Integer>> e : boardedPerPort.entrySet()){
            res.put(e.getKey(), new HashMap<>());
            res.get(e.getKey()).putAll(e.getValue());
        }
        return res;
    }

    public MapHolder<Airport, MapHolder<PassengerType, Integer>> getBoardedPerPort(){
        return boardedPerPort;
    }

    public MapHolder<Player, Integer> getIncomeChange(){return incomeChange;}
}
