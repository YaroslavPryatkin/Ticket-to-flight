package com.game.Ticket_To_Flight.commonFrontAndBack;

import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airline;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.AirlineType;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.AirportType;

import java.util.HashMap;
import java.util.Map;

public class GameMapData {
    private static Map<Integer, AirportType> airportTypes = new HashMap<>();
    private static Map<Integer, AirlineType> airlineTypes = new HashMap<>();
    private Map<Integer, Airport> airports = new HashMap<>();
    private Map<Integer, Airline> airlines = new HashMap<>();
}
