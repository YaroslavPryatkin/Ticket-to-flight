package com.game.Ticket_To_Flight.backend.gameLogicEntities;

import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.AirlineType;

import java.util.concurrent.atomic.AtomicInteger;

public class Airline {
    private static final AtomicInteger idGenerator = new AtomicInteger(0);

    public final int id;
    public final AirlineType type;
    public final Airport portA;
    public final Airport portB;
    public Player player = null;

    //for server
    public Airline(AirlineType type, Airport portA, Airport portB) {
        this.id = idGenerator.incrementAndGet();
        this.type = type;
        this.portA = portA;
        this.portB = portB;
    }

    //for client
    public Airline(int id, AirlineType type, Airport portA, Airport portB, Player player) {
        this.id = id;
        this.type = type;
        this.portA = portA;
        this.portB = portB;
        this.player = player;
    }

    public double getDistance(){
        return portA.position.dst2(portB.position);
    }

    //returns null if nothing fit
    public Airport getAnotherEnd(Airport end){
        if(end == portA) return portB;
        else if (end == portB) return portA;
        return null;
    }
}
