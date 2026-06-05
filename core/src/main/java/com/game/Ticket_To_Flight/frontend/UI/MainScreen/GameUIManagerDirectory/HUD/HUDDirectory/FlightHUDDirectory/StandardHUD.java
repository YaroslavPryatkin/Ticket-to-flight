package com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.HUD.HUDDirectory.FlightHUDDirectory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Player;
import com.game.Ticket_To_Flight.commonFrontAndBack.Route;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.Flights.MainFlightController;
import com.game.Ticket_To_Flight.frontend.components.buttons.RoundedButton;
import com.game.Ticket_To_Flight.frontend.components.texts.SingleLineText;

import java.util.Iterator;
import java.util.Map;

public class StandardHUD extends Table {
    private final Skin skin;
    private final Table summaryTable;
    private final TextButton backButton;
    private final TextButton finishButton;
    private final TextButton resetButton;

    public StandardHUD(Skin skin) {
        this.skin = skin;
        setFillParent(true);
        setTouchable(Touchable.childrenOnly);

        summaryTable = new Table();
        summaryTable.top().right();

        backButton = new RoundedButton("Step Back", skin);
        finishButton = new RoundedButton("Finish Flight", skin);
        resetButton = new RoundedButton("Reset", skin);

        addActor(summaryTable);
        addActor(backButton);
        addActor(finishButton);
        addActor(resetButton);
    }

    public void setCallbacks(Runnable onReset, Runnable onBack, Runnable onFinish) {
        resetButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { onReset.run(); }
        });
        backButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { onBack.run(); }
        });
        finishButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { onFinish.run(); }
        });
    }

    public void updateData(MainFlightController.Step step, Route route) {
        summaryTable.clearChildren();
        summaryTable.add(new SingleLineText("Possible Income: " + formatPossibleIncome(route), skin)).right().padBottom(15).row();
        summaryTable.add(new SingleLineText("Step: " + getStepText(step), skin)).right().padBottom(15).row();

        boolean canFinish = route != null && route.canFinishRoute();
        finishButton.setDisabled(!canFinish);
        finishButton.getLabel().setColor(canFinish ? Color.WHITE : Color.LIGHT_GRAY);
    }

    public void layoutFor(float width, float height) {
        summaryTable.pack();
        summaryTable.setPosition(width - summaryTable.getWidth() - 20, height - summaryTable.getHeight() - 230);

        float buttonWidth = 340;
        float buttonHeight = 80;
        float margin = 40;
        backButton.setSize(buttonWidth, buttonHeight);
        finishButton.setSize(buttonWidth, buttonHeight);
        resetButton.setSize(buttonWidth, buttonHeight);

        backButton.setPosition(margin, margin);
        finishButton.setPosition((width - buttonWidth) / 2f, margin);
        resetButton.setPosition(width - buttonWidth - margin, margin);
    }

    private String formatPossibleIncome(Route route) {
        if (route == null) return "$0";

        int total = 0;
        Iterator<Map.Entry<Player, Integer>> iterator = route.getIncomeChangeIterator();
        Map.Entry<Player, Integer> entry;
        while ((entry = iterator.next()) != null) {
            total += entry.getValue();
        }
        return "$" + total;
    }

    private String getStepText(MainFlightController.Step step) {
        if (step == MainFlightController.Step.SELECT_PLANE) return "Choose plane";
        if (step == MainFlightController.Step.CHOOSE_AIRPORT_GROUP) return "Choose airport and group";
        return "Choose airline";
    }
}
