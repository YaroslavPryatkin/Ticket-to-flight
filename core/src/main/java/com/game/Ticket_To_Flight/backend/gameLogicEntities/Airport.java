package com.game.Ticket_To_Flight.backend.gameLogicEntities;

import com.badlogic.gdx.math.Vector2;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.AirportType;
import com.game.Ticket_To_Flight.Utilities.Identifiable;
import com.badlogic.gdx.graphics.Color;

public class Airport extends Identifiable {
    public final AirportType type;
    public final Vector2 position;

    public Airport(int id, AirportType type, Vector2 position) {
        super(id);
        if(type == null || position == null) throw new IllegalArgumentException("Null arguments in constructor.");
        this.type = type;
        this.position = position;
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

}
