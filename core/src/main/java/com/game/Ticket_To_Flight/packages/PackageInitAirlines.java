package com.game.Ticket_To_Flight.packages;

import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airline;
import java.util.ArrayList;
import java.util.List;

public class PackageInitAirlines {
    private List<Airline> airlines;

    public PackageInitAirlines() {
        this.airlines = new ArrayList<>();
    }

    public PackageInitAirlines(List<Airline> airlines) {
        this.airlines = airlines;
    }

    public List<Airline> getAirlines() {
        return airlines;
    }
}
