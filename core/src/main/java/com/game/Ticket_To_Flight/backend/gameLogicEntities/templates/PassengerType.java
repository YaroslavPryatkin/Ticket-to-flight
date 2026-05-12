package com.game.Ticket_To_Flight.backend.gameLogicEntities.templates;

import com.game.Ticket_To_Flight.Utilities.ClosedRange;
import com.game.Ticket_To_Flight.Utilities.Identifiable;

public class PassengerType extends Identifiable {
    public final double solvency;
    public final int size;
    public final ClosedRange<Double> luxuryRange;
    public final ClosedRange<Double> yieldRange;
    public final ClosedRange<Integer> capacityRange;
    public final ClosedRange<Integer> stationsRange;
    public final String description;

    public PassengerType(int id, double solvency, int size, ClosedRange<Double> luxuryRange, ClosedRange<Double> yieldRange, ClosedRange<Integer> capacityRange, ClosedRange<Integer> stationsRange, String description) {
        super(id);
        this.solvency = solvency;
        this.size = size;
        this.luxuryRange = new ClosedRange<>(luxuryRange);
        this.yieldRange = new ClosedRange<>(yieldRange);
        this.capacityRange = new ClosedRange<>(capacityRange);
        this.stationsRange = new ClosedRange<>(stationsRange);
        this.description = description;
    }

    @Override
    public int hashCode(){
        return id;
    }
}
