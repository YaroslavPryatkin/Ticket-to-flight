package com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.HUD.HUDDirectory.FlightHUDDirectory;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.Flights.MainFlightController;
import com.game.Ticket_To_Flight.frontend.components.ExpandableListWidget;
import com.game.Ticket_To_Flight.frontend.components.tables.FlightPassengerTableWidget;
import com.game.Ticket_To_Flight.frontend.components.tables.PassengerTableWidget;

import java.util.ArrayList;
import java.util.List;

public class GroupHUD extends Table {
    private final Skin skin;
    private final FlightPassengerTableWidget passengerTable;
    private final ScrollPane scrollPane;

    private float screenWidth = 0;
    private float screenHeight = 0;
    private float currentTopY = 0;

    private List<MainFlightController.ChosenGroup> currentGroups;
    private Runnable onToggle;

    private int lastGroupCount = -1;
    private boolean isInitialized = false;

    private final ExpandableListWidget expandList;

    public GroupHUD(Skin skin) {
        this.skin = skin;
        top().right();
        pad(20);
        setBackground(skin.getDrawable("flight-panel-bg"));

        passengerTable = new FlightPassengerTableWidget(skin);
        scrollPane = new ScrollPane(passengerTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(false, false);
        scrollPane.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (event.getStage() != null) event.getStage().setScrollFocus(scrollPane);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (event.getStage() != null) event.getStage().setScrollFocus(null);
            }
        });

        expandList = new ExpandableListWidget("Chosen Passengers", skin);
        add(expandList).fillX().expandX().row();

        expandList.expand(); // Открыт по умолчанию
    }

    public void setOnToggle(Runnable onToggle) {
        this.onToggle = onToggle;
        expandList.setCallbacks(null, () -> {
            if (screenWidth > 0 && currentTopY > 0) recalculatePosition();
            if (this.onToggle != null) this.onToggle.run();
        });
    }

    public void updateData(List<MainFlightController.ChosenGroup> chosenGroups) {
        this.currentGroups = chosenGroups;
        int currentSize = (chosenGroups == null) ? 0 : chosenGroups.size();

        if (lastGroupCount == currentSize && isInitialized) {
            return;
        }

        this.lastGroupCount = currentSize;
        this.isInitialized = true;
        renderContent();
    }

    private void renderContent() {
        Table content = expandList.getContentTable();
        content.clearChildren();

        List<PassengerTableWidget> rows = new ArrayList<>();
        if (currentGroups != null) {
            for (MainFlightController.ChosenGroup group : currentGroups) {
                rows.add(new PassengerTableWidget(group.passengerType));
            }
        }
        passengerTable.setRows(rows);

        content.add(scrollPane).width(760).height(260).padTop(10).row();

        if (screenWidth > 0 && currentTopY > 0) {
            recalculatePosition();
        }
    }

    public void layoutFor(float width, float height, float topY) {
        this.screenWidth = width;
        this.screenHeight = height;
        this.currentTopY = topY;
        recalculatePosition();
    }

    private void recalculatePosition() {
        pack();
        setWidth(Math.max(getWidth(), 820));
        setPosition(screenWidth - getWidth() - 20, currentTopY - getHeight() - 14);
    }
}
