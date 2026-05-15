package com.game.Ticket_To_Flight.backend.gameLogicEntities;

import com.badlogic.gdx.math.Vector2;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.AirportType;
import com.game.Ticket_To_Flight.Utilities.Identifiable;
import com.badlogic.gdx.graphics.Color;

import java.util.ArrayList;
import java.util.List;

public class Airport extends Identifiable {
    public final AirportType type;
    public final Vector2 position;
    public final List<Passenger> passengers = new ArrayList<>();
    public final String airportName;

    public Airport(int id, AirportType type, Vector2 position, String AirportName) {
        super(id);
        if(type == null || position == null) throw new IllegalArgumentException("Null arguments in constructor.");
        this.type = type;
        this.position = position;
        this.airportName = AirportName;
    }

    public Color getColor() {
        if (this.type.id == 1) {
            return Color.WHITE;
        }
        if (this.type.id == 2) {
            return Color.BLUE;
        }
        if (this.type.id == 3) {
            return Color.GREEN;
        }
        if (this.type.id == 4) {
            return Color.RED;
        }
        return null;
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

    public List<Passenger> getGuests() {
        return passengers;
    }

    public void addPassengers(Passenger group) {
        passengers.add(group);
    }

}
