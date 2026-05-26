package com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airline;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.frontend.LowLevelHandlerFront;
import com.game.Ticket_To_Flight.frontend.MainClient;
import com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.GameUIManagerDirectory.HUD.HUDOverlay;
import com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.Managers.TooltipManager;
import com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.Managers.WindowManager;
import com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.GameUIManagerDirectory.Skins.WorldMapStyleFactory;

public class GameUIManager {

    private final WindowManager windowManager;
    private final TooltipManager tooltipManager;
    private final HUDOverlay hud;
    private final Stage uiStageHUD;
    private final Skin defaultSkin;
    private final GameData gameData;
    private final LowLevelHandlerFront llh;
    private final MapSelectionState selectionState;
    private TextButton flyButton;

    public GameUIManager(Stage uiStageWindow, Stage uiStageHUD, MainClient client, MapSelectionState selectionState, OrthographicCamera mapCamera) {
        WorldMapStyleFactory styleFactory = new WorldMapStyleFactory();
        Skin skin_default_window = styleFactory.createBasicWindow();
        Skin skin_invest_window = styleFactory.createInvestWindow();

        this.uiStageHUD = uiStageHUD;
        this.defaultSkin = skin_default_window;
        this.gameData = client.getGameData();
        this.llh = client.getLlh();
        this.selectionState = selectionState;

        this.windowManager = new WindowManager(uiStageWindow, skin_default_window, skin_invest_window, this, gameData, client.getLlh());
        this.tooltipManager = new TooltipManager(uiStageHUD, skin_default_window, this, gameData, client.getLlh(), selectionState, mapCamera);
        this.hud = new HUDOverlay(uiStageHUD, skin_default_window, gameData, client.getLlh());
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
        hud.updateHUD(null);
    }

    public void updateDynamicControls() {
        updateFlyButton();
        tooltipManager.updateTooltipPosition();
    }

    public void resize(int width, int height) {
        windowManager.centerCurrentWindow();
        hud.resize();
        positionFlyButton();
    }

    private void updateFlyButton() {
        boolean isFlightPhase = gameData.currentState == GameData.State.FLIGHTS;
        if (!isFlightPhase) {
            if (flyButton != null) {
                flyButton.remove();
                flyButton = null;
            }
            selectionState.clearFlightSelection();
            return;
        }

        if (flyButton == null) {
            flyButton = new TextButton("Fly", defaultSkin);
            flyButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (flyButton.isDisabled() || !selectionState.hasCompleteFlightSelection()) return;

                    llh.sendRouteResponse(
                        selectionState.getSelectedAirportId(),
                        selectionState.getSelectedAirlineId(),
                        selectionState.getSelectedPassengerTypeId()
                    );
                    selectionState.clearFlightSelection();
                    removeTooltip();
                    showSuccessWindow("Flight request was sent to server");
                }
            });
            uiStageHUD.addActor(flyButton);
        }

        boolean isMyFlightTurn = gameData.currentPlayer == llh.getMyId();
        if (!isMyFlightTurn) {
            selectionState.clearFlightSelection();
        }

        boolean canFly = isMyFlightTurn && selectionState.hasCompleteFlightSelection();
        flyButton.setDisabled(!canFly);
        flyButton.getLabel().setColor(canFly ? Color.WHITE : Color.LIGHT_GRAY);
        positionFlyButton();
    }

    private void positionFlyButton() {
        if (flyButton == null) return;
        flyButton.setSize(260, 80);
        flyButton.setPosition((uiStageHUD.getWidth() - flyButton.getWidth()) / 2f, 40);
    }
}
