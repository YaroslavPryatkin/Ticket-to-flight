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
import java.util.function.Consumer;

public class StandardHUD extends Table {
    private final Skin skin;
    private final Table summaryTable;
    private final TextButton passBtn;
    private final TextButton stepBackBtn;
    private final TextButton resetBtn;
    private final TextButton finishBtn;
    private final TextButton finishAndPassBtn;

    public StandardHUD(Skin skin) {
        this.skin = skin;
        setFillParent(true);
        setTouchable(Touchable.childrenOnly);

        summaryTable = new Table();
        summaryTable.top().right();

        passBtn = new RoundedButton("Pass", skin);
        stepBackBtn = new RoundedButton("Step Back", skin);
        resetBtn = new RoundedButton("Reset", skin);
        finishBtn = new RoundedButton("Finish Flight", skin);
        finishAndPassBtn = new RoundedButton("Finish Flight and Pass", skin);

        passBtn.getLabel().setColor(Color.RED);

        addActor(summaryTable);
        addActor(passBtn);
        addActor(stepBackBtn);
        addActor(resetBtn);
        addActor(finishBtn);
        addActor(finishAndPassBtn);
    }

    public void setCallbacks(Runnable onReset, Runnable onBack, Runnable onPass, Consumer<Boolean> onFinish) {
        passBtn.clearListeners();
        stepBackBtn.clearListeners();
        resetBtn.clearListeners();
        finishBtn.clearListeners();
        finishAndPassBtn.clearListeners();

        passBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { onPass.run(); }
        });
        stepBackBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { onBack.run(); }
        });
        resetBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { onReset.run(); }
        });
        finishBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { onFinish.accept(false); }
        });
        finishAndPassBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { onFinish.accept(true); }
        });
    }

    public void updateData(MainFlightController.Step step, Route route) {
        summaryTable.clearChildren();
        summaryTable.add(new SingleLineText("Possible Income: " + formatPossibleIncome(route), skin)).right().padBottom(15).row();
        summaryTable.add(new SingleLineText("Step: " + getStepText(step), skin)).right().padBottom(15).row();

        boolean selectingPlane = step == MainFlightController.Step.SELECT_PLANE;
        passBtn.setVisible(selectingPlane);
        stepBackBtn.setVisible(!selectingPlane);
        resetBtn.setVisible(!selectingPlane);
        finishBtn.setVisible(!selectingPlane);
        finishAndPassBtn.setVisible(!selectingPlane);

        boolean canFinish = route != null && route.canFinishRoute();
        finishBtn.setDisabled(!canFinish);
        finishAndPassBtn.setDisabled(!canFinish);

        Color finishLabelColor = canFinish ? Color.WHITE : Color.LIGHT_GRAY;
        finishBtn.getLabel().setColor(finishLabelColor);
        finishAndPassBtn.getLabel().setColor(finishLabelColor);
    }

    public void layoutFor(float width, float height) {
        summaryTable.pack();
        summaryTable.setPosition(width - summaryTable.getWidth() - 20, height - summaryTable.getHeight() - 20);

        float buttonHeight = 80;
        float marginBottom = 40;
        float spacingX = 24;
        float spacingY = 24;

        if (passBtn.isVisible()) {
            float buttonWidth = 280;
            passBtn.setSize(buttonWidth, buttonHeight);
            passBtn.setPosition((width - buttonWidth) / 2f, marginBottom);
            return;
        }

        float stepBackWidth = 240;
        float resetWidth = 200;
        float finishWidth = 500;
        float finishAndPassWidth = 500;

        stepBackBtn.setSize(stepBackWidth, buttonHeight);
        resetBtn.setSize(resetWidth, buttonHeight);
        finishBtn.setSize(finishWidth, buttonHeight);
        finishAndPassBtn.setSize(finishAndPassWidth, buttonHeight);

        float row1Width = stepBackWidth + resetWidth + spacingX;
        float row1StartX = (width - row1Width) / 2f;
        float row1Y = marginBottom + buttonHeight + spacingY;

        stepBackBtn.setPosition(row1StartX, row1Y);
        resetBtn.setPosition(row1StartX + stepBackWidth + spacingX, row1Y);

        float row2Width = finishWidth + finishAndPassWidth + spacingX;
        float row2StartX = (width - row2Width) / 2f;
        float row2Y = marginBottom;

        finishBtn.setPosition(row2StartX, row2Y);
        finishAndPassBtn.setPosition(row2StartX + finishWidth + spacingX, row2Y);
    }

    public float getSummaryBottomY() {
        return summaryTable.getY();
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
        if (step == MainFlightController.Step.SELECT_PLANE) return "Choose the plane";
        if (step == MainFlightController.Step.CHOOSING_STARTING_AIRPORT) return "Choose the start airport";
        return "Fly";
    }
}
