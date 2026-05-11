package com.game.Ticket_To_Flight.backend.gameLogicEntities.templates;

public class AirportType {
    public final int id;
    public final double cost;
    public final int gateAmount;
    public final CityType cityType;
    public final String description;

    public AirportType(int id, double cost, int gateAmount, CityType cityTypeId, String description) {
        this.id = id;
        this.cost = cost;
        this.gateAmount = gateAmount;
        this.cityType = cityTypeId;
        this.description = description;
    }
}
