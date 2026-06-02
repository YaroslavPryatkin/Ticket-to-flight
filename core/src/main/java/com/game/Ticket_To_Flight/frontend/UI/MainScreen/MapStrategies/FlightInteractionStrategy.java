package com.game.Ticket_To_Flight.frontend.UI.MainScreen.MapStrategies;

import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airline;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.frontend.LowLevelHandlerFront;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManager;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.MapInput.MapSelectionState;

public class FlightInteractionStrategy implements MapInteractionStrategy {
    private final GameData gameData;
    private final GameUIManager uiManager;
    private final LowLevelHandlerFront llh;
    private final MapSelectionState selectionState;

    public FlightInteractionStrategy(GameData gameData, GameUIManager uiManager, LowLevelHandlerFront llh, MapSelectionState selectionState) {
        this.gameData = gameData;
        this.uiManager = uiManager;
        this.llh = llh;
        this.selectionState = selectionState;
    }

    @Override
    public void onAirportClicked(Airport airport) {
        if (!canChooseFlight()) return;
        uiManager.handleFlightAirportClick(airport);
    }

    @Override
    public void onAirlineClicked(Airline airline) {
        if (!canChooseFlight()) return;
        uiManager.handleFlightAirlineClick(airline);
    }

    @Override
    public void onEmptyMapClicked(float worldX, float worldY) {
        selectionState.clearFlightSelection();
        uiManager.removeTooltip();
    }

    private boolean canChooseFlight() {
        return gameData.currentState == GameData.State.FLIGHTS && gameData.currentPlayer == llh.getMyId();
    }
}
