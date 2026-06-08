package com.game.Ticket_To_Flight.backend.gameLogicEntities;

import com.badlogic.gdx.math.Vector2;
import com.game.Ticket_To_Flight.Utilities.MapHolder;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.AirportType;
import com.game.Ticket_To_Flight.Utilities.Identifiable;
import com.badlogic.gdx.graphics.Color;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PassengerType;
import com.game.Ticket_To_Flight.commonFrontAndBack.StaticGameData;

import java.util.Map;

public class Airport extends Identifiable {
    public final AirportType type;
    public final Vector2 position;
    public final MapHolder<PassengerType, Integer> passengers;
    public final String airportName;
    private int takenGates = 0;

    /**
     * Should not be called anywhere except game data
     */
    public Airport(int id, AirportType type, Vector2 position, String AirportName, MapHolder<PassengerType, Integer> passengers, int takenGates) {
        super(id);
        if(type == null || position == null) throw new IllegalArgumentException("Null arguments in constructor.");
        this.type = type;
        this.position = position;
        this.airportName = AirportName;
        this.passengers = passengers;
        this.takenGates = takenGates;
    }

    public Float getX() {
        return position.x;
    }

    public Float getY() {
        return position.y;
    }

    public Float getRadius() {
        if (this.type.id == 1) {
            return 6f;
        }
        if (this.type.id == 2) {
            return 9f;
        }
        if (this.type.id == 3) {
            return 12f;
        }
        if (this.type.id == 4) {
            return 15f;
        }
        return 6f;
    }

    public String getCityName() {
        return airportName;
    }

    public MapHolder<PassengerType, Integer> getGuests() {
        return this.passengers;
    }

    @Override
    public String toString(){
        String ans  = "Airport [" + airportName + "] of type [" + type.getId() + "]\n PassengerType -> Amount";
        for(Map.Entry<Integer, Integer> e : passengers.entrySet()){
            ans += "\n    " + e.getKey() + " -> " + e.getValue();
        }
        return ans;
    }

    public int getTakenGates(){return takenGates;}

    public void setTakenGates(int val){takenGates = val;}

    public int getFreeGates(){return type.gateAmount - takenGates;}
}
