package com.game.Ticket_To_Flight.frontend.components.tables;

import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PassengerType;

public class PassengerTableWidget {
    public final PassengerType passengerType;

    public PassengerTableWidget(PassengerType passengerType) {
        this.passengerType = passengerType;
    }

    public String cityTo() {
        return passengerType.typeTo.description;
    }

    public String persons() {
        return String.valueOf(passengerType.size);
    }

    public String reward() {
        return "$" + passengerType.solvency;
    }

    public String passengerClass() {
        return passengerType.description;
    }
}
