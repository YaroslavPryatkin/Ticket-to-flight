package com.game.Ticket_To_Flight.backend.gameLogicEntities;

import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.AirlineType;
import com.game.Ticket_To_Flight.Utilities.Identifiable;

import java.util.concurrent.atomic.AtomicInteger;

public class Airline extends Identifiable {
    public final AirlineType type;
    public final Airport portA;
    public final Airport portB;
    public Player player = null;

    /**
     * Should not be called anywhere except game data
     */
    public Airline(int id, AirlineType type, Airport portA, Airport portB, Player player) {
        super(id);
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

    public Player getPlayer() {
        return player;
    }

    public double getPrice() {
        return type.getPrice();
    }

    public String getName() {
        return player.getName();
    }

    public Airport getPortA() {
        return this.portA;
    }

    public Airport getPortB() {
        return this.portB;
    }

    @Override
    public String toString(){
        return "Airline of type [" + type.description + "] connecting airports A: " + portA.getId() + " and B: " + portB.getId();
    }
}
