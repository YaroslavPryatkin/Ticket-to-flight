package com.game.Ticket_To_Flight.backend.gameLogicEntities.templates;

import com.game.Ticket_To_Flight.Utilities.ClosedInterval;
import com.game.Ticket_To_Flight.Utilities.Identifiable;

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

    @Override
    public int hashCode(){
        return id;
    }

    public int getSize() {
        return size;
    }
}
