package com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.HUD.HUDDirectory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PlaneType;
import com.game.Ticket_To_Flight.commonFrontAndBack.Route;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.Flights.MainFlightController;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.Flights.MainFlightController;
import com.game.Ticket_To_Flight.frontend.components.buttons.RoundedButton;
import com.game.Ticket_To_Flight.frontend.components.texts.SingleLineText;

import java.util.List;

public class FlightHUD extends Table {
    private final Skin skin;
    private final Table statusPanel;
    private final TextButton resetButton, backButton, finishButton;

    public FlightHUD(Skin skin) {
        this.skin = skin;

        this.setFillParent(true);
        this.bottom().left();
        this.pad(40);

        statusPanel = new Table();
        statusPanel.setBackground(skin.getDrawable("background"));
        statusPanel.pad(25);

        resetButton = new RoundedButton("Reset", skin);
        backButton = new RoundedButton("Back", skin);
        finishButton = new RoundedButton("Finish route", skin);

        Table buttonsTable = new Table();
        buttonsTable.add(backButton).width(260).height(80).padRight(20);
        buttonsTable.add(resetButton).width(260).height(80).padRight(20);
        buttonsTable.add(finishButton).width(380).height(80);

        this.add(statusPanel).width(760).minHeight(400).padBottom(20).right().row();
        this.add(buttonsTable).right();

        this.setVisible(false);
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

    public void updateData(MainFlightController.Step step, PlaneType plane, Route route, List<MainFlightController.ChosenGroup> chosenGroups) {
        statusPanel.clearChildren();
        statusPanel.add(new SingleLineText("Flight status", skin)).left().padBottom(20).row();
        statusPanel.add(new SingleLineText("Step: " + getStepText(step), skin)).left().padBottom(15).row();

        String planeText = (plane == null) ? "None" : plane.description;
        statusPanel.add(new SingleLineText("Chosen Plane: " + planeText, skin)).left().padBottom(15).row();

        if (route != null) {
            statusPanel.add(new SingleLineText("Current airport: " + route.getCurrentAirport().getCityName(), skin)).left().padBottom(15).row();
            statusPanel.add(new SingleLineText("Airlines selected: " + route.getLinesCount(), skin)).left().padBottom(15).row();
        }

        for (MainFlightController.ChosenGroup group : chosenGroups) {
            statusPanel.add(new SingleLineText("Group to: " + group.passengerType.typeTo.description, skin)).left().row();
            statusPanel.add(new SingleLineText("Composition: " + group.passengerType.size + " passengers", skin)).left().row();
            statusPanel.add(new SingleLineText("Type: " + group.passengerType.description, skin)).left().padBottom(15).row();
        }

        boolean canFinish = (route != null && route.canFinishRoute());
        finishButton.setDisabled(!canFinish);
        finishButton.getLabel().setColor(canFinish ? Color.WHITE : Color.LIGHT_GRAY);
    }

    private String getStepText(MainFlightController.Step step) {
        if (step == MainFlightController.Step.SELECT_PLANE) return "Choose plane";
        if (step == MainFlightController.Step.CHOOSE_AIRPORT_GROUP) return "Choose airport and group";
        return "Choose airline";
    }
}
