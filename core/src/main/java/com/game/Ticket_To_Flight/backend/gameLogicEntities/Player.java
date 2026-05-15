package com.game.Ticket_To_Flight.backend.gameLogicEntities;

import com.game.Ticket_To_Flight.Utilities.SetHolder;
import com.game.Ticket_To_Flight.Utilities.MapHolder;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PlaneType;
import com.game.Ticket_To_Flight.Utilities.Identifiable;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;

import java.util.concurrent.atomic.AtomicInteger;

public class Player extends Identifiable {
    public String name;
    public double money;
    public double income;
    public MapHolder<PlaneType, Integer> planes;
    public SetHolder<Airline> airlines;

    /**
     * Should not be called anywhere except game data
     */
    public Player(int id, String name, double money, double income, MapHolder<PlaneType, Integer> planes, SetHolder<Airline> airlines){
        super(id);
        this.name = name;
        this.money = money;
        this.income=income;
        this.planes = planes;
        this.airlines = airlines;
    }

    public Double getIncome() {return income;}
    public void setIncome(Double val){income = val;}
    public Double getMoney() {return money;}
    public void setMoney(Double val) {money = val;}

}
