package com.game.Ticket_To_Flight.frontend.UI.MainScreen.Managers;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airline;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.frontend.LowLevelHandlerFront;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManager;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.Flights.MainFlightController;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.Flights.PassengerSelectionListener;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.Tooltips.AirlineTooltipWindow;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.Tooltips.AirportTooltipWindow;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.Tooltips.MapTooltipWindow;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.MapInput.MapSelectionState;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class TooltipManager {
    private final Stage uiStageHUD;
    private final Skin skin;
    private final GameUIManager facade;
    private final GameData gameData;
    private final LowLevelHandlerFront llh;
    private final MapSelectionState selectionState;
    private final MainFlightController flightController;

    private MapTooltipWindow currentTooltip;

    public TooltipManager(Stage uiStageHUD, Skin skin, GameUIManager facade, GameData gameData, LowLevelHandlerFront llh, MapSelectionState selectionState, OrthographicCamera mapCamera, MainFlightController flightController) {
        this.uiStageHUD = uiStageHUD;
        this.skin = skin;
        this.facade = facade;
        this.gameData = gameData;
        this.llh = llh;
        this.selectionState = selectionState;
        this.flightController = flightController;
    }

    public void showAirportTooltip(Airport airport) {
        showAirportTooltip(airport, null);
    }

    public void showAirportTooltipForFlight(Airport airport, PassengerSelectionListener passengerSelectionListener) {
        showAirportTooltip(airport, passengerSelectionListener);
    }

    private void showAirportTooltip(Airport airport, PassengerSelectionListener passengerSelectionListener) {
        removeTooltip();
        boolean canSelectGroup =
            gameData.currentState == GameData.State.FLIGHTS &&
                gameData.currentPlayer == llh.getMyId() &&
                flightController.canSelectPassengerGroups(airport);

        currentTooltip = new AirportTooltipWindow(skin, airport, selectionState, canSelectGroup, passengerSelectionListener, flightController);
        uiStageHUD.addActor(currentTooltip.asWindow());
        updateTooltipPosition();
    }

    public void showAirlineTooltip(Airline airline) {
        removeTooltip();

        double currentPlayerMoney = gameData.players.get(llh.getMyId()).getMoney();
        boolean canBuyDuringCurrentStage = gameData.currentState == GameData.State.AIRLINES;

        currentTooltip = new AirlineTooltipWindow(skin, facade, airline, currentPlayerMoney, canBuyDuringCurrentStage, llh);
        uiStageHUD.addActor(currentTooltip.asWindow());
        updateTooltipPosition();
    }

    public void removeTooltip() {
        if (currentTooltip != null) {
            currentTooltip.asWindow().remove();
            currentTooltip = null;
        }
    }

    public void updateTooltipPosition() {
        if (currentTooltip == null) return;

        var window = currentTooltip.asWindow();

        window.pack();

        float paddingLeft = 20f;
        float paddingBottom = 20f;
        float paddingTop = 20f;
        float maxHeight = uiStageHUD.getHeight() - paddingBottom - paddingTop;

        if (window.getHeight() > maxHeight) {
            window.setHeight(maxHeight);
        }

        window.setPosition(paddingLeft, paddingBottom);
    }
}
