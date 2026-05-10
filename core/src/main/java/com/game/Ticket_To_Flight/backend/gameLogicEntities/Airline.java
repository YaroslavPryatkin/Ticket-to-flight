package com.game.Ticket_To_Flight.backend.gameLogicEntities;

import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.AirlineType;

public class Airline {
    public final AirlineType type;
    public final int portA;
    public final int portB;

    public Airline(AirlineType type, int portA, int portB) {
        this.type = type;
        this.portA = portA;
        this.portB = portB;
    }
}
