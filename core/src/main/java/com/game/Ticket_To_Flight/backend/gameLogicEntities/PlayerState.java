package com.game.Ticket_To_Flight.backend.gameLogicEntities;

import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PlaneType;

import java.util.ArrayList;
import java.util.List;

public class PlayerState {
    public int money=0;
    public int income=0;
    public List<PlaneType> planes = new ArrayList<>();
    public List<Airline> airlines = new ArrayList<>();

}
