package com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.Tooltips;

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.game.Ticket_To_Flight.Utilities.MapHolder;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PassengerType;
import com.game.Ticket_To_Flight.frontend.components.details.AirportDetailsWidget;
import com.game.Ticket_To_Flight.frontend.components.details.PassengerDetailsWidget;
import com.game.Ticket_To_Flight.frontend.components.tables.expandable.ExpandableListWidget;
import com.game.Ticket_To_Flight.frontend.components.tables.passenger.PassengerTableWidget;
import com.game.Ticket_To_Flight.frontend.components.texts.WrappedText;
import com.game.Ticket_To_Flight.frontend.components.windows.LeftDownCornerTooltip;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AirportLeftDownCornerTooltip extends LeftDownCornerTooltip {
    private static final float CONTENT_WIDTH = 590f;
    private static final float CONTENT_MAX_HEIGHT = 430f;

    public AirportLeftDownCornerTooltip(Skin skin, Airport airport) {
        super("Airport", skin);

        Table content = new Table();
        content.top().left();
        content.defaults().left().padBottom(8);

        AirportDetailsWidget.addCityHeader(content, skin, airport, CONTENT_WIDTH);
        AirportDetailsWidget.fill(content, skin, airport, CONTENT_WIDTH);

        ScrollPane scrollPane = new ScrollPane(content, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);

        fillPassengers(content, skin, airport, scrollPane);

        add(scrollPane).width(CONTENT_WIDTH).maxHeight(CONTENT_MAX_HEIGHT).row();
        registerScrollFocus(scrollPane);
        pack();
    }

    private void fillPassengers(Table table, Skin skin, Airport airport, ScrollPane scrollPane) {
        Iterator<Map.Entry<PassengerType, Integer>> iterator = MapHolder.viewAsEntrySet(airport.getGuests());
        List<ExpandableListWidget> activeLists = new ArrayList<>();
        boolean hasPassengers = false;

        float passengerListWidth = CONTENT_WIDTH - 30f;

        while (iterator.hasNext()) {
            Map.Entry<PassengerType, Integer> entry = iterator.next();
            if (entry == null || entry.getValue() <= 0) continue;

            hasPassengers = true;
            PassengerType passenger = entry.getKey();
            PassengerTableWidget passengerWidget = new PassengerTableWidget(passenger);
            ExpandableListWidget passengerList = new ExpandableListWidget(passengerWidget.passengerClass() + " (x" + entry.getValue() + ")", skin, passenger);
            passengerList.setPreferredWidth(passengerListWidth);
            activeLists.add(passengerList);

            Table passengerContent = passengerList.getContentTable();
            PassengerDetailsWidget.fill(passengerContent, skin, passenger, passengerListWidth);

            addExtraPassengerUI(passengerContent, passenger, skin);

            passengerList.setCallbacks(
                () -> {
                    for (ExpandableListWidget other : activeLists) {
                        if (other != passengerList) other.collapse();
                    }
                },
                () -> {
                    invalidateHierarchy();
                    scrollPane.layout();
                }
            );

            table.add(passengerList).width(passengerListWidth).fillX().expandX().padTop(8).row();
        }

        if (!hasPassengers) {
            table.add(new WrappedText("No passengers", skin, passengerListWidth)).width(passengerListWidth).left().padLeft(15).row();
        }
    }

    protected void addExtraPassengerUI(Table passengerContent, PassengerType passenger, Skin skin) {}
}
