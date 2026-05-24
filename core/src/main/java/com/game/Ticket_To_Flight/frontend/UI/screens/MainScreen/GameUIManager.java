package com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airline;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.frontend.MainClient;
import com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.Managers.TooltipManager;
import com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.Managers.WindowManager;
import com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.Managers.HUDController;
import com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.GameUIManagerDirectory.Skins.WorldMapStyleFactory;

public class GameUIManager {

    private final WindowManager windowManager;
    private final TooltipManager tooltipManager;
    private final HUDController hudController;

    public GameUIManager(Stage uiStageWindow, Stage uiStageHUD, MainClient client) {
        WorldMapStyleFactory styleFactory = new WorldMapStyleFactory();
        Skin skin_default_window = styleFactory.createBasicWindow();
        Skin skin_invest_window = styleFactory.createInvestWindow();

        GameData gameData = client.getGameData();

        this.windowManager = new WindowManager(uiStageWindow, skin_default_window, skin_invest_window, this, gameData, client.getLlh());
        this.tooltipManager = new TooltipManager(uiStageHUD, skin_default_window, this, gameData, client.getLlh());
        this.hudController = new HUDController(uiStageHUD, skin_default_window, gameData, client.getLlh());
    }

    public boolean showAuctionWindow() { return windowManager.showAuctionWindow(); }
    public boolean showInvestWindow() { return windowManager.showInvestWindow(); }
    public void showSuccessWindow(String message) { windowManager.showSuccessWindow(message); }
    public boolean showAbilitiesWindow() { return windowManager.showAbilitiesWindow(); }
    public boolean showPlaneWindow() { return windowManager.showPlaneWindow(); }
    public void setWindowOpen(boolean windowOpen) {
        windowManager.setWindowOpen(windowOpen);
    }

    public void showAirportTooltip(Airport airport) { tooltipManager.showAirportTooltip(airport); }
    public void showAirlineTooltip(Airline airline) { tooltipManager.showAirlineTooltip(airline); }
    public void removeTooltip() { tooltipManager.removeTooltip(); }

    public void updateHUDData() {
        hudController.updateData();
    }

    public void resize(int width, int height) {
        windowManager.centerCurrentWindow();
        hudController.resize();
    }
}
