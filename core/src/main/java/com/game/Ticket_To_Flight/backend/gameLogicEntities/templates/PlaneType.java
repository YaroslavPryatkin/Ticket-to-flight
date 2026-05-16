package com.game.Ticket_To_Flight.backend.gameLogicEntities.templates;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.game.Ticket_To_Flight.Utilities.ClosedInterval;
import com.game.Ticket_To_Flight.Utilities.Identifiable;

public class PlaneType extends Identifiable {
    public final double fuel;
    public final int stations;
    public final double luxury;
    public final int capacity;
    public final ClosedInterval<Integer> gateRange;
    public final ClosedInterval<Double> distRange;
    public final double price;
    public final String description;


    @JsonCreator
    public PlaneType(
        @JsonProperty("id") int id,
        @JsonProperty("fuel") double fuel,
        @JsonProperty("stations") int stations,
        @JsonProperty("luxury") double luxury,
        @JsonProperty("capacity") int capacity,
        @JsonProperty("minGate") Integer minGate,
        @JsonProperty("maxGate") Integer maxGate,
        @JsonProperty("minDist") Double minDist,
        @JsonProperty("maxDist") Double maxDist,
        @JsonProperty("price") double price,
        @JsonProperty("description") String description
    ) {
        super(id);
        this.fuel = fuel;
        this.stations = stations;
        this.luxury = luxury;
        this.capacity = capacity;
        this.gateRange = new ClosedInterval<>(minGate, maxGate);
        this.distRange = new ClosedInterval<>(minDist, maxDist);
        this.price = price;
        this.description = description;
    }
}
