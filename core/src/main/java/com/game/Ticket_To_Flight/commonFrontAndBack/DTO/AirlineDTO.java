package com.game.Ticket_To_Flight.commonFrontAndBack.DTO;

import com.game.Ticket_To_Flight.Utilities.Identifiable;
import com.game.Ticket_To_Flight.Utilities.SetHolder;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airline;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Player;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.AirlineType;
import com.game.Ticket_To_Flight.commonFrontAndBack.StaticGameData;

import java.util.concurrent.atomic.AtomicInteger;

public class AirlineDTO extends Identifiable {
    private AirlineDTO() {
        super(0);
        type = 0;
        portA = 0;
        portB = 0;
        player = null;
    }

    private static final AtomicInteger idGenerator = new AtomicInteger(0);
    private final int type;
    private final int portA;
    private final int portB;
    private final Integer player;

    public AirlineDTO(Airline line) {
        super(line.getId());
        this.type = line.type.getId();
        this.portA = line.portA.getId();
        this.portB = line.portB.getId();
        if (line.player != null) {
            this.player = line.player.getId();
        } else {
            this.player = null;
        }
    }

    /**
     * Should not be called anywhere except Low Level Handler
     */
    public AirlineDTO(AirlineType type, Airport portA, Airport portB) {
        super(idGenerator.incrementAndGet());
        this.type = type.getId();
        this.portA = portA.getId();
        this.portB = portB.getId();
        this.player = null;
    }

    /**
     * Should not be called anywhere except Low Level Handler
     * UNSAFE
     */
    public AirlineDTO(Integer type, Integer portA, Integer portB) {
        super(idGenerator.incrementAndGet());
        this.type = type;
        this.portA = portA;
        this.portB = portB;
        this.player = null;
    }

    public Airline restore(SetHolder<Airport> lookUpAirports, SetHolder<Player> lookUpPlayers) {
        AirlineType type = StaticGameData.airlineTypes.get(this.type);
        Airport portA = lookUpAirports.get(this.portA);
        Airport portB = lookUpAirports.get(this.portB);
        if (type == null || portA == null || portB == null) return null;

        if (this.player == null)
            return new Airline(this.getId(), type, portA, portB, null);

        Player player = lookUpPlayers.get(this.player);
        if (player == null) return null;

        return new Airline(this.getId(), type, portA, portB, player);
    }
}
