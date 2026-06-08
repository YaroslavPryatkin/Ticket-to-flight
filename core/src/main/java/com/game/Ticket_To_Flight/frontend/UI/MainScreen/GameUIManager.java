package com.game.Ticket_To_Flight.frontend.UI.MainScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airline;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.frontend.LowLevelHandlerFront;
import com.game.Ticket_To_Flight.frontend.MainClient;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.Airlines.AirlinesControls;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.Flights.MainFlightController;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.HUD.HUDDirectory.StandardHUDDirectory.PlayersListHUD;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.HUD.HUDOverlay;
import com.game.Ticket_To_Flight.frontend.components.tables.flight.AbstractFlightPanel;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.Skins.WorldMapStyleFactory;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.Managers.LeftDownCornerTooltipManager;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.Managers.WindowManager;

public class GameUIManager {

    private final WindowManager windowManager;
    private final LeftDownCornerTooltipManager leftDownCornerTooltipManager;
    private final HUDOverlay hudOverlay;
    private final MainFlightController mainFlightController;
    private final AirlinesControls airlinesControls;
    private final Stage uiStageHUD;
    private final GameData gameData;
    private final LowLevelHandlerFront llh;

    private Airport currentClickedAirport;
    private Airline currentClickedAirline;

    public MainFlightController getMainFlightController() {
        return mainFlightController;
    }

    public boolean isCurrentClickedAirline(Airline line) {
        if (line == null) return false;
        return line.equals(currentClickedAirline);
    }

    public boolean isCurrentClickedAirport(Airport port) {
        if (port == null) return false;
        return port.equals(currentClickedAirport);
    }

    public GameUIManager(Stage uiStageWindow, Stage uiStageHUD, MainClient client, OrthographicCamera mapCamera) {
        this.uiStageHUD = uiStageHUD;
        WorldMapStyleFactory styleFactory = new WorldMapStyleFactory();
        Skin defaultSkin = styleFactory.createBasicWindow();
        Skin investSkin = styleFactory.createInvestWindow();
        gameData = client.getGameData();
        llh = client.getLlh();

        this.windowManager = new WindowManager(uiStageWindow, defaultSkin, investSkin, this, gameData, llh);
        this.hudOverlay = new HUDOverlay(uiStageHUD, defaultSkin, gameData, llh);
        this.mainFlightController = new MainFlightController(uiStageHUD, defaultSkin, gameData, llh, this, hudOverlay.getFlightHUD());
        this.leftDownCornerTooltipManager = new LeftDownCornerTooltipManager(uiStageHUD, defaultSkin);
        this.airlinesControls = new AirlinesControls(uiStageHUD, defaultSkin, gameData, llh, this, mapCamera);
    }

    public boolean showAuctionWindow() {
        return windowManager.showAuctionWindow();
    }

    public boolean showInvestWindow() {
        return windowManager.showInvestWindow();
    }

    public void showNotificationWindow(String message) {
        windowManager.showNotificationWindow(message);
    }

    public boolean showAbilitiesWindow() {
        return windowManager.showAbilitiesWindow();
    }

    public boolean showPlaneWindow() {
        return windowManager.showPlaneWindow();
    }

    public void setWindowOpen(boolean windowOpen) {
        windowManager.setWindowOpen(windowOpen);
    }

    public boolean isWindowOpen() {
        return windowManager.isWindowOpen();
    }

    public boolean canClickAndScrollMap() {
        return !windowManager.isPointerOverCurrentWindow() &&
            !isPointerOverHudActor() &&
            !leftDownCornerTooltipManager.isPointerOverTooltip() &&
            !airlinesControls.isPointerOverTooltip();
    }

    private boolean canChooseAirline() {
        return gameData.currentState == GameData.State.AIRLINES &&
            gameData.currentPlayer == llh.getMyId() &&
            llh.getCurrentStateState() != LowLevelHandlerFront.Flags.CurrentStateState.WAITING_FOR_SERVER_RESPONSE;
    }

    private boolean canChooseFlight() {
        return gameData.currentState == GameData.State.FLIGHTS && gameData.currentPlayer == llh.getMyId();
    }

    public boolean isPointerOverHudActor() {
        Vector2 stageCoords = uiStageHUD.screenToStageCoordinates(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
        Actor hitActor = uiStageHUD.hit(stageCoords.x, stageCoords.y, true);
        while (hitActor != null && hitActor != uiStageHUD.getRoot()) {
            if (hitActor instanceof AbstractFlightPanel || hitActor instanceof PlayersListHUD) {
                return true;
            }
            if (hitActor.getListeners().size > 0 || hitActor.getCaptureListeners().size > 0) {
                return true;
            }
            hitActor = hitActor.getParent();
        }
        return false;
    }

    public void handleAirportClick(Airport airport) {
        if (!airport.equals(currentClickedAirport))
            setCurrentClickedAirport(airport);

        if (canChooseFlight())
            mainFlightController.handleAirportClick(airport);

        if (canChooseAirline())
            airlinesControls.removeTooltip();
    }

    public void handleAirlineClick(Airline airline) {
        if (!airline.equals(currentClickedAirline))
            setCurrentClickedAirline(airline);

        if (canChooseFlight())
            mainFlightController.handleAirlineClick(airline);


        if (canChooseAirline())
            airlinesControls.showTooltip(airline);
    }

    public void handleEmptyMapClick() {
        currentClickedAirport = null;
        currentClickedAirline = null;
        leftDownCornerTooltipManager.removeTooltip();
        airlinesControls.removeTooltip();
    }

    private void setCurrentClickedAirport(Airport port) {
        leftDownCornerTooltipManager.showAirportTooltip(port);
        currentClickedAirport = port;
        currentClickedAirline = null;
    }

    private void setCurrentClickedAirline(Airline line) {
        leftDownCornerTooltipManager.showAirlineTooltip(line);
        currentClickedAirline = line;
        currentClickedAirport = null;
    }

    public void updateHUDData() {
        if (canChooseAirline())
            airlinesControls.setActive();
        if (canChooseFlight())
            mainFlightController.setActive();

        hudOverlay.updateStandardHUD(null);
    }

    public void updateDynamicControls() {
        if (canChooseFlight()) {
            mainFlightController.update();
        } else {
            mainFlightController.setInactive();
        }

        if (canChooseAirline()) {
            airlinesControls.updatePassButton();
            airlinesControls.updateTooltip(currentClickedAirline);
        } else {
            airlinesControls.setInactive();
        }

        leftDownCornerTooltipManager.update();
    }

    public void resize() {
        windowManager.centerCurrentWindow();
        hudOverlay.resize();
        mainFlightController.positionPlaneWindow();
        airlinesControls.positionPassButton();
    }
}
