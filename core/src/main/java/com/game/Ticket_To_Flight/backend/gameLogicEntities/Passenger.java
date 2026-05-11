package com.game.Ticket_To_Flight.backend.gameLogicEntities;

import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.CityType;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PassengerType;

import java.util.concurrent.atomic.AtomicInteger;

public class Passenger {
    private static final AtomicInteger idGenerator = new AtomicInteger(0);

    public final int id;
    public final PassengerType type;
    public final Airport portFrom;
    public final Airport portTo;
    public final CityType typeTo;

    //for server
    public Passenger(PassengerType type, Airport portFrom, Airport portTo, CityType typeTo) {
        if((portTo == null  && typeTo == null) || (portTo != null && typeTo != null))
            throw new IllegalArgumentException("There should be exactly one non-null value among typeTo and portTo.");
        this.id = idGenerator.incrementAndGet();
        this.type = type;
        this.portFrom = portFrom;
        this.portTo = portTo;
        this.typeTo = typeTo;
    }

    //for client
    public Passenger(int id, PassengerType type, Airport portFrom, Airport portTo, CityType typeTo) {
        if((portTo == null  && typeTo == null) || (portTo != null && typeTo != null))
            throw new IllegalArgumentException("There should be exactly one non-null value among typeTo and portTo.");
        this.id = id;
        this.type = type;
        this.portFrom = portFrom;
        this.portTo = portTo;
        this.typeTo = typeTo;
    }

    public boolean doesAirportFit(Airport port){
        if(portTo != null ) return port == portTo;
        else return port.type.cityType == typeTo;
    }

    @Override
    public int hashCode(){
        return id;
    }
}
