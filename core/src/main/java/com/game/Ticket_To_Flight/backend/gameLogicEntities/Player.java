package com.game.Ticket_To_Flight.backend.gameLogicEntities;

import com.badlogic.gdx.graphics.Color;
import com.game.Ticket_To_Flight.Utilities.SetHolder;
import com.game.Ticket_To_Flight.Utilities.MapHolder;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.AbilityType;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PlaneType;
import com.game.Ticket_To_Flight.Utilities.Identifiable;
import java.util.concurrent.atomic.AtomicInteger;

public class Player extends Identifiable {
    private static final AtomicInteger idGenerator = new AtomicInteger(0);

    public double money = 0;
    public double income = 0;
    public int amountOfShares = 0;
    public int actionPoints = 0;
    public boolean hasPassed = false;
    public Integer auctionBet = null;
    public MapHolder<PlaneType, Integer> planes = new MapHolder<>();
    public SetHolder<Airline> airlines = new SetHolder<>();
    public String name;
    public AbilityType ability = null;
    public Color color;

    //for client
    public Player(
        int id, double money, double income, int amountOfShares, int actionPoints,
        MapHolder<PlaneType, Integer> planes, SetHolder<Airline> airlines,
        String name, AbilityType ability,  Color color,  boolean hasPassed, Integer auctionBet){
        super(id);
        this.money = money;
        this.income=income;
        this.planes = planes;
        this.airlines = airlines;
        this.amountOfShares = amountOfShares;
        this.actionPoints = actionPoints;
        this.name = name;
        this.ability = ability;
        this.color = color;
        this.hasPassed = hasPassed;
        this.auctionBet = auctionBet;
    }

    public Double getIncome() {return income;}
    public void setIncome(Double val){income = val;}
    public Double getMoney() {return money;}
    public void setMoney(Double val) {money = val;}
    public Integer getAmountOfShares() {return amountOfShares;}
    public void setAmountOfShares(Integer val) {amountOfShares = val;}
    public Integer getActionPoints() {return actionPoints;}
    public void setActionPoints(Integer val) {actionPoints = val;}
    public AbilityType getAbility() {return ability;}
    public void setAbility(AbilityType val) {ability = val;}
    public Boolean getHasPassed() {return hasPassed;}
    public void setHasPassed(Boolean val) {hasPassed = val;}
    public Integer getAuctionBet() {return auctionBet;}
    public void setAuctionBet(Integer val) {auctionBet = val;}

    public String getName() {
        return name;
    }

    public Color getColor() {
        return this.color;
    }

    @Override
    public String toString(){
        return "Player " + name;
    }

}
