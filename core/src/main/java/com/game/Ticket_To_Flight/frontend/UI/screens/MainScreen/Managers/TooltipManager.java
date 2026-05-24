package com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.Managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airline;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.frontend.LowLevelHandlerFront;
import com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.GameUIManager;
import com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.GameUIManagerDirectory.Tooltips.AirlineTooltipWindow;
import com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.GameUIManagerDirectory.Tooltips.AirportTooltipWindow;

public class TooltipManager {
    private final Stage uiStageHUD;
    private final Skin skin;
    private final GameUIManager facade;
    private final GameData gameData;
    private final LowLevelHandlerFront llh;

    private Window currentTooltip;

    public TooltipManager(Stage uiStageHUD, Skin skin, GameUIManager facade, GameData gameData, LowLevelHandlerFront llh) {
        this.uiStageHUD = uiStageHUD;
        this.skin = skin;
        this.facade = facade;
        this.gameData = gameData;
        this.llh = llh;
    }

    public void showAirportTooltip(Airport airport) {
        removeTooltip();
        currentTooltip = new AirportTooltipWindow(skin, airport);
        uiStageHUD.addActor(currentTooltip);
        updateTooltipPosition();
    }

    public void showAirlineTooltip(Airline airline) {
        removeTooltip();

        double currentPlayerMoney = gameData.players.get(llh.getMyId()).getMoney();
        boolean currentBuyingPhase = (gameData.currentState == GameData.State.AIRLINES);

        currentTooltip = new AirlineTooltipWindow(skin, facade, airline, currentPlayerMoney, currentBuyingPhase, llh);
        uiStageHUD.addActor(currentTooltip);
        updateTooltipPosition();
    }

    public void removeTooltip() {
        if (currentTooltip != null) {
            currentTooltip.remove();
            currentTooltip = null;
        }
    }

    private void updateTooltipPosition() {
        if (currentTooltip == null) return;

        Vector2 stageCoords = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        uiStageHUD.screenToStageCoordinates(stageCoords);
        currentTooltip.setPosition(stageCoords.x + 15, stageCoords.y + 15);
    }
}
