package com.game.Ticket_To_Flight.backend.gameLogicEntities.templates;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonCreator
    public AirlineType(
        @JsonProperty("id") int id,
        @JsonProperty("yield") double yield,
        @JsonProperty("gateA") int gateA,
        @JsonProperty("gateB") int gateB,
        @JsonProperty("minLuxury") Double minLuxury,
        @JsonProperty("maxLuxury") Double maxLuxury,
        @JsonProperty("minCapacity") Integer minCapacity,
        @JsonProperty("maxCapacity") Integer maxCapacity,
        @JsonProperty("price") double price,
        @JsonProperty("description") String description
    ){   super(id);
        this.yield = yield;
        this.gateA = gateA;
        this.gateB = gateB;
        this.luxuryRange = new ClosedInterval<>(minLuxury, maxLuxury);
        this.capacityRange = new ClosedInterval<>(minCapacity, maxCapacity);
        this.price = price;
        this.description = description;
    }

    public double getPrice() {
        return price;
    }
}
