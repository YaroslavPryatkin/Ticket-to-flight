package com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.GameUIManagerDirectory.HUD;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class HUDOverlay extends Table {
    private final Label roundLabel;
    private final Label stageLabel;
    private final Label timeLabel;

    private final Label moneyLabel;
    private final Label incomeLabel;
    private final Label currentBetLabel;

    public HUDOverlay(Skin skin) {
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

        Table leftStats = new Table();

        leftStats.add(roundLabel).left().row();
        leftStats.add(stageLabel).left().padTop(15).row();
        leftStats.add(timeLabel).left().padTop(15).row();

        Table rightStats = new Table();

        rightStats.add(moneyLabel).right().row();
        rightStats.add(incomeLabel).right().padTop(15).row();

        this.add(leftStats).expandX().left().top();
        this.add(rightStats).expandX().right().top();
    }

    public void updateHUD(int round, String stage, int time, double money, double income, int currentBet) {
        roundLabel.setText("Round: " + round);
        stageLabel.setText("Stage: " + stage);
        timeLabel.setText("Time: " + time + "s");
        moneyLabel.setText("Money: $" + money);
        incomeLabel.setText("Income: +$" + income);
    }
}
