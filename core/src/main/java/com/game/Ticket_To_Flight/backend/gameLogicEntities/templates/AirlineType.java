package com.game.Ticket_To_Flight.backend.gameLogicEntities.templates;

import com.game.Ticket_To_Flight.Utilities.ClosedInterval;
import com.game.Ticket_To_Flight.Utilities.Identifiable;

public class AirlineType extends Identifiable {
    public final double yield;
    public final int gateA;
    public final int gateB;
    public final ClosedInterval<Double> luxuryRange;
    public final ClosedInterval<Integer> capacityRange;
    public final double price;
    public final String description;

    public AirlineType(int id, double yield, int gateA, int gateB, ClosedInterval<Double> luxuryRange, ClosedInterval<Integer> capacityRange, double price, String description) {
        super(id);
        this.yield = yield;
        this.gateA = gateA;
        this.gateB = gateB;
        this.luxuryRange = new ClosedInterval<>(luxuryRange);
        this.capacityRange = new ClosedInterval<>(capacityRange);
        this.price = price;
        this.description = description;
    }
}
