package com.game.Ticket_To_Flight.frontend.UI.MainScreen.Managers;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airline;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.frontend.LowLevelHandlerFront;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManager;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.Flights.MainFlightController;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.Flights.PassengerSelectionListener;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.Tooltips.AirlineClickTooltip;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.Tooltips.AirlineHoverTooltip;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.Tooltips.AirportTooltipWindow;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.Tooltips.AirportHoverTooltip;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.Tooltips.MapTooltipWindow;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.MapInput.MapSelectionState;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.game.Ticket_To_Flight.frontend.components.subsidiary.ComponentHover;

public class TooltipManager {
    private final Stage uiStageHUD;
    private final Skin skin;
    private final GameUIManager facade;
    private final GameData gameData;
    private final LowLevelHandlerFront llh;
    private final MapSelectionState selectionState;
    private final MainFlightController flightController;
    private final OrthographicCamera mapCamera;

    private MapTooltipWindow currentTooltip;
    private Airline currentClickAirline;
    private Window hoverTooltip;
    private Airport hoverAirport;
    private Airline hoverAirline;

    public TooltipManager(Stage uiStageHUD, Skin skin, GameUIManager facade, GameData gameData, LowLevelHandlerFront llh, MapSelectionState selectionState, OrthographicCamera mapCamera, MainFlightController flightController) {
        this.uiStageHUD = uiStageHUD;
        this.skin = skin;
        this.facade = facade;
        this.gameData = gameData;
        this.llh = llh;
        this.selectionState = selectionState;
        this.flightController = flightController;
        this.mapCamera = mapCamera;
    }

    public void showAirportTooltip(Airport airport) {
        removeTooltip();
        showAirportHoverTooltip(airport);
    }

    public void showAirportTooltipForFlight(Airport airport, PassengerSelectionListener passengerSelectionListener) {
        showAirportTooltip(airport, passengerSelectionListener);
    }

    private void showAirportTooltip(Airport airport, PassengerSelectionListener passengerSelectionListener) {
        removeTooltip();
        currentClickAirline = null;
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
        showAirlineHoverTooltip(airline);

        double currentPlayerMoney = gameData.players.get(llh.getMyId()).getMoney();
        boolean currentPlayerAP = gameData.players.get(llh.getMyId()).actionPoints > 0;
        boolean canBuyDuringCurrentStage = gameData.currentState == GameData.State.AIRLINES;
        if (!canBuyDuringCurrentStage) return;

        currentTooltip = new AirlineClickTooltip(skin, facade, airline, currentPlayerMoney, currentPlayerAP, canBuyDuringCurrentStage, llh);
        currentClickAirline = airline;
        uiStageHUD.addActor(currentTooltip.asWindow());
        updateTooltipPosition();
    }

    private void showAirportHoverTooltip(Airport airport) {
        if (hoverTooltip != null && hoverAirport == airport) return;

        removeHoverTooltip();
        hoverAirport = airport;
        hoverTooltip = new AirportHoverTooltip(skin, airport);
        uiStageHUD.addActor(hoverTooltip);
        updateHoverTooltipPosition();
    }

    private void showAirlineHoverTooltip(Airline airline) {
        if (hoverTooltip != null && hoverAirline == airline) return;

        removeHoverTooltip();
        hoverAirline = airline;
        hoverTooltip = new AirlineHoverTooltip(skin, airline);
        uiStageHUD.addActor(hoverTooltip);
        updateHoverTooltipPosition();
    }

    public void removeTooltip() {
        if (currentTooltip != null) {
            currentTooltip.asWindow().remove();
            currentTooltip = null;
        }
        currentClickAirline = null;
        removeHoverTooltip();
    }

    private void removeHoverTooltip() {
        if (hoverTooltip != null) {
            hoverTooltip.remove();
            hoverTooltip = null;
        }
        hoverAirport = null;
        hoverAirline = null;
    }

    public void updateTooltipPosition() {
        updateClickTooltipPosition();
        updateHoverTooltipPosition();
        clearTooltipScrollFocusWhenPointerLeaves();
    }

    public boolean isPointerOverTooltip() {
        return isPointerOverWindow(currentTooltip == null ? null : currentTooltip.asWindow()) ||
            isPointerOverWindow(hoverTooltip);
    }

    private void clearTooltipScrollFocusWhenPointerLeaves() {
        Actor scrollFocus = uiStageHUD.getScrollFocus();
        if (scrollFocus == null || isPointerOverTooltip()) return;

        Window clickWindow = currentTooltip == null ? null : currentTooltip.asWindow();
        if (isDescendantOf(scrollFocus, clickWindow) || isDescendantOf(scrollFocus, hoverTooltip)) {
            uiStageHUD.setScrollFocus(null);
        }
    }

    private boolean isPointerOverWindow(Window window) {
        return window != null && window.getStage() != null && ComponentHover.isMouseOver(window);
    }

    private boolean isDescendantOf(Actor actor, Window window) {
        return actor != null && window != null && (actor == window || window.isAscendantOf(actor));
    }

    private void updateClickTooltipPosition() {
        if (currentTooltip == null) return;

        var window = currentTooltip.asWindow();

        window.pack();

        if (currentClickAirline != null) {
            positionAirlineClickTooltip(window);
            return;
        }

        float paddingLeft = 20f;
        float paddingBottom = 20f;
        float paddingTop = 20f;
        float maxHeight = uiStageHUD.getHeight() - paddingBottom - paddingTop;

        if (window.getHeight() > maxHeight) {
            window.setHeight(maxHeight);
        }

        window.setPosition(paddingLeft, paddingBottom);
    }

    private void updateHoverTooltipPosition() {
        if (hoverTooltip == null) return;

        hoverTooltip.pack();

        float paddingLeft = 20f;
        float paddingBottom = 20f;
        float paddingTop = 20f;
        float maxHeight = uiStageHUD.getHeight() - paddingBottom - paddingTop;

        if (hoverTooltip.getHeight() > maxHeight) {
            hoverTooltip.setHeight(maxHeight);
        }

        hoverTooltip.setPosition(paddingLeft, paddingBottom);
    }

    private void positionAirlineClickTooltip(Window window) {
        float worldX = (currentClickAirline.getPortA().getX() + currentClickAirline.getPortB().getX()) / 2f;
        float worldY = (currentClickAirline.getPortA().getY() + currentClickAirline.getPortB().getY()) / 2f;

        Vector3 screenCoords = new Vector3(worldX, worldY, 0f);
        mapCamera.project(screenCoords);
        Vector2 stageCoords = uiStageHUD.screenToStageCoordinates(new Vector2(screenCoords.x, screenCoords.y));

        float x = stageCoords.x - window.getWidth() / 2f;
        float y = stageCoords.y + 20f;

        x = Math.max(20f, Math.min(x, uiStageHUD.getWidth() - window.getWidth() - 20f));
        y = Math.max(20f, Math.min(y, uiStageHUD.getHeight() - window.getHeight() - 20f));
        window.setPosition(x, y);
    }
}
