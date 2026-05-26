package com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.MapStrategies;

import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airline;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.frontend.LowLevelHandlerFront;
import com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.GameUIManager;
import com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.MapSelectionState;

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
        if (canChooseFlight()) {
            selectionState.selectAirport(airport);
        }
        uiManager.showAirportTooltip(airport);
    }

    @Override
    public void onAirlineClicked(Airline airline) {
        if (!canChooseFlight()) {
            uiManager.showAirlineTooltip(airline);
            return;
        }

        Integer selectedAirportId = selectionState.getSelectedAirportId();
        if (selectedAirportId == null) {
            uiManager.showAirlineTooltip(airline);
            return;
        }

        boolean connectedToSelectedAirport =
            airline.getPortA().getId() == selectedAirportId || airline.getPortB().getId() == selectedAirportId;
        if (connectedToSelectedAirport) {
            selectionState.selectAirline(airline);
            uiManager.removeTooltip();
        } else {
            uiManager.showAirlineTooltip(airline);
        }
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
