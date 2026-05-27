package com.game.Ticket_To_Flight.commonFrontAndBack.DTO;

import com.badlogic.gdx.graphics.Color;
import com.game.Ticket_To_Flight.Utilities.Identifiable;
import com.game.Ticket_To_Flight.Utilities.MapHolder;
import com.game.Ticket_To_Flight.Utilities.SetHolder;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airline;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Player;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PlaneType;
import com.game.Ticket_To_Flight.commonFrontAndBack.StaticGameData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class PlayerDTO extends Identifiable {
    private PlayerDTO() {
        super(0);
        name = null;
        money = 0;
        income = 0;
        amountOfShares = 0;
        actionPoints = 0;
        planes = null;
        airlines = null;
        ability = null;
        color = null;
        hasPassed = false;
        auctionBet = 0;
    }

    private static final AtomicInteger idGenerator = new AtomicInteger(0);

    private final String name;
    private final int money;
    private final int income;
    private final int amountOfShares;
    private final int actionPoints;
    private final boolean hasPassed;
    private final int auctionBet;
    private final Map<Integer, Integer> planes;
    private final Set<Integer> airlines;
    private final Integer ability;
    private final Color color;

    public PlayerDTO(Player player) {
        super(player.getId());
        this.name = player.name;
        this.money = player.money;
        this.income = player.income;
        this.amountOfShares = player.amountOfShares;
        this.actionPoints = player.actionPoints;
        this.airlines = new HashSet<>();
        for (Airline line : player.airlines) {
            this.airlines.add(line.getId());
        }
        this.planes = new HashMap<>();
        this.planes.putAll(player.planes);
        if (player.ability != null)
            this.ability = player.ability.getId();
        else
            this.ability = null;
        this.color = player.color;
        this.hasPassed = player.hasPassed;
        this.auctionBet = player.auctionBet;
    }

    /**
     * Should not be called anywhere except Low Level Handler
     * Creates player in default state
     */
    public PlayerDTO(String name, Color color) {
        super(idGenerator.incrementAndGet());
        this.name = name;
        money = 0;
        income = 0;
        amountOfShares = 0;
        actionPoints = 0;
        planes = new HashMap<>();
        airlines = new HashSet<>();
        ability = null;
        this.color = color;
        this.hasPassed = false;
        this.auctionBet = 0;
    }

    public Player restore(SetHolder<Airline> lookUpAirlines) {
        SetHolder<Airline> lines = new SetHolder<>();
        for (Integer id : this.airlines) {
            Airline line = lookUpAirlines.get(id);
            if (line == null) return null;
            lines.add(line);
        }
        MapHolder<PlaneType, Integer> planes = new MapHolder<>(StaticGameData.planeTypes);
        try {
            planes.putAll(this.planes);
        } catch (Exception e) {
            return null;
        }
        return new Player(
            this.getId(), this.money, this.income, this.amountOfShares,
            this.actionPoints, planes, lines, this.name, StaticGameData.abilityTypes.get(this.ability),
            this.color, this.hasPassed, this.auctionBet);
    }
}
