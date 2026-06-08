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
import com.game.Ticket_To_Flight.frontend.components.details.AirportDetailsWidget;
import com.game.Ticket_To_Flight.frontend.components.details.PassengerDetailsWidget;
import com.game.Ticket_To_Flight.frontend.components.buttons.SelectButton;
import com.game.Ticket_To_Flight.frontend.components.tables.expandable.ExpandableListWidget;
import com.game.Ticket_To_Flight.frontend.components.tables.passenger.PassengerTableWidget;
import com.game.Ticket_To_Flight.frontend.components.tables.flight.AbstractFlightPanel;
import com.game.Ticket_To_Flight.frontend.components.texts.SingleLineText;

import java.util.ArrayList;
import java.util.HashMap;
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
    private MapHolder<PassengerType, Integer> currentGroups;
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

    public void updateData(Route route) {
        if(route!=null) {
            this.currentGroups = route.getSuitablePassengers();
            this.currentAirport = route.getCurrentAirport();
            this.currentRoute = route;
        }
        else{
            this.currentGroups = null;
            this.currentAirport = null;
            this.currentRoute = null;
        }

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

            List<ExpandableListWidget> activeLists = new ArrayList<>();
            Map<ExpandableListWidget, PassengerType> passengerByList = new HashMap<>();
            Table detailsTable = new Table();
            detailsTable.top().left();

            Iterator<Map.Entry<PassengerType, Integer>> iterator = MapHolder.viewAsEntrySet(currentGroups);
            Map.Entry<PassengerType, Integer> e;
            boolean hasPassengers = false;
            while ((e = iterator.next()) != null) {
                hasPassengers = true;
                PassengerType type = e.getKey();
                Integer amount = e.getValue();

                PassengerTableWidget passengerWidget = new PassengerTableWidget(type);

                ExpandableListWidget passengerList = new ExpandableListWidget(
                    passengerWidget.passengerClass() + " (x" + amount + ")", skin);
                passengerList.setPreferredWidth(CONTENT_WIDTH);
                activeLists.add(passengerList);
                passengerByList.put(passengerList, type);

                SelectButton selectButton = createSelectButton(type);
                passengerList.addHeaderActor(selectButton, 160f, 55f);

                passengerList.setCallbacks(
                    () -> {
                        for (ExpandableListWidget other : activeLists) {
                            if (other != passengerList) other.collapse();
                        }
                        refreshPassengerDetails(detailsTable, activeLists, passengerByList);
                    },
                    () -> {
                        refreshPassengerDetails(detailsTable, activeLists, passengerByList);
                        invalidateHierarchy();
                        contentTable.layout();
                        scrollPane.layout();
                    }
                );
                contentTable.add(passengerList).width(CONTENT_WIDTH).fillX().expandX().padTop(8).row();
            }

            if (!hasPassengers) {
                contentTable.add(new SingleLineText("No passenger groups here.", skin)).left().pad(15).row();
            } else {
                contentTable.add(detailsTable).width(CONTENT_WIDTH).fillX().padTop(12).row();
            }

            add(scrollPane).width(CONTENT_WIDTH).height(CONTENT_HEIGHT).padTop(10).row();
        }

        if (screenWidth > 0 && currentTopY > 0) recalculatePosition();
    }

    private void refreshPassengerDetails(
        Table detailsTable,
        List<ExpandableListWidget> activeLists,
        Map<ExpandableListWidget, PassengerType> passengerByList
    ) {
        detailsTable.clearChildren();

        for (ExpandableListWidget listWidget : activeLists) {
            if (listWidget.isExpanded()) {
                PassengerType type = passengerByList.get(listWidget);
                if (type != null) {
                    PassengerDetailsWidget.fill(detailsTable, skin, type);
                }
                return;
            }
        }
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

        boolean canAdd = currentRoute != null && currentRoute.checkPassengerAdding(passenger, 1) == null;
        button.setDisabled(!canAdd);
        button.getLabel().setColor(canAdd ? Color.WHITE : Color.DARK_GRAY);
        return button;
    }
}
