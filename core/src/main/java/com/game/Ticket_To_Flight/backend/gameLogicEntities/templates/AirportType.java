package com.game.Ticket_To_Flight.backend.gameLogicEntities.templates;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.game.Ticket_To_Flight.Utilities.Identifiable;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;

public class AirportType extends Identifiable {
    public final double cost;
    public final int gateAmount;
    public final CityType cityType;
    public final String description;

    public AirportType(int id, double cost, int gateAmount, CityType cityType, String description) {
        super(id);
        this.cost = cost;
        this.gateAmount = gateAmount;
        this.cityType = cityType;
        this.description = description;
    }

    @JsonCreator
    public AirportType(
        @JsonProperty("id") int id,
        @JsonProperty("cost") double cost,
        @JsonProperty("gateAmount") int gateAmount,
        @JsonProperty("cityType") int cityType,
        @JsonProperty("description") String description
    ) {
        super(id);
        this.cost = cost;
        this.gateAmount = gateAmount;
        this.cityType = GameData.cityTypes.get(cityType);
        this.description = description;
    }

    public String getCityType() {
        return cityType.getCity();
    }
}
