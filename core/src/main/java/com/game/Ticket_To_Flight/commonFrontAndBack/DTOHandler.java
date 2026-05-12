package com.game.Ticket_To_Flight.commonFrontAndBack;

import com.badlogic.gdx.math.Vector2;
import com.game.Ticket_To_Flight.Utilities.Identifiable;
import com.game.Ticket_To_Flight.Utilities.SetHolder;
import com.game.Ticket_To_Flight.Utilities.SomethingHolder;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airline;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Passenger;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Player;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DTOHandler {
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
        @Override
        public int hashCode(){
            return id;
        }
    }
    public static class DataChangesDTO extends Identifiable{
        public final Set<PlayerDTO> playersToAdd;
        public final Set<PlayerDTO> playersToRemove;
        public final Set<AirportDTO> airportsToAdd;
        public final Set<AirportDTO> airportsToRemove;
        public final Set<AirlineDTO> airlinesToAdd;
        public final Set<AirlineDTO> airlinesToRemove;
        public final Map<Integer, Integer> availablePlanesToRemove;
        public final Map<Integer, Integer> availablePlanesToAdd;
        public final Set<PassengerDTO> passengersToRemove;
        public final Set<PassengerDTO> passengersToAdd;
        public final Map<Integer, Double> playerIncomeChange;
        public final Map<Integer, Set<Integer>> ownersAirlinesToAdd;
        public final Map<Integer, Set<Integer>> ownersAirlinesToRemove;
        public final Map<Integer, Map<Integer, Integer>> ownersPlanesToAdd;
        public final Map<Integer, Map<Integer, Integer>> ownersPlanesToRemove;

        public DataChangesDTO(int id, Set<PlayerDTO> playersToAdd, Set<PlayerDTO> playersToRemove, Set<AirportDTO> airportsToAdd, Set<AirportDTO> airportsToRemove, Set<AirlineDTO> airlinesToAdd, Set<AirlineDTO> airlinesToRemove, Map<Integer, Integer> availablePlanesToRemove, Map<Integer, Integer> availablePlanesToAdd, Set<PassengerDTO> passengersToRemove, Set<PassengerDTO> passengersToAdd, Map<Integer, Double> playerIncomeChange, Map<Integer, Set<Integer>> ownersAirlinesToAdd, Map<Integer, Set<Integer>> ownersAirlinesToRemove, Map<Integer, Map<Integer, Integer>> ownersPlanesToAdd, Map<Integer, Map<Integer, Integer>> ownersPlanesToRemove) {
            super(id);
            this.playersToAdd = playersToAdd;
            this.playersToRemove = playersToRemove;
            this.airportsToAdd = airportsToAdd;
            this.airportsToRemove = airportsToRemove;
            this.airlinesToAdd = airlinesToAdd;
            this.airlinesToRemove = airlinesToRemove;
            this.availablePlanesToRemove = availablePlanesToRemove;
            this.availablePlanesToAdd = availablePlanesToAdd;
            this.passengersToRemove = passengersToRemove;
            this.passengersToAdd = passengersToAdd;
            this.playerIncomeChange = playerIncomeChange;
            this.ownersAirlinesToAdd = ownersAirlinesToAdd;
            this.ownersAirlinesToRemove = ownersAirlinesToRemove;
            this.ownersPlanesToAdd = ownersPlanesToAdd;
            this.ownersPlanesToRemove = ownersPlanesToRemove;
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
            // Helper for Set transformation: Domain -> DTO
            Function<Set<?>, Set<?>> transformSet = (sourceSet) -> {
                if (sourceSet == null) return null;
                return sourceSet.stream()
                    .map(item -> toDTO(item)) // Using toDTO(Object) as specified
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
            Set<PlayerDTO> pAdd = (Set<PlayerDTO>) transformSet.apply(dc.playersToAdd);
            Set<PlayerDTO> pRem = (Set<PlayerDTO>) transformSet.apply(dc.playersToRemove);
            Set<AirportDTO> airpAdd = (Set<AirportDTO>) transformSet.apply(dc.airportsToAdd);
            Set<AirportDTO> airpRem = (Set<AirportDTO>) transformSet.apply(dc.airportsToRemove);
            Set<AirlineDTO> airlAdd = (Set<AirlineDTO>) transformSet.apply(dc.airlinesToAdd);
            Set<AirlineDTO> airlRem = (Set<AirlineDTO>) transformSet.apply(dc.airlinesToRemove);
            Set<PassengerDTO> passAdd = (Set<PassengerDTO>) transformSet.apply(dc.passengersToAdd);
            Set<PassengerDTO> passRem = (Set<PassengerDTO>) transformSet.apply(dc.passengersToRemove);

            // 2. Transforming maps with simple values (Key -> ID)
            Map<Integer, Integer> planesRem = (Map<Integer, Integer>) transformMapKeys.apply(dc.availablePlanesToRemove);
            Map<Integer, Integer> planesAdd = (Map<Integer, Integer>) transformMapKeys.apply(dc.availablePlanesToAdd);
            Map<Integer, Double> incomeChg = (Map<Integer, Double>) transformMapKeys.apply(dc.playerIncomeChange);

            // 3. Transforming complex maps (Owners' airlines and planes)
            Map<Integer, Set<Integer>> ownAirlinesAdd = dc.ownersAirlinesToAdd == null ? null :
                dc.ownersAirlinesToAdd.entrySet().stream()
                .collect(Collectors.toMap(
                    e -> e.getKey().getId(),
                    e -> e.getValue().stream().map(Airline::getId).collect(Collectors.toSet())
                ));

            Map<Integer, Set<Integer>> ownAirlinesRem = dc.ownersAirlinesToRemove == null ? null :
                dc.ownersAirlinesToRemove.entrySet().stream()
               .collect(Collectors.toMap(
                   e -> e.getKey().getId(),
                   e -> e.getValue().stream().map(Airline::getId).collect(Collectors.toSet())
               ));

            Map<Integer, Map<Integer, Integer>> ownPlanesAdd = dc.ownersPlanesToAdd == null ? null :
                dc.ownersPlanesToAdd.entrySet().stream()
                 .collect(Collectors.toMap(
                     e -> e.getKey().getId(),
                     e -> e.getValue().entrySet().stream()
                          .collect(Collectors.toMap(ie -> ie.getKey().getId(), Map.Entry::getValue))
                 ));

            Map<Integer, Map<Integer, Integer>> ownPlanesRem = dc.ownersPlanesToRemove == null ? null :
                dc.ownersPlanesToRemove.entrySet().stream()
                .collect(Collectors.toMap(
                    e -> e.getKey().getId(),
                    e -> e.getValue().entrySet().stream()
                         .collect(Collectors.toMap(ie -> ie.getKey().getId(), Map.Entry::getValue))
                ));

            // 4. Create the final DTO instance via constructor
            DataChangesDTO dto = new DataChangesDTO(
                dc.getId(),
                pAdd, pRem,
                airpAdd, airpRem,
                airlAdd, airlRem,
                planesRem, planesAdd,
                passRem, passAdd,
                incomeChg,
                ownAirlinesAdd, ownAirlinesRem,
                ownPlanesAdd, ownPlanesRem
            );
        }
        throw new IllegalArgumentException("Could not create data transfer object: unknown class");
    }
    public static Object fromDTO(Object o, GameData data){
        if(o==null) return null;
        if(o instanceof AirlineDTO){
            if(data == null) return null;

            AirlineDTO dto = (AirlineDTO) o;
            AirlineType type = GameData.airlineTypes.get(dto.type);
            Airport portA = data.airports.get(dto.portA);
            Airport portB = data.airports.get(dto.portB);
            if(type == null || portA == null || portB == null) return null;
            if(dto.player==null)
                return new Airline(dto.id, type, portA, portB, null);
            else{
                Player player = data.players.get(dto.player);
                if(player==null) return null;
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
            PassengerType type = GameData.passengerTypes.get(dto.type);
            Airport portFrom = data.airports.get(dto.portFrom);
            Airport portTo = data.airports.get(dto.portTo);
            CityType typeTo = GameData.cityTypes.get(dto.typeTo);
            if(type==null || portFrom==null) return null;
            if((typeTo == null && portTo == null) || (typeTo != null && portTo != null)) return null;
            return new Passenger(dto.id, type, portFrom, portTo, typeTo);
        }
        else if(o instanceof PlayerDTO){
            if(data == null) return null;

            PlayerDTO dto = (PlayerDTO) o;

            SetHolder<Airline> lines = new SetHolder<>();
            for(Integer lineid : dto.airlines){
                Airline  line = data.airlines.get(lineid);
                if(line == null) return null;
                lines.add(line);
            }
            SomethingHolder<PlaneType, Integer> planes = new SomethingHolder<>();
            for(Map.Entry<Integer, Integer> entry : dto.planes.entrySet()){
                PlaneType plane = GameData.planeTypes.get(entry.getKey());
                if(plane == null) return null;
                planes.put(plane, entry.getValue());
            }
            return new Player(dto.id, dto.money, dto.income, planes, lines);
        }
        else if(o instanceof DataChangesDTO){
            //fuck
            return null;
        }
        throw new IllegalArgumentException("Could not create an object from data transfer object: unknown class.");
    }
}
