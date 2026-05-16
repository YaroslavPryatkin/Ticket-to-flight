package com.game.Ticket_To_Flight.backend.gameLogicEntities.templates;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.game.Ticket_To_Flight.Utilities.Identifiable;

public class CityType extends Identifiable {
    public final String description;

    @JsonCreator
    public CityType(
        @JsonProperty("id") int id,
        @JsonProperty("description") String description
    ) {
        super(id);
        this.description = description;
    }

    public String getCity() {
        return description;
    }
}
