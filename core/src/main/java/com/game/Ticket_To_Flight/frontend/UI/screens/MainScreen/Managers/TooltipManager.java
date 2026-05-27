package com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.Managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airline;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.frontend.LowLevelHandlerFront;
import com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.GameUIManager;
import com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.MapSelectionState;
import com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.GameUIManagerDirectory.Tooltips.AirlineTooltipWindow;
import com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.GameUIManagerDirectory.Tooltips.AirportTooltipWindow;

public class TooltipManager {
    private final Stage uiStageHUD;
    private final Skin skin;
    private final GameUIManager facade;
    private final GameData gameData;
    private final LowLevelHandlerFront llh;
    private final MapSelectionState selectionState;
    private final OrthographicCamera mapCamera;

    private Window currentTooltip;
    private Vector2 anchoredWorldPosition;

    public TooltipManager(Stage uiStageHUD, Skin skin, GameUIManager facade, GameData gameData, LowLevelHandlerFront llh, MapSelectionState selectionState, OrthographicCamera mapCamera) {
        this.uiStageHUD = uiStageHUD;
        this.skin = skin;
        this.facade = facade;
        this.gameData = gameData;
        this.llh = llh;
        this.selectionState = selectionState;
        this.mapCamera = mapCamera;
    }

    public void showAirportTooltip(Airport airport) {
        removeTooltip();
        boolean canSelectGroup = gameData.currentState == GameData.State.FLIGHTS && gameData.currentPlayer == llh.getMyId();
        currentTooltip = new AirportTooltipWindow(skin, airport, selectionState, canSelectGroup);
        anchoredWorldPosition = airportPosition(airport);
        uiStageHUD.addActor(currentTooltip);
        updateTooltipPosition();
    }

    public void showAirlineTooltip(Airline airline) {
        removeTooltip();

        double currentPlayerMoney = gameData.players.get(llh.getMyId()).getMoney();
        boolean canBuyDuringCurrentStage = gameData.currentState == GameData.State.AIRLINES;

        currentTooltip = new AirlineTooltipWindow(skin, facade, airline, currentPlayerMoney, canBuyDuringCurrentStage, llh);
        anchoredWorldPosition = airlineMidpoint(airline);
        uiStageHUD.addActor(currentTooltip);
        updateTooltipPosition();
    }

    public void removeTooltip() {
        if (currentTooltip != null) {
            currentTooltip.remove();
            currentTooltip = null;
        }
        anchoredWorldPosition = null;
    }

    public void updateTooltipPosition() {
        if (currentTooltip == null) return;

        Vector2 stageCoords;
        if (anchoredWorldPosition == null) {
            stageCoords = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        } else {
            Vector3 screenCoords = mapCamera.project(new Vector3(anchoredWorldPosition.x, anchoredWorldPosition.y, 0));
            stageCoords = new Vector2(screenCoords.x, Gdx.graphics.getHeight() - screenCoords.y);
        }
        uiStageHUD.screenToStageCoordinates(stageCoords);
        currentTooltip.setPosition(stageCoords.x + 15, stageCoords.y + 15);
    }

    private Vector2 airportPosition(Airport airport) {
        return new Vector2(airport.getX(), airport.getY());
    }

    private Vector2 airlineMidpoint(Airline airline) {
        return new Vector2(
            (airline.getPortA().getX() + airline.getPortB().getX()) / 2f,
            (airline.getPortA().getY() + airline.getPortB().getY()) / 2f
        );
    }
}
