package com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.HUD.HUDDirectory.FlightHUDDirectory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.game.Ticket_To_Flight.Utilities.MapHolder;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PassengerType;
import com.game.Ticket_To_Flight.commonFrontAndBack.Route;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.Flights.MainFlightController;
import com.game.Ticket_To_Flight.frontend.components.buttons.SelectButton;
import com.game.Ticket_To_Flight.frontend.components.tables.expandable.ExpandableListWidget;
import com.game.Ticket_To_Flight.frontend.components.tables.passenger.PassengerTableWidget;
import com.game.Ticket_To_Flight.frontend.components.tables.flight.AbstractFlightPanel;
import com.game.Ticket_To_Flight.frontend.components.texts.SingleLineText;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class BoardingHUD extends AbstractFlightPanel {
    private static final float CONTENT_WIDTH = 760f;
    private static final float CONTENT_HEIGHT = 240f;

    private final ScrollPane scrollPane;
    private final Table contentTable;

    private float currentTopY = 0;
    private Airport currentAirport;
    private Route currentRoute;
    private List<MainFlightController.ChosenGroup> currentGroups;
    private Consumer<PassengerType> onPassengerSelected;

    public BoardingHUD(Skin skin) {
        super(skin);

        contentTable = new Table();
        contentTable.top().left();

        scrollPane = new ScrollPane(contentTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);

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

    public void setCallbacks(Consumer<PassengerType> onPassengerSelected) {
        this.onPassengerSelected = onPassengerSelected;
    }

    public void updateData(Airport airport, Route route, List<MainFlightController.ChosenGroup> chosenGroups) {
        this.currentGroups = chosenGroups;

        if (this.currentAirport == airport && this.currentRoute == route && this.isInitialized) {
            return;
        }

        this.currentAirport = airport;
        this.currentRoute = route;
        this.isInitialized = true;

        renderContent();
    }

    public void forceUpdate() {
        renderContent();
    }

    @Override
    protected void renderContent() {
        clearChildren();
        contentTable.clearChildren();

        String headerTitle = currentAirport != null ? "Boarding: " + currentAirport.getCityName() : "Boarding (No Airport)";
        add(buildHeader(headerTitle)).fillX().expandX().row();

        if (!isCollapsed && currentAirport != null) {
            Iterator<Map.Entry<PassengerType, Integer>> iterator = MapHolder.viewAsEntrySet(currentAirport.getGuests());
            List<ExpandableListWidget> activeLists = new ArrayList<>();
            boolean hasPassengers = false;

            while (iterator.hasNext()) {
                Map.Entry<PassengerType, Integer> entry = iterator.next();
                if (entry == null || entry.getValue() <= 0) continue;

                PassengerType passenger = entry.getKey();

                int takenAmount = 0;
                if (currentGroups != null) {
                    for (MainFlightController.ChosenGroup group : currentGroups) {
                        if (group.airport.equals(currentAirport) && group.passengerType.equals(passenger)) {
                            takenAmount++;
                        }
                    }
                }

                int remaining = entry.getValue() - takenAmount;
                if (remaining <= 0) continue;

                hasPassengers = true;
                PassengerTableWidget passengerWidget = new PassengerTableWidget(passenger);

                ExpandableListWidget passengerList = new ExpandableListWidget(passengerWidget.passengerClass() + " (x" + remaining + ")", skin);
                passengerList.setPreferredWidth(CONTENT_WIDTH);
                activeLists.add(passengerList);

                SelectButton selectButton = createSelectButton(passenger);
                passengerList.addHeaderActor(selectButton, 160f, 55f);

                Table passengerContent = passengerList.getContentTable();
                addPassengerRow(passengerContent, "To", passengerWidget.cityTo());
                addPassengerRow(passengerContent, "Persons", passengerWidget.persons());
                addPassengerRow(passengerContent, "Reward", passengerWidget.reward());

                passengerList.setCallbacks(
                    () -> {
                        for (ExpandableListWidget other : activeLists) {
                            if (other != passengerList) other.collapse();
                        }
                    },
                    () -> {
                        invalidateHierarchy();
                        contentTable.layout();
                        scrollPane.layout();
                    }
                );

                contentTable.add(passengerList).width(CONTENT_WIDTH).fillX().expandX().padTop(8).row();
            }

            if (!hasPassengers) {
                contentTable.add(new SingleLineText("No passengers waiting here", skin)).left().pad(15).row();
            }

            add(scrollPane).width(CONTENT_WIDTH).height(CONTENT_HEIGHT).padTop(10).row();
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
        setWidth(Math.max(getWidth(), 820)); // Выравниваем ширину с GroupHUD
        float x = screenWidth - getWidth() - 20;
        float y = currentTopY - getHeight() - 14;
        x = Math.max(20, x);
        y = Math.max(20, y);
        setPosition(x, y);
    }

    private SelectButton createSelectButton(PassengerType passenger) {
        SelectButton button = new SelectButton("Select", skin, () -> {
            if (onPassengerSelected != null) onPassengerSelected.accept(passenger);
        });

        boolean canAdd = currentRoute != null && currentRoute.checkPassengerAdding(passenger) == null;
        button.setDisabled(!canAdd);
        button.getLabel().setColor(canAdd ? Color.WHITE : Color.DARK_GRAY);
        return button;
    }

    private void addPassengerRow(Table table, String label, String value) {
        SingleLineText row = new SingleLineText(label + ": " + value, skin);
        row.setColor(Color.LIGHT_GRAY);
        table.add(row).left().padBottom(4).row();
    }
}
