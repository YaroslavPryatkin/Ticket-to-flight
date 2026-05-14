package com.game.Ticket_To_Flight.backend.gameLogicEntities.templates;

import com.game.Ticket_To_Flight.Utilities.Identifiable;

public class AirportType extends Identifiable {
    public final double cost;
    public final int gateAmount;
    public final CityType cityType;
    public final String description;

    public AirportType(int id, double cost, int gateAmount, CityType cityTypeId, String description) {
        super(id);
        this.cost = cost;
        this.gateAmount = gateAmount;
        this.cityType = cityTypeId;
        this.description = description;
    }

    public String getCityType() {
        return cityType.getCity();
    }
}
