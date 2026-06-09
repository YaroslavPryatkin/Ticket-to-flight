package com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.HUD;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.frontend.LowLevelHandlerFront;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManager;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.HUD.HUDDirectory.AllPlanesListPanel;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.HUD.HUDDirectory.FlightHUD;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.HUD.HUDDirectory.LeftPanel;

public class HUDOverlay {
    private final LeftPanel leftPanel;
    private final AllPlanesListPanel allPlanesListPanel;
    private final FlightHUD flightHUD;

    public HUDOverlay(Stage uiStageHUD, Skin skin, GameData gameData, GameUIManager uiManager, LowLevelHandlerFront llh) {
        this.leftPanel = new LeftPanel(skin, gameData, uiManager, llh);
        this.flightHUD = new FlightHUD(skin);
        this.allPlanesListPanel = new AllPlanesListPanel(skin, gameData, uiManager);

        uiStageHUD.addActor(leftPanel);
        uiStageHUD.addActor(allPlanesListPanel);
        uiStageHUD.addActor(flightHUD);
    }

    public void updateStandardHUD() {
        leftPanel.updateHUD();
        allPlanesListPanel.updateData();
    }

    public FlightHUD getFlightHUD() {
        return flightHUD;
    }

    public void resize() {
        leftPanel.invalidateHierarchy();
        flightHUD.invalidateHierarchy();
        allPlanesListPanel.invalidateHierarchy();
    }
}
