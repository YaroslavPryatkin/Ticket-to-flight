package com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.Managers;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.frontend.LowLevelHandlerFront;
import com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.GameUIManagerDirectory.HUD.HUDOverlay;

public class HUDController {
    private final HUDOverlay hudOverlay;
    private final GameData gameData;
    private final LowLevelHandlerFront llh;

    public HUDController(Stage uiStageHUD, Skin skin, GameData gameData, LowLevelHandlerFront llh) {
        this.gameData = gameData;
        this.llh = llh;
        this.hudOverlay = new HUDOverlay(skin);
        uiStageHUD.addActor(hudOverlay);
    }

    public void updateData() {
        int round = gameData.roundNumber;
        String stage = gameData.currentState.toString();
        int time = 120;
        double money = gameData.players.get(llh.getMyId()).getMoney();
        double income = gameData.players.get(llh.getMyId()).getIncome();
        int currentBet = gameData.players.get(llh.getMyId()).getAuctionBet();

        hudOverlay.updateHUD(round, stage, time, money, income, currentBet);
    }

    public void resize() {
        hudOverlay.invalidateHierarchy();
    }
}
