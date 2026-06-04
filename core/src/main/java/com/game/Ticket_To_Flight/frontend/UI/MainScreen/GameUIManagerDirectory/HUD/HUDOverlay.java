package com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.HUD;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Player;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.frontend.LowLevelHandlerFront;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.HUD.HUDDirectory.FlightHUD;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.HUD.HUDDirectory.StandardHUD;

public class HUDOverlay {
    private final StandardHUD standardHUD;
    private final FlightHUD flightHUD;

    public HUDOverlay(Stage uiStageHUD, Skin skin, GameData gameData, LowLevelHandlerFront llh) {
        this.standardHUD = new StandardHUD(skin, gameData, llh);
        this.flightHUD = new FlightHUD(skin);

        uiStageHUD.addActor(standardHUD);
        uiStageHUD.addActor(flightHUD);
    }

    public void updateStandardHUD(Player chosenPlayer) {
        standardHUD.updateHUD(chosenPlayer);
    }

    public FlightHUD getFlightHUD() {
        return flightHUD;
    }

    public void resize() {
        standardHUD.invalidateHierarchy();
        flightHUD.invalidateHierarchy();
    }
}
