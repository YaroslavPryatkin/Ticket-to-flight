package com.game.Ticket_To_Flight.backend.gameLogicEntities;

import com.badlogic.gdx.math.Vector2;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.AirportType;
import com.game.Ticket_To_Flight.Utilities.Identifiable;

public class Airport extends Identifiable {
    public final AirportType type;
    public final Vector2 position;

    public Airport(int id, AirportType type, Vector2 position) {
        super(id);
        if(type == null || position == null) throw new IllegalArgumentException("Null arguments in constructor.");
        this.type = type;
        this.position = position;
    }
}
