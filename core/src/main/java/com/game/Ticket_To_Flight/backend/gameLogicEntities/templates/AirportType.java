package com.game.Ticket_To_Flight.backend.gameLogicEntities.templates;

public class AirportType {
    public final int id;
    public final double cost;
    public final int gateAmount;
    public final int cityTypeId;
    public final String description;

    public AirportType(int id, double cost, int gateAmount, int cityTypeId, String description) {
        this.id = id;
        this.cost = cost;
        this.gateAmount = gateAmount;
        this.cityTypeId = cityTypeId;
        this.description = description;
    }
}
