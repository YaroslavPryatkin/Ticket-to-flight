package com.game.Ticket_To_Flight.backend.gameLogicEntities.templates;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.game.Ticket_To_Flight.Utilities.ClosedInterval;
import com.game.Ticket_To_Flight.Utilities.Identifiable;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;

import java.util.concurrent.CyclicBarrier;

public class PassengerType extends Identifiable {
    public final double solvency;
    public final int size;
    public final ClosedInterval<Double> luxuryRange;
    public final ClosedInterval<Double> yieldRange;
    public final ClosedInterval<Integer> capacityRange;
    public final ClosedInterval<Integer> stationsRange;
    public final String description;
    public final CityType typeTo;

    public PassengerType(
        int id, double solvency, int size,
        ClosedInterval<Double> luxuryRange, ClosedInterval<Double> yieldRange,
        ClosedInterval<Integer> capacityRange, ClosedInterval<Integer> stationsRange,
        CityType typeTo,  String description) {
        super(id);
        this.solvency = solvency;
        this.size = size;
        this.luxuryRange = luxuryRange;
        this.yieldRange = yieldRange;
        this.capacityRange = capacityRange;
        this.stationsRange = stationsRange;
        this.typeTo = typeTo;
        this.description = description;
    }

    @JsonCreator
    public PassengerType(
        @JsonProperty("id") int id,
        @JsonProperty("solvency") double solvency,
        @JsonProperty("size") int size,
        @JsonProperty("minLuxury") Double minLuxury,
        @JsonProperty("maxLuxury") Double maxLuxury,
        @JsonProperty("minYield") Double minYield,
        @JsonProperty("maxYield") Double maxYield,
        @JsonProperty("minCapacity") Integer minCapacity,
        @JsonProperty("maxCapacity") Integer maxCapacity,
        @JsonProperty("minStations") Integer minStations,
        @JsonProperty("maxStations") Integer maxStations,
        @JsonProperty("typeTo") Integer typeTo,
        @JsonProperty("description") String description
    ) {
        super(id);
        this.solvency = solvency;
        this.size = size;
        this.luxuryRange = new ClosedInterval<>(minLuxury, maxLuxury);
        this.yieldRange = new ClosedInterval<>(minYield, maxYield);
        this.capacityRange = new ClosedInterval<>(minCapacity, maxCapacity);
        this.stationsRange = new ClosedInterval<>(minStations, maxStations);
        this.typeTo = GameData.cityTypes.get(typeTo);
        this.description = description;
    }

    @Override
    public int hashCode(){
        return id;
    }

    public int getSize() {
        return size;
    }
}
