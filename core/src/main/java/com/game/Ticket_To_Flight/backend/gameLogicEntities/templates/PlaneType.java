package com.game.Ticket_To_Flight.backend.gameLogicEntities.templates;

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


    public PlaneType(int id, double fuel, int stations, double luxury, int capacity,
                     ClosedInterval<Integer> gateRange, ClosedInterval<Double> distRange, double price, String description) {
        super(id);
        this.fuel = fuel;
        this.stations=stations;
        this.luxury=luxury;
        this.capacity=capacity;
        this.gateRange = new ClosedInterval<>(gateRange);
        this.distRange = new ClosedInterval<>(distRange);
        this.price=price;
        this.description=description;
    }
}
