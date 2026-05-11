package com.game.Ticket_To_Flight.commonFrontAndBack;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.math.Vector2;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airline;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Passenger;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Player;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.*;

import java.util.ArrayList;
import java.util.List;

public class DTOHandler {
    public static class AirlineDTO{
        public final int id;
        public final int type;
        public final int portA;
        public final int portB;
        public final Integer player;

        public AirlineDTO(int id, int type, Integer portA, Integer portB, Integer player) {
            this.id = id;
            this.type = type;
            this.portA = portA;
            this.portB = portB;
            this.player = player;
        }
    }
    public static class AirportDTO{
        public final int id;
        public final Integer type;
        public final Vector2 position;

        public AirportDTO(int id, Integer type, Vector2 position) {
            this.id = id;
            this.type = type;
            this.position = position;
        }
    }
    public static class PassengerDTO{
        public final int id;
        public final int type;
        public final int portFrom;
        public final Integer portTo;
        public final Integer typeTo;

        public PassengerDTO(int id, int type, int portFrom, Integer portTo, Integer typeTo) {
            this.id = id;
            this.type = type;
            this.portFrom = portFrom;
            this.portTo = portTo;
            this.typeTo = typeTo;
        }
    }
    public static class PlayerDTO{
        public final int id;
        public final double money;
        public final double income;
        public final List<Integer> planes;
        public final List<Integer> airlines;

        public PlayerDTO(int id, double money, double income, List<Integer> planes, List<Integer> airlines) {
            this.id = id;
            this.money = money;
            this.income = income;
            this.planes = planes;
            this.airlines = airlines;
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
            List<Integer> lines = new ArrayList<>();
            for(Airline line : pl.airlines){
                lines.add(line.id);
            }
            List<Integer> planes = new ArrayList<>();
            for(PlaneType plane : pl.planes){
                planes.add(plane.id);
            }
            return new PlayerDTO(pl.id, pl.money, pl.income, planes, lines);
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

            List<Airline> lines = new ArrayList<>();
            for(Integer lineid : dto.airlines){
                Airline  line = data.airlines.get(lineid);
                if(line == null) return null;
                lines.add(line);
            }
            List<PlaneType> planes = new ArrayList<>();
            for(Integer planeid : dto.planes){
                PlaneType plane = GameData.planeTypes.get(planeid);
                if(plane == null) return null;
                planes.add(plane);
            }
            return new Player(dto.id, dto.money, dto.income, planes, lines);
        }
        throw new IllegalArgumentException("Could not create an object from data transfer object: unknown class.");
    }
}
