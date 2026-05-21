package com.game.Ticket_To_Flight.backend.gameLogicEntities.templates;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.game.Ticket_To_Flight.Utilities.Identifiable;

public class AbilityType extends Identifiable {
    public String description;
    @JsonCreator
    public AbilityType(
        @JsonProperty int id,
        @JsonProperty String description
    ){
        super(id);
        this.description = description;
    }
}
