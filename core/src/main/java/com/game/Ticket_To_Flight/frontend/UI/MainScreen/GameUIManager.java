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
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.Flights.PassengerSelectionListener;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.HUD.HUDOverlay;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.Skins.WorldMapStyleFactory;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.Managers.TooltipManager;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.Managers.WindowManager;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.MapInput.MapSelectionState;

public class GameUIManager {

    private final WindowManager windowManager;
    private final TooltipManager tooltipManager;
    private final HUDOverlay hud;
    private final MainFlightController flightController;
    private final AirlinesControls airlinesControls;
    private final Stage uiStageHUD;

    public GameUIManager(Stage uiStageWindow, Stage uiStageHUD, MainClient client, MapSelectionState selectionState, OrthographicCamera mapCamera) {
        this.uiStageHUD = uiStageHUD;

        WorldMapStyleFactory styleFactory = new WorldMapStyleFactory();
        Skin defaultSkin = styleFactory.createBasicWindow();
        Skin investSkin = styleFactory.createInvestWindow();
        GameData gameData = client.getGameData();
        LowLevelHandlerFront llh = client.getLlh();

        this.windowManager = new WindowManager(uiStageWindow, defaultSkin, investSkin, this, gameData, llh);
        this.hud = new HUDOverlay(uiStageHUD, defaultSkin, gameData, llh);
        this.flightController = new MainFlightController(uiStageHUD, defaultSkin, gameData, llh, this, hud.getFlightHUD(), selectionState);
        this.tooltipManager = new TooltipManager(uiStageHUD, defaultSkin, this, gameData, llh, selectionState, mapCamera, flightController);
        this.airlinesControls = new AirlinesControls(uiStageHUD, defaultSkin, gameData, llh, this);
    }

    public boolean showAuctionWindow() { return windowManager.showAuctionWindow(); }
    public boolean showInvestWindow() { return windowManager.showInvestWindow(); }
    public void showSuccessWindow(String message) { windowManager.showSuccessWindow(message); }
    public boolean showAbilitiesWindow() { return windowManager.showAbilitiesWindow(); }
    public boolean showPlaneWindow() { return windowManager.showPlaneWindow(); }
    public void setWindowOpen(boolean windowOpen) { windowManager.setWindowOpen(windowOpen); }
    public boolean isWindowOpen() { return windowManager.isWindowOpen(); }
    public boolean isPointerOverWindow() { return windowManager.isPointerOverCurrentWindow(); }

    public void showAirportTooltip(Airport airport) { tooltipManager.showAirportTooltip(airport); }
    public void showAirportTooltipForFlight(Airport airport, PassengerSelectionListener listener) {
        tooltipManager.showAirportTooltipForFlight(airport, listener);
    }

    public void showAirlineTooltip(Airline airline) { tooltipManager.showAirlineTooltip(airline); }

    public void removeTooltip() { tooltipManager.removeTooltip(); }
    public boolean isPointerOverTooltip() { return tooltipManager.isPointerOverTooltip(); }
    public boolean isPointerOverHudActor() {
        Vector2 stageCoords = uiStageHUD.screenToStageCoordinates(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
        Actor hitActor = uiStageHUD.hit(stageCoords.x, stageCoords.y, true);
        while (hitActor != null && hitActor != uiStageHUD.getRoot()) {
            if (hitActor.getListeners().size > 0 || hitActor.getCaptureListeners().size > 0) {
                return true;
            }
            hitActor = hitActor.getParent();
        }
        return false;
    }
    public void handleFlightAirportClick(Airport airport) { flightController.handleAirportClick(airport); }
    public void handleFlightAirlineClick(Airline airline) { flightController.handleAirlineClick(airline); }

    public void updateHUDData() { hud.updateStandardHUD(null); }

    public void updateDynamicControls() {
        flightController.update();
        airlinesControls.update();
        tooltipManager.updateTooltipPosition();
    }

    public void resize(int width, int height) {
        windowManager.centerCurrentWindow();
        hud.resize();
        flightController.position();
        airlinesControls.position();
    }

    public MainFlightController getFlightController() {
        return flightController;
    }
}
