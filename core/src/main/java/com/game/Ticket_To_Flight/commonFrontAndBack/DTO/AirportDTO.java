package com.game.Ticket_To_Flight.commonFrontAndBack.DTO;

import com.badlogic.gdx.math.Vector2;
import com.game.Ticket_To_Flight.Utilities.Identifiable;
import com.game.Ticket_To_Flight.Utilities.MapHolder;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.AirportType;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PassengerType;
import com.game.Ticket_To_Flight.commonFrontAndBack.StaticGameData;

import java.util.HashMap;
import java.util.Map;

public class AirportDTO extends Identifiable {
    private AirportDTO() {
        super(0);
        type = null;
        position = null;
        passengers = null;
        name = null;
        takenGates=0;
    }

    private final Integer type;
    private final Vector2 position;
    private final Map<Integer, Integer> passengers;
    private final String name;
    private final int takenGates;

    public AirportDTO(Airport port) {
        super(port.getId());
        this.type = port.type.getId();
        this.position = port.position;
        this.passengers = new HashMap<>();
        this.passengers.putAll(port.passengers);
        this.name = port.airportName;
        this.takenGates = port.getTakenGates();
    }

    /**
     * Should not be called anywhere except Low Level Handler
     * UNSAFE
     */
    public AirportDTO(int id, Integer type, Vector2 position, String AirportName) {
        super(id);
        if (type == null || position == null) throw new IllegalArgumentException("Null arguments in constructor.");
        this.type = type;
        this.position = position;
        this.name = AirportName;
        this.passengers = new HashMap<>();
        this.takenGates = 0;
    }

    public Airport restore() {
        AirportType type = StaticGameData.airportTypes.get(this.type);
        if (type == null) return null;
        MapHolder<PassengerType, Integer> passengers = new MapHolder<>(StaticGameData.passengerTypes);
        try {
            passengers.putAll(this.passengers);
        } catch (Exception e) {
            return null;
        }
        return new Airport(this.getId(), type, this.position, this.name, passengers, this.takenGates);
    }
}
