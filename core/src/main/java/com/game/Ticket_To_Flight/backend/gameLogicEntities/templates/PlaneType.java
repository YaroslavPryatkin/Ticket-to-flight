package com.game.Ticket_To_Flight.backend.gameLogicEntities.templates;

import com.game.Ticket_To_Flight.Utilities.ClosedRange;
import com.game.Ticket_To_Flight.Utilities.Identifiable;

public class PlaneType extends Identifiable {
    public final double fuel;
    public final int stations;
    public final double luxury;
    public final int capacity;
    public final ClosedRange<Integer> gateRange;
    public final ClosedRange<Double> distRange;
    public final double price;
    public final String description;


    public PlaneType(int id, double fuel, int stations, double luxury, int capacity,
                     ClosedRange<Integer> gateRange, ClosedRange<Double> distRange, double price, String description) {
        super(id);
        this.fuel = fuel;
        this.stations=stations;
        this.luxury=luxury;
        this.capacity=capacity;
        this.gateRange = new ClosedRange<>(gateRange);
        this.distRange = new ClosedRange<>(distRange);
        this.price=price;
        this.description=description;
    }
}
