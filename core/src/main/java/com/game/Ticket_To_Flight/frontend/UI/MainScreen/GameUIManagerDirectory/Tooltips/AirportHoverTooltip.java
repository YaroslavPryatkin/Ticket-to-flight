package com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.Tooltips;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.game.Ticket_To_Flight.Utilities.MapHolder;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.AirportType;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PassengerType;
import com.game.Ticket_To_Flight.frontend.components.tables.expandable.ExpandableListWidget;
import com.game.Ticket_To_Flight.frontend.components.tables.passenger.PassengerTableWidget;
import com.game.Ticket_To_Flight.frontend.components.subsidiary.ComponentHover;
import com.game.Ticket_To_Flight.frontend.components.texts.SingleLineText;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AirportHoverTooltip extends Window {
    private static final float CONTENT_WIDTH = 650f;
    private static final float CONTENT_MAX_HEIGHT = 430f;
    private final ScrollPane scrollPane;

    public AirportHoverTooltip(Skin skin, Airport airport) {
        super("Airport", skin);
        top().left();
        setMovable(false);
        getColor().a = 0.85f;
        pad(30);
        padTop(70);

        Table content = new Table();
        content.top().left();
        content.defaults().left().padBottom(8);

        Label cityLabel = new SingleLineText(airport.getCityName(), skin);
        cityLabel.setColor(Color.CYAN);
        content.add(cityLabel).left().row();

        AirportType type = airport.type;
        addRow(content, skin, "City type", type.getCityType());
        addRow(content, skin, "Description", type.description);
        addRow(content, skin, "Cost", "$" + type.cost);
        addRow(content, skin, "Available gates", String.valueOf(airport.getFreeGates()) + "/" + String.valueOf(type.gateAmount));

        scrollPane = new ScrollPane(content, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);

        fillPassengers(content, skin, airport);

        add(scrollPane).width(CONTENT_WIDTH).maxHeight(CONTENT_MAX_HEIGHT).row();
        pack();
    }

    private void fillPassengers(Table table, Skin skin, Airport airport) {
        Iterator<Map.Entry<PassengerType, Integer>> iterator = MapHolder.viewAsEntrySet(airport.getGuests());
        List<ExpandableListWidget> activeLists = new ArrayList<>();
        boolean hasPassengers = false;

        while (iterator.hasNext()) {
            Map.Entry<PassengerType, Integer> entry = iterator.next();
            if (entry == null || entry.getValue() <= 0) continue;

            hasPassengers = true;
            PassengerType passenger = entry.getKey();
            PassengerTableWidget passengerWidget = new PassengerTableWidget(passenger);
            ExpandableListWidget passengerList = new ExpandableListWidget(passengerWidget.passengerClass() + " (x" + entry.getValue() + ")", skin);
            passengerList.setPreferredWidth(CONTENT_WIDTH - 30f);
            activeLists.add(passengerList);

            Table passengerContent = passengerList.getContentTable();
            addRow(passengerContent, skin, "To", passengerWidget.cityTo());
            addRow(passengerContent, skin, "Persons", passengerWidget.persons());
            addRow(passengerContent, skin, "Reward", passengerWidget.reward());

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

            table.add(passengerList).width(CONTENT_WIDTH - 30f).fillX().expandX().padTop(8).row();
        }

        if (!hasPassengers) {
            table.add(new SingleLineText("No passengers", skin)).left().padLeft(15).row();
        }
    }

    private void addRow(Table table, Skin skin, String label, String value) {
        Label row = new SingleLineText(label + ": " + value, skin);
        row.setColor(Color.LIGHT_GRAY);
        table.add(row).left().padBottom(4).row();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (getStage() == null) return;

        if (ComponentHover.isMouseOver(this)) {
            getStage().setScrollFocus(scrollPane);
        } else if (getStage().getScrollFocus() == scrollPane) {
            getStage().setScrollFocus(null);
        }
    }

    protected void addExtraPassengerUI(Table passengerContent, PassengerType passenger, Skin skin) {}
}
