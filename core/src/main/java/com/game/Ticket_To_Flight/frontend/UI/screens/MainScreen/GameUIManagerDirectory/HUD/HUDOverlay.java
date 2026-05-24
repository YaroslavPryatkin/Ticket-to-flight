package com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.GameUIManagerDirectory.HUD;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.game.Ticket_To_Flight.Utilities.MapHolder;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Player;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PlaneType;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.frontend.LowLevelHandlerFront;

import java.util.Iterator;
import java.util.Map;

public class HUDOverlay extends Table {
    private final GameData gameData;
    private final LowLevelHandlerFront llh;

    private final Label roundLabel;
    private final Label stageLabel;
    private final Label timeLabel;

    private final Label moneyLabel;
    private final Label incomeLabel;
    private final Label currentBetLabel;

    private final Label planesLabel;

    public HUDOverlay(Stage uiStageHUD, Skin skin, GameData gameData, LowLevelHandlerFront llh) {
        this.gameData = gameData;
        this.llh = llh;

        this.setFillParent(true);
        this.top();
        this.pad(20);

        roundLabel = new Label("Round: ", skin);
        stageLabel = new Label("Stage: ", skin);

        timeLabel = new Label("Time: ", skin);
        timeLabel.setColor(Color.ORANGE);

        moneyLabel = new Label("Money: ", skin);

        incomeLabel = new Label("Income: ", skin);
        incomeLabel.setColor(Color.GREEN);

        currentBetLabel = new Label("Current bet: ", skin);
        currentBetLabel.setColor(Color.ORANGE);

        planesLabel = new Label("Planes: None", skin);
        planesLabel.setColor(Color.CYAN);

        Table leftStats = new Table();
        leftStats.add(roundLabel).left().row();
        leftStats.add(stageLabel).left().padTop(15).row();
        leftStats.add(timeLabel).left().padTop(15).row();

        Table rightStats = new Table();
        rightStats.add(moneyLabel).right().row();
        rightStats.add(incomeLabel).right().padTop(15).row();

        rightStats.add(planesLabel).right().padTop(15).row();
        rightStats.add(currentBetLabel).right().padTop(15).row();

        this.add(leftStats).expandX().left().top();
        this.add(rightStats).expandX().right().top();

        uiStageHUD.addActor(this);
    }

    public void updateHUD() {
        int round = gameData.roundNumber;
        String stage = gameData.currentState.toString();
        int time = 120;

        Player me = gameData.players.get(llh.getMyId());

        int money = me.getMoney();
        int income = me.getIncome();
        int currentBet = me.getAuctionBet();

        roundLabel.setText("Round: " + round);
        stageLabel.setText("Stage: " + stage);
        timeLabel.setText("Time: " + time + "s");
        moneyLabel.setText("Money: $" + money);
        incomeLabel.setText("Income: +$" + income);

        if (stage.equalsIgnoreCase("Auction")) {
            currentBetLabel.setVisible(true);
            currentBetLabel.setText("Current bet: $" + currentBet);
        } else {
            currentBetLabel.setVisible(false);
        }

        StringBuilder planesText = new StringBuilder("Planes: ");
        boolean hasPlanes = false;

        Iterator<Map.Entry<PlaneType, Integer>> it = MapHolder.viewAsEntrySet(me.planes);
        while (it.hasNext()) {
            Map.Entry<PlaneType, Integer> e = it.next();
            if (e != null && e.getValue() > 0) {
                PlaneType plane = e.getKey();
                Integer amount = e.getValue();

                planesText.append(plane.description).append(" (").append(amount).append("), ");
                hasPlanes = true;
            }
        }

        if (hasPlanes) {
            planesText.setLength(planesText.length() - 2);
        } else {
            planesText.append("None");
        }

        planesLabel.setText(planesText.toString());
    }

    public void resize() {
        this.invalidateHierarchy();
    }
}
