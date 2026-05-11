package com.game.Ticket_To_Flight.backend.gameLogicEntities;

import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PlaneType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Player {
    private static final AtomicInteger idGenerator = new AtomicInteger(0);

    public final int id;
    public double money=0;
    public double income=0;
    public List<PlaneType> planes = new ArrayList<>();
    public List<Airline> airlines = new ArrayList<>();

    //for server
    public Player(int id) {
        this.id = idGenerator.incrementAndGet();
    }

    //for client
    public Player(int id, double money, double income, List<PlaneType> planes, List<Airline> airlines){
        this.id = id;
        this.money = money;
        this.income=income;
        this.planes = planes;
        this.airlines = airlines;
    }

    @Override
    public int hashCode(){
        return id;
    }
}
