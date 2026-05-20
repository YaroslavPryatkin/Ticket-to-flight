package com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.GameUIManagerDirectory;

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

    public HUDOverlay(Skin skin) {
        this.setFillParent(true);
        this.top();
        this.pad(15);

        roundLabel = new Label("Round: 1", skin);
        stageLabel = new Label("Stage: WAITING", skin);

        timeLabel = new Label("Time: 120s", skin);
        timeLabel.setColor(Color.ORANGE);

        moneyLabel = new Label("Money: $0", skin);

        incomeLabel = new Label("Income: +$0", skin);
        incomeLabel.setColor(Color.GREEN);

        Table leftStats = new Table();
        leftStats.add(roundLabel).padRight(30);
        leftStats.add(stageLabel).padRight(30);
        leftStats.add(timeLabel);

        Table rightStats = new Table();
        rightStats.add(moneyLabel).left().row();
        rightStats.add(incomeLabel).left().padTop(5).row();

        this.add(leftStats).expandX().left();
        this.add(rightStats).expandX().right();
    }

    public void updateHUD(int round, String stage, int time, double money, double income) {
        roundLabel.setText("Round: " + round);
        stageLabel.setText("Stage: " + stage);
        timeLabel.setText("Time: " + time + "s");
        moneyLabel.setText("Money: $" + money);
        incomeLabel.setText("Income: +$" + income);
    }
}
