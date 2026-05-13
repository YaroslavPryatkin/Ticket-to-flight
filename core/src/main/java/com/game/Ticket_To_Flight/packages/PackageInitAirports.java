package com.game.Ticket_To_Flight.packages;

import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;
import java.util.ArrayList;
import java.util.List;

public class PackageInitAirports {

    private List<Airport> airports;

    public PackageInitAirports() {
        this.airports = new ArrayList<>();
    }

    public PackageInitAirports(List<Airport> airports) {
        this.airports = airports;
    }

    public List<Airport> getAirports() {
        return airports;
    }
}
