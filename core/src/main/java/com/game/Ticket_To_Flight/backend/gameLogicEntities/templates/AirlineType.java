package com.game.Ticket_To_Flight.backend.gameLogicEntities.templates;

import com.game.Ticket_To_Flight.Utilities.ClosedRange;

public class AirlineType {
    public final int id;
    public final double yield;
    public final int gateA;
    public final int gateB;
    public final ClosedRange<Double> luxuryRange;
    public final ClosedRange<Integer> capacityRange;
    public final double price;
    public final String description;

    public AirlineType(int id, double yield, int gateA, int gateB, ClosedRange<Double> luxuryRange, ClosedRange<Integer> capacityRange, double price, String description) {
        this.id = id;
        this.yield = yield;
        this.gateA = gateA;
        this.gateB = gateB;
        this.luxuryRange = new ClosedRange<>(luxuryRange);
        this.capacityRange = new ClosedRange<>(capacityRange);
        this.price = price;
        this.description = description;
    }
}
