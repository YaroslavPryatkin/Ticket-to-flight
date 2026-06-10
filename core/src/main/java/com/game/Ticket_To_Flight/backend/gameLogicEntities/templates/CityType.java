package com.game.Ticket_To_Flight.backend.gameLogicEntities.templates;

import com.badlogic.gdx.graphics.Color;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.game.Ticket_To_Flight.Utilities.Identifiable;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;

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
    public Color getColor(){
        if(id == 1)
            return Color.GREEN;
        if(id == 2)
            return Color.RED;
        if(id == 3)
            return Color.GRAY;
        if(id == 4)
            return Color.PINK;
        if(id == 5)
            return Color.MAGENTA;
        return Color.WHITE;
    }
}
