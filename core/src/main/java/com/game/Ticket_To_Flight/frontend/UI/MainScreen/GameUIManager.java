package com.game.Ticket_To_Flight.frontend.UI.MainScreen;

import com.badlogic.gdx.graphics.OrthographicCamera;
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

    public GameUIManager(Stage uiStageWindow, Stage uiStageHUD, MainClient client, MapSelectionState selectionState, OrthographicCamera mapCamera) {
        WorldMapStyleFactory styleFactory = new WorldMapStyleFactory();
        Skin defaultSkin = styleFactory.createBasicWindow();
        Skin investSkin = styleFactory.createInvestWindow();
        GameData gameData = client.getGameData();
        LowLevelHandlerFront llh = client.getLlh();

        this.windowManager = new WindowManager(uiStageWindow, defaultSkin, investSkin, this, gameData, llh);
        this.tooltipManager = new TooltipManager(uiStageHUD, defaultSkin, this, gameData, llh, selectionState, mapCamera);
        this.hud = new HUDOverlay(uiStageHUD, defaultSkin, gameData, llh);
        this.flightController = new MainFlightController(uiStageHUD, defaultSkin, gameData, llh, this);
        this.airlinesControls = new AirlinesControls(uiStageHUD, defaultSkin, gameData, llh, this);
    }

    public boolean showAuctionWindow() { return windowManager.showAuctionWindow(); }
    public boolean showInvestWindow() { return windowManager.showInvestWindow(); }
    public void showSuccessWindow(String message) { windowManager.showSuccessWindow(message); }
    public boolean showAbilitiesWindow() { return windowManager.showAbilitiesWindow(); }
    public boolean showPlaneWindow() { return windowManager.showPlaneWindow(); }
    public void setWindowOpen(boolean windowOpen) { windowManager.setWindowOpen(windowOpen); }

    public void showAirportTooltip(Airport airport) { tooltipManager.showAirportTooltip(airport); }
    public void showAirportTooltipForFlight(Airport airport, PassengerSelectionListener listener) {
        tooltipManager.showAirportTooltipForFlight(airport, listener);
    }
    public void showAirlineTooltip(Airline airline) { tooltipManager.showAirlineTooltip(airline); }
    public void removeTooltip() { tooltipManager.removeTooltip(); }
    public void handleFlightAirportClick(Airport airport) { flightController.handleAirportClick(airport); }
    public void handleFlightAirlineClick(Airline airline) { flightController.handleAirlineClick(airline); }
    public boolean shouldFinishAirlinesAfterPurchase() { return airlinesControls.shouldFinishAfterPurchase(); }
    public void resetAirlinesFinishChoice() { airlinesControls.resetFinishChoice(); }

    public void updateHUDData() { hud.updateHUD(null); }

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
}
