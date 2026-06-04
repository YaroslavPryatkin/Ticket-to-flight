package com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.HUD.HUDDirectory.FlightHUDDirectory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airline;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Player;
import com.game.Ticket_To_Flight.commonFrontAndBack.Route;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.Flights.MainFlightController;
import com.game.Ticket_To_Flight.frontend.components.buttons.RoundedButton;
import com.game.Ticket_To_Flight.frontend.components.texts.SingleLineText;
import com.game.Ticket_To_Flight.frontend.components.texts.WrappedText;

import java.util.Iterator;
import java.util.Map;

public class StandardHUD extends Table {
    private final Skin skin;
    private final Table summaryTable;
    private final Table routePlate;
    private final TextButton backButton;
    private final TextButton finishButton;
    private final TextButton resetButton;
    private Window routePopup;
    private Route currentRoute;

    public StandardHUD(Skin skin) {
        this.skin = skin;
        setFillParent(true);
        setTouchable(Touchable.childrenOnly);

        summaryTable = new Table();
        summaryTable.top().right();
        routePlate = new Table();
        routePlate.setTouchable(Touchable.enabled);
        routePlate.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                showRoutePopup();
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor toActor) {
                hideRoutePopup();
            }
        });

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
        currentRoute = route;

        summaryTable.clearChildren();
        summaryTable.add(new SingleLineText("Possible Income: " + formatPossibleIncome(route), skin)).right().padBottom(15).row();
        summaryTable.add(new SingleLineText("Step: " + getStepText(step), skin)).right().padBottom(15).row();

        routePlate.clearChildren();
        routePlate.add(new SingleLineText("Current Route: " + formatShortRoute(route), skin)).right();
        summaryTable.add(routePlate).right().row();

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

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (!visible) hideRoutePopup();
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

    private String formatShortRoute(Route route) {
        if (route == null) return "None";
        Airport start = route.startingPort;
        Airport end = route.getCurrentAirport();
        return start.getCityName() + " -> " + end.getCityName();
    }

    private String formatFullRoute(Route route) {
        if (route == null) return "No route selected";

        StringBuilder routeText = new StringBuilder(route.startingPort.getCityName());
        Airport current = route.startingPort;
        for (Airline line : route.getLines()) {
            Airport next = line.getAnotherEnd(current);
            if (next == null) break;
            routeText.append(" -> ").append(next.getCityName());
            current = next;
        }
        return routeText.toString();
    }

    private void showRoutePopup() {
        hideRoutePopup();
        if (getStage() == null) return;

        routePopup = new Window("Full route", skin);
        routePopup.setMovable(false);
        routePopup.pad(25);
        Label routeLabel = new WrappedText(formatFullRoute(currentRoute), skin, 700);
        routePopup.add(routeLabel).width(700);
        routePopup.pack();
        routePopup.setPosition(getStage().getWidth() - routePopup.getWidth() - 60, 120);
        getStage().addActor(routePopup);
    }

    private void hideRoutePopup() {
        if (routePopup != null) {
            routePopup.remove();
            routePopup = null;
        }
    }

    private String getStepText(MainFlightController.Step step) {
        if (step == MainFlightController.Step.SELECT_PLANE) return "Choose plane";
        if (step == MainFlightController.Step.CHOOSE_AIRPORT_GROUP) return "Choose airport and group";
        return "Choose airline";
    }
}
