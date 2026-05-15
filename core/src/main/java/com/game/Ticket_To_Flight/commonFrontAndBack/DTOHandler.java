package com.game.Ticket_To_Flight.commonFrontAndBack;

import com.badlogic.gdx.math.Vector2;
import com.game.Ticket_To_Flight.Utilities.Identifiable;
import com.game.Ticket_To_Flight.Utilities.SetHolder;
import com.game.Ticket_To_Flight.Utilities.MapHolder;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airline;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Passenger;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Player;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.*;
import com.sun.source.tree.IdentifierTree;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DTOHandler {
    /*
    public static class AirlineDTO extends Identifiable {
        public final int type;
        public final int portA;
        public final int portB;
        public final Integer player;

        public AirlineDTO(int id, int type, Integer portA, Integer portB, Integer player) {
            super(id);
            this.type = type;
            this.portA = portA;
            this.portB = portB;
            this.player = player;
        }
    }
    public static class AirportDTO extends Identifiable{
        public final Integer type;
        public final Vector2 position;

        public AirportDTO(int id, Integer type, Vector2 position) {
            super(id);
            this.type = type;
            this.position = position;
        }
    }
    public static class PassengerDTO extends Identifiable{
        public final int type;
        public final int portFrom;
        public final Integer portTo;
        public final Integer typeTo;

        public PassengerDTO(int id, int type, int portFrom, Integer portTo, Integer typeTo) {
            super(id);
            this.type = type;
            this.portFrom = portFrom;
            this.portTo = portTo;
            this.typeTo = typeTo;
        }
    }
    public static class PlayerDTO extends Identifiable{
        public final double money;
        public final double income;
        public final Map<Integer, Integer> planes;
        public final Set<Integer> airlines;

        public PlayerDTO(int id, double money, double income, Map<Integer, Integer> planes, Set<Integer> airlines) {
            super(id);
            this.money = money;
            this.income = income;
            this.planes = planes;
            this.airlines = airlines;
        }
    }
    public static class DataChangesDTO extends Identifiable{
        public final Set<PlayerDTO> playersToAdd;
        public final Set<Integer> playersToRemove;
        public final Set<AirportDTO> airportsToAdd;
        public final Set<Integer> airportsToRemove;
        public final Set<AirlineDTO> airlinesToAdd;
        public final Set<Integer> airlinesToRemove;
        public final Set<PassengerDTO> passengersToAdd;
        public final Set<Integer> passengersToRemove;

        public final GameData.State currentState;
        public final Integer currentPlayer;
        public final Set<Integer> newWorldEvents;

        public final Set<Integer> availableAirlinesToAdd;
        public final Set<Integer> availableAirlinesToRemove;
        public final Map<Integer, Integer> availablePlanesToRemove;
        public final Map<Integer, Integer> availablePlanesToAdd;

        public final Map<Integer, Double> playerMoneyChange;
        public final Map<Integer, Double> playerIncomeChange;
        public final Map<Integer, Integer> playerActionPointsChange;

        public final Map<Integer, Set<Integer>> playerAirlinesToAdd;
        public final Map<Integer, Set<Integer>> playerAirlinesToRemove;
        public final Map<Integer, Map<Integer, Integer>> playerPlanesToAdd;
        public final Map<Integer, Map<Integer, Integer>> playerPlanesToRemove;

        public DataChangesDTO(int id, Set<PlayerDTO> playersToAdd, Set<Integer> playersToRemove,
                              Set<AirportDTO> airportsToAdd, Set<Integer> airportsToRemove,
                              Set<AirlineDTO> airlinesToAdd, Set<Integer> airlinesToRemove,
                              Set<PassengerDTO> passengersToAdd, Set<Integer> passengersToRemove,
                              GameData.State currentState, Integer currentPlayer, Set<Integer> newWorldEvents,
                              Set<Integer> availableAirlinesToAdd, Set<Integer> availableAirlinesToRemove,
                              Map<Integer, Integer> availablePlanesToRemove, Map<Integer, Integer> availablePlanesToAdd,
                              Map<Integer, Double> playerMoneyChange, Map<Integer, Double> playerIncomeChange,
                              Map<Integer, Integer> playerActionPointsChange, Map<Integer, Set<Integer>> playerAirlinesToAdd,
                              Map<Integer, Set<Integer>> playerAirlinesToRemove,
                              Map<Integer, Map<Integer, Integer>> playerPlanesToAdd,
                              Map<Integer, Map<Integer, Integer>> playerPlanesToRemove) {
            super(id);
            this.playersToAdd = playersToAdd;
            this.playersToRemove = playersToRemove;
            this.airportsToAdd = airportsToAdd;
            this.airportsToRemove = airportsToRemove;
            this.airlinesToAdd = airlinesToAdd;
            this.airlinesToRemove = airlinesToRemove;
            this.passengersToAdd = passengersToAdd;
            this.passengersToRemove = passengersToRemove;
            this.currentState = currentState;
            this.currentPlayer = currentPlayer;
            this.newWorldEvents = newWorldEvents;
            this.availableAirlinesToAdd = availableAirlinesToAdd;
            this.availableAirlinesToRemove = availableAirlinesToRemove;
            this.availablePlanesToRemove = availablePlanesToRemove;
            this.availablePlanesToAdd = availablePlanesToAdd;
            this.playerMoneyChange = playerMoneyChange;
            this.playerIncomeChange = playerIncomeChange;
            this.playerActionPointsChange = playerActionPointsChange;
            this.playerAirlinesToAdd = playerAirlinesToAdd;
            this.playerAirlinesToRemove = playerAirlinesToRemove;
            this.playerPlanesToAdd = playerPlanesToAdd;
            this.playerPlanesToRemove = playerPlanesToRemove;
        }
    }


    public static Object toDTO(Object o){
        if(o==null) return null;
        if(o instanceof Airline){
            Airline line = (Airline) o;
            if(line.player == null)
                return new AirlineDTO(line.id, line.type.id, line.portA.id, line.portB.id, null);
            else
                return new AirlineDTO(line.id, line.type.id, line.portA.id, line.portB.id, line.player.id);
        }
        else if(o instanceof Airport){
            Airport port = (Airport) o;
            return new AirportDTO(port.id, port.type.id, port.position);
        }
        else if(o instanceof Passenger){
            Passenger psg = (Passenger) o;
            if(psg.portTo==null)
                return new PassengerDTO(psg.id, psg.type.id, psg.portFrom.id, null, psg.typeTo.id);
            else if(psg.typeTo == null)
                return new PassengerDTO(psg.id, psg.type.id, psg.portFrom.id, psg.portTo.id, null);
            throw new IllegalArgumentException("Wrong passenger data.");
        }
        else if(o instanceof Player){
            Player pl = (Player) o;
            Set<Integer> lines = new HashSet<>();
            for(Airline line : pl.airlines){
                lines.add(line.getId());
            }
            Map<Integer, Integer> planes = new HashMap<>();
            for(Map.Entry<PlaneType, Integer> plane : pl.planes.entrySet()){
                planes.put(plane.getKey().getId(), plane.getValue());
            }
            return new PlayerDTO(pl.id, pl.money, pl.income, planes, lines);
        }
        else if(o instanceof GameData.DataChanges){
            GameData.DataChanges dc = (GameData.DataChanges) o;

            Function<Set<? extends Identifiable>, Set<?>> SetToDTO = (sourceSet) -> {
                if (sourceSet == null) return null;
                return sourceSet.stream()
                    .map(item -> toDTO(item))
                    .collect(Collectors.toSet());
            };

            Function<Set<? extends Identifiable>, Set<Integer>> SetToInteger = (sourceSet) -> {
                if (sourceSet == null) return null;
                return sourceSet.stream()
                    .map(item -> item.getId())
                    .collect(Collectors.toSet());
            };

            // Helper for Map transformation: Map<Identifiable, V> -> Map<Integer, V>
            Function<Map<? extends Identifiable, ?>, Map<Integer, ?>> transformMapKeys = (sourceMap) -> {
                if (sourceMap == null) return null;
                return sourceMap.entrySet().stream()
                    .collect(Collectors.toMap(
                        e -> e.getKey().getId(),
                        Map.Entry::getValue
                    ));
            };

            // 1. Transforming simple sets
            Set<PlayerDTO> pAdd = (Set<PlayerDTO>) SetToDTO.apply(dc.playersToAdd);
            Set<Integer> pRem = SetToInteger.apply(dc.playersToRemove);
            Set<AirportDTO> airpAdd = (Set<AirportDTO>) SetToDTO.apply(dc.airportsToAdd);
            Set<Integer> airpRem = SetToInteger.apply(dc.airportsToRemove);
            Set<AirlineDTO> airlAdd = (Set<AirlineDTO>) SetToDTO.apply(dc.airlinesToAdd);
            Set<Integer> airlRem = SetToInteger.apply(dc.airlinesToRemove);
            Set<PassengerDTO> passAdd = (Set<PassengerDTO>) SetToDTO.apply(dc.passengersToAdd);
            Set<Integer> passRem =  SetToInteger.apply(dc.passengersToRemove);
            Set<Integer> avLineAdd = SetToInteger.apply(dc.availableAirlinesToAdd);
            Set<Integer> avLineRem = SetToInteger.apply(dc.availableAirlinesToRemove);
            Set<Integer> newEvents = SetToInteger.apply(dc.newWorldEvents);

            // 2. Transforming maps with simple values (Key -> ID)
            Map<Integer, Integer> planesAdd = (Map<Integer, Integer>) transformMapKeys.apply(dc.availablePlanesToAdd);
            Map<Integer, Integer> planesRem = (Map<Integer, Integer>) transformMapKeys.apply(dc.availablePlanesToRemove);
            Map<Integer, Double> incomeChg = (Map<Integer, Double>) transformMapKeys.apply(dc.playerIncomeChange);
            Map<Integer, Double> moneyChg = (Map<Integer, Double>) transformMapKeys.apply(dc.playerMoneyChange);
            Map<Integer, Integer> actChg = (Map<Integer, Integer>) transformMapKeys.apply(dc.playerActionPointsChange);

            // 3. Transforming complex maps (Owners' airlines and planes)
            Map<Integer, Set<Integer>> plAirlinesAdd = dc.ownersAirlinesToAdd == null ? null :
                dc.ownersAirlinesToAdd.entrySet().stream()
                .collect(Collectors.toMap(
                    e -> e.getKey().getId(),
                    e -> e.getValue().stream().map(Airline::getId).collect(Collectors.toSet())
                ));

            Map<Integer, Set<Integer>> plAirlinesRem = dc.ownersAirlinesToRemove == null ? null :
                dc.ownersAirlinesToRemove.entrySet().stream()
               .collect(Collectors.toMap(
                   e -> e.getKey().getId(),
                   e -> e.getValue().stream().map(Airline::getId).collect(Collectors.toSet())
               ));

            Map<Integer, Map<Integer, Integer>> plPlanesAdd = dc.ownersPlanesToAdd == null ? null :
                dc.ownersPlanesToAdd.entrySet().stream()
                 .collect(Collectors.toMap(
                     e -> e.getKey().getId(),
                     e -> e.getValue().entrySet().stream()
                          .collect(Collectors.toMap(ie -> ie.getKey().getId(), Map.Entry::getValue))
                 ));

            Map<Integer, Map<Integer, Integer>> plPlanesRem = dc.ownersPlanesToRemove == null ? null :
                dc.ownersPlanesToRemove.entrySet().stream()
                .collect(Collectors.toMap(
                    e -> e.getKey().getId(),
                    e -> e.getValue().entrySet().stream()
                         .collect(Collectors.toMap(ie -> ie.getKey().getId(), Map.Entry::getValue))
                ));

            // 4. Create the final DTO instance via constructor
            DataChangesDTO dto = new DataChangesDTO(
                dc.getId(), pAdd, pRem, airpAdd, airpRem, airlAdd, airlRem, passAdd, passRem, dc.currentState, dc.currentPlayer.getId(),
                newEvents, avLineAdd, avLineRem, planesAdd, planesRem, moneyChg, incomeChg, actChg, plAirlinesAdd, plAirlinesRem,
                plPlanesAdd, plPlanesRem);
        }
        throw new IllegalArgumentException("Could not create data transfer object: unknown class");
    }

    public static Object fromDTO(Object o, GameData data){
        return fromDTO(o,data,null);
    }

    private static <T extends Identifiable, D extends Identifiable> boolean parseAddRemoveDTO(
        Set<D> toAdd, Set<Integer> toRemove, SetHolder<T> dcAdd,
        SetHolder<T> dcRemove, SetHolder<T> cur, GameData curGD, GameData.DataChanges curDC){

        boolean hasAdd = toAdd != null;
        boolean hasRemove = toRemove != null;

        if (hasAdd && dcAdd == null) return false;
        if (hasRemove && dcRemove == null) return false;

        if(hasAdd && hasRemove){
            for (D d : toAdd) {
                Integer id = d.getId();
                T fromCur = cur.get(id);
                boolean removeContains = toRemove.contains(id);

                //delete old version, add new
                if(fromCur != null && removeContains){
                    dcRemove.add(fromCur);
                    toRemove.remove(id);
                    T newObj = (T) fromDTO(d, curGD, curDC);
                    if (newObj == null) return false;
                    dcAdd.add(newObj);
                }
                //add new object with same id, error
                else if(fromCur != null){
                    return false;
                }
                //add and immediately delete
                else if(removeContains){
                    toRemove.remove(id);
                }
                //add new
                else{
                    T newObj = (T) fromDTO(d, curGD, curDC);
                    if (newObj == null) return false;
                    dcAdd.add(newObj);
                }
            }
            //delete old objects
            for(Integer id : toRemove){
                T fromCur = cur.get(id);
                if(fromCur == null) return false;
                dcRemove.add(fromCur);
            }
        }
        //only adding
        else if(hasAdd){
            for (D d : toAdd) {
                T newObj = (T) fromDTO(d, curGD, curDC);
                if (newObj == null) return false;
                dcAdd.add(newObj);
            }
        }
        //only deleting
        else if(hasRemove){
            for(Integer id : toRemove){
                T fromCur = cur.get(id);
                if(fromCur == null) return false;
                dcRemove.add(fromCur);
            }
        }
        return true;
    }

    private static boolean parseAvailableAirlines(
        Set<Integer> toAdd, Set<Integer> toRemove, GameData curGD, GameData.DataChanges curDC){
        boolean hasAdd = toAdd != null;
        boolean hasRemove = toRemove != null;

        if (hasAdd && curDC.availableAirlinesToAdd == null) return false;
        if (hasRemove && curDC.availableAirlinesToRemove == null) return false;

        if(hasAdd && hasRemove){
            for(Integer id : toRemove){
                Airline line = curGD.availableAirlines.get(id);
                if(line == null){
                    if(toAdd.contains(id)){
                        toAdd.remove(id);
                    }
                    else {
                        return false;
                    }
                }
                else{
                    curDC.availableAirlinesToRemove.add(line);
                }
            }
            for(Integer id : toAdd){
                Airline line = curGD.airlines.get(id);
                if(line == null) return false;
                else curDC.availableAirlinesToAdd.add(line);
            }
        }
        else if(hasAdd){
            for(Integer id : toAdd){
                Airline line = curGD.airlines.get(id);
                if(line == null) return false;
                else curDC.availableAirlinesToAdd.add(line);
            }
        }
        else if(hasRemove){
            for(Integer id : toRemove){
                Airline line = curGD.availableAirlines.get(id);
                if(line == null) {
                    return false;
                }
                else{
                    curDC.availableAirlinesToRemove.add(line);
                }
            }
        }
        return true;
    }

    private static <T extends Identifiable> T findInThree(int id, SetHolder<T> toAdd, SetHolder<T> toRem, SetHolder<T> cur){
        T ad = toAdd.get(id);
        T rm = toRem.get(id);
        T cu = cur.get(id);
        if(ad!=null){
            if(rm!=null){
                if(cu != null){
                    return ad;
                }
                return null;
            }
            if(cu != null){
                return null;
            }
            return ad;
        }
        else{
            if(rm!=null){
                return null;
            }
            if(cu != null){
                return null;
            }
            return cu;
        }
    }

    private static Object fromDTO(Object o, GameData data, GameData.DataChanges currentDT){
        if(o==null) return null;
        if(o instanceof AirlineDTO){
            if(data == null) return null;

            AirlineDTO dto = (AirlineDTO) o;

            AirlineType type = GameData.airlineTypes.get(dto.type);
            if(type == null) return null;

            Airport portA = data.airports.get(dto.portA);
            if(portA == null){
                portA = currentDT.airportsToAdd.get(dto.portA);
                if(portA == null) return null;
            }
            Airport portB = data.airports.get(dto.portB);
            if(portB == null){
                portB = currentDT.airportsToAdd.get(dto.portB);
                if(portB == null) return null;
            }

            if(dto.player==null)
                return new Airline(dto.id, type, portA, portB, null);
            else{
                Player player = data.players.get(dto.player);
                if(player==null){
                    player = currentDT.playersToAdd.get(dto.player);
                    if(player==null)return null;
                }
                return new Airline(dto.id, type, portA, portB, player);
            }
        }
        else if(o instanceof AirportDTO){
            AirportDTO dto = (AirportDTO) o;
            AirportType type = GameData.airportTypes.get(dto.type);
            if(type == null) return null;
            return new Airport(dto.id,type,dto.position);
        }
        else if(o instanceof PassengerDTO){
            if(data == null) return null;

            PassengerDTO dto = (PassengerDTO) o;
            if((dto.typeTo == null && dto.portTo == null) || (dto.typeTo != null && dto.portTo != null)) return null;

            PassengerType type = GameData.passengerTypes.get(dto.type);
            if(type == null)  return null;
            Airport portFrom = data.airports.get(dto.portFrom);
            if(portFrom==null){
                portFrom = currentDT.airportsToAdd.get(dto.portFrom);
                if(portFrom==null) return null;
            }
            Airport portTo = null;
            if(dto.portTo!=null) {
                portTo = data.airports.get(dto.portTo);
                if(portTo == null){
                    portTo = currentDT.airportsToAdd.get(dto.portTo);
                    if(portTo==null) return null;
                }
            }
            CityType typeTo = null;
            if(dto.typeTo!=null){
                typeTo = GameData.cityTypes.get(dto.typeTo);
            }

            return new Passenger(dto.id, type, portFrom, portTo, typeTo);
        }
        else if(o instanceof PlayerDTO){
            if(data == null) return null;

            PlayerDTO dto = (PlayerDTO) o;

            SetHolder<Airline> lines = new SetHolder<>();
            for(Integer lineid : dto.airlines){
                Airline  line = data.airlines.get(lineid);
                if(line == null){
                    line = currentDT.airlinesToAdd.get(lineid);
                    if(line == null) return null;
                }
                lines.add(line);
            }
            MapHolder<PlaneType, Integer> planes = new MapHolder<>(GameData.planeTypes);
            for(Map.Entry<Integer, Integer> entry : dto.planes.entrySet()){
                PlaneType plane = GameData.planeTypes.get(entry.getKey());
                if(plane == null) return null;
                planes.put(plane, entry.getValue());
            }
            return new Player(dto.id, dto.money, dto.income, planes, lines);
        }
        else if(o instanceof DataChangesDTO){
            if(data == null) return null;
            DataChangesDTO dto = (DataChangesDTO) o;
            GameData.DataChanges dc = new GameData.DataChanges();

            if(dto.playersToAdd != null) dc.playersToAdd = new SetHolder<>();
            if(dto.playersToRemove != null) dc.playersToRemove = new SetHolder<>();
            if(!parseAddRemoveDTO(dto.playersToAdd, dto.playersToRemove, dc.playersToAdd, dc.playersToRemove,
                data.players, data, dc)) return null;
            if(dto.airportsToAdd != null) dc.airportsToAdd = new SetHolder<>();
            if(dto.airportsToRemove != null) dc.airportsToRemove = new SetHolder<>();
            if(!parseAddRemoveDTO(dto.airportsToAdd, dto.airportsToRemove, dc.airportsToAdd, dc.airportsToRemove,
                data.airports, data, dc)) return null;
            if(dto.airlinesToAdd != null) dc.airlinesToAdd = new SetHolder<>();
            if(dto.airlinesToRemove != null) dc.airlinesToRemove = new SetHolder<>();
            if(!parseAddRemoveDTO(dto.airlinesToAdd, dto.airlinesToRemove, dc.airlinesToAdd, dc.airlinesToRemove,
                data.airlines, data, dc)) return null;
            if(dto.passengersToAdd != null) dc.passengersToAdd = new SetHolder<>();
            if(dto.passengersToRemove != null) dc.passengersToRemove = new SetHolder<>();
            if(!parseAddRemoveDTO(dto.passengersToAdd, dto.passengersToRemove, dc.passengersToAdd, dc.passengersToRemove,
                data.passengers, data, dc)) return null;
            if(dto.availableAirlinesToAdd != null) dc.availableAirlinesToAdd = new SetHolder<>();
            if(dto.availableAirlinesToRemove != null) dc.availableAirlinesToRemove = new SetHolder<>();
            if(!parseAvailableAirlines(dto.availableAirlinesToAdd, dto.availableAirlinesToRemove,
                data,dc)) return null;

            dc.currentState = dto.currentState;

            Player pl = findInThree(dto.currentPlayer, dc.playersToAdd, dc.playersToRemove, data.players);
            if(pl==null) return null;
            dc.currentPlayer=pl;

            if(dto.newWorldEvents!=null) dc.newWorldEvents=new SetHolder<>();
            for(Integer id : dto.newWorldEvents){
                WorldEventType tp = GameData.worldEventTypes.get(id);
                if(tp == null) return null;
                dc.newWorldEvents.add(tp);
            }



            return null;
        }
        throw new IllegalArgumentException("Could not recreate an object from data transfer object: unknown class.");
    }
    */
}

