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
    public final int price;
    public final String description;

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
        @JsonProperty("price") int price,
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
