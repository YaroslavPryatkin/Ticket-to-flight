package com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.MapStrategies;

import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airline;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;

public interface MapInteractionStrategy {
    void onAirportClicked(Airport airport);
    void onAirlineClicked(Airline airline);
    void onEmptyMapClicked(float x, float y);
}
