package com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.HUD.HUDDirectory.FlightHUDDirectory;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.Flights.MainFlightController;
import com.game.Ticket_To_Flight.frontend.components.tables.passenger.FlightPassengerTableWidget;
import com.game.Ticket_To_Flight.frontend.components.tables.passenger.PassengerTableWidget;
import com.game.Ticket_To_Flight.frontend.components.tables.flight.AbstractFlightPanel;

import java.util.ArrayList;
import java.util.List;

public class GroupHUD extends AbstractFlightPanel {
    private final FlightPassengerTableWidget passengerTable;
    private final ScrollPane scrollPane;

    private float currentTopY = 0;
    private List<MainFlightController.ChosenGroup> currentGroups;
    private int lastGroupCount = -1;

    public GroupHUD(Skin skin) {
        super(skin); // Вызов конструктора родителя

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
    }

    public void updateData(List<MainFlightController.ChosenGroup> chosenGroups) {
        this.currentGroups = chosenGroups;
        int currentSize = (chosenGroups == null) ? 0 : chosenGroups.size();

        if (lastGroupCount == currentSize && isInitialized) return;

        this.lastGroupCount = currentSize;
        this.isInitialized = true;
        renderContent();
    }

    @Override
    protected void renderContent() {
        clearChildren();

        // Используем метод родителя для создания шапки
        add(buildHeader("Chosen Passengers")).fillX().expandX().row();

        if (!isCollapsed) {
            List<PassengerTableWidget> rows = new ArrayList<>();
            if (currentGroups != null) {
                for (MainFlightController.ChosenGroup group : currentGroups) {
                    rows.add(new PassengerTableWidget(group.passengerType));
                }
            }
            passengerTable.setRows(rows);
            add(scrollPane).width(760).height(260).padTop(10).row();
        }

        if (screenWidth > 0 && currentTopY > 0) recalculatePosition();
    }

    public void layoutFor(float width, float height, float topY) {
        this.screenWidth = width;
        this.screenHeight = height;
        this.currentTopY = topY;
        recalculatePosition();
    }

    @Override
    protected void recalculatePosition() {
        pack();
        setWidth(Math.max(getWidth(), 820));
        setPosition(screenWidth - getWidth() - 20, currentTopY - getHeight() - 14);
    }
}
