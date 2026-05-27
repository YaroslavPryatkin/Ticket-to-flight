package com.game.Ticket_To_Flight.frontend.UI.MainScreen.MapStrategies;

import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airline;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManager;

public class DefaultInteractionStrategy implements MapInteractionStrategy {
    private final GameUIManager uiManager;

    public DefaultInteractionStrategy(GameUIManager uiManager) {
        this.uiManager = uiManager;
    }

    @Override
    public void onAirportClicked(Airport airport) {
        uiManager.showAirportTooltip(airport);
    }

    @Override
    public void onAirlineClicked(Airline airline) {
        uiManager.showAirlineTooltip(airline);
    }

    @Override
    public void onEmptyMapClicked(float worldX, float worldY) {
        uiManager.removeTooltip();
    }
}
