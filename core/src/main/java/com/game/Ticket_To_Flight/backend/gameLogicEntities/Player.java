package com.game.Ticket_To_Flight.backend.gameLogicEntities;

import com.badlogic.gdx.graphics.Color;
import com.game.Ticket_To_Flight.Utilities.SetHolder;
import com.game.Ticket_To_Flight.Utilities.MapHolder;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PlaneType;
import com.game.Ticket_To_Flight.Utilities.Identifiable;
import java.util.concurrent.atomic.AtomicInteger;

public class Player extends Identifiable {
    private static final AtomicInteger idGenerator = new AtomicInteger(0);

    public double money = 0;
    public double income = 0;
    public MapHolder<PlaneType, Integer> planes = new MapHolder<>();
    public SetHolder<Airline> airlines = new SetHolder<>();
    public String name;
    public Color color = Color.WHITE;

    //for server
    public Player() {
        super( idGenerator.incrementAndGet());
    }
    //for server
    public Player(int id) {super(id);}

    //for client

    public Player(int id, double money, double income, MapHolder<PlaneType, Integer> planes, SetHolder<Airline> airlines){
        super(id);
        this.money = money;
        this.income=income;
        this.planes = planes;
        this.airlines = airlines;
    }

    public Player(int id, double money, double income, MapHolder<PlaneType, Integer> planes, SetHolder<Airline> airlines, String name, Color color){
        super(id);
        this.money = money;
        this.income=income;
        this.planes = planes;
        this.airlines = airlines;
        this.name = name;
        this.color = Color.WHITE;
    }

    public Double getIncome() {return income;}
    public void setIncome(Double val){income = val;}
    public Double getMoney() {return money;}
    public void setMoney(Double val) {money = val;}

    public String getName() {
        return name;
    }

    public Color getColor() {
        return this.color;
    }
}
