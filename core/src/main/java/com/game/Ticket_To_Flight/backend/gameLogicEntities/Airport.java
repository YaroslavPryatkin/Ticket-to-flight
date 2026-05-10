package com.game.Ticket_To_Flight.backend.gameLogicEntities;

import com.badlogic.gdx.math.Vector2;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.AirportType;

public class Airport {
    public final AirportType type;
    public final int id;
    public final Vector2 position;

    public Airport(AirportType type, Vector2 position, int id) {
        this.type = type;
        this.position = position;
        this.id = id;
    }
}
