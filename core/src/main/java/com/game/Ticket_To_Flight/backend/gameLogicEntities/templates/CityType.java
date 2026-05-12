package com.game.Ticket_To_Flight.backend.gameLogicEntities.templates;

import com.game.Ticket_To_Flight.Utilities.Identifiable;

public class CityType extends Identifiable {
    public final String description;

    public CityType(int id, String description) {
        super(id);
        this.description = description;
    }
}
