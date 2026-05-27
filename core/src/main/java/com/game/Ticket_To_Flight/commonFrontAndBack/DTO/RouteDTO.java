package com.game.Ticket_To_Flight.commonFrontAndBack.DTO;

import com.game.Ticket_To_Flight.Utilities.Identifiable;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airline;
import com.game.Ticket_To_Flight.commonFrontAndBack.Route;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class RouteDTO extends Identifiable {
    private static final AtomicInteger idGenerator = new AtomicInteger(0);

    private final Integer plane;
    private final Integer startingPort;
    private final List<Integer> lines;
    private final Map<Integer, Map<Integer,Integer>> passengers;

    public RouteDTO(Route route){
        super(idGenerator.incrementAndGet());
        plane = route.plane.getId();
        startingPort = route.startingPort.getId();
        lines = new ArrayList<>();
        for(Airline line : route.getLines())
            lines.add(line.getId());
        passengers = route.passengersForDTO();
    }
}
