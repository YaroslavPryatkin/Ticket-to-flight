package com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.Tooltips;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airline;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.AirlineType;
import com.game.Ticket_To_Flight.frontend.components.texts.SingleLineText;

public class AirlineHoverTooltip extends Window {
    public AirlineHoverTooltip(Skin skin, Airline airline) {
        super("Airline", skin);
        top().left();
        setMovable(false);
        getColor().a = 0.85f;
        pad(30);
        padTop(70);

        Table content = new Table();
        content.top().left();
        content.defaults().left().padBottom(8);

        Label title = new SingleLineText(routeTitle(airline), skin);
        title.setColor(Color.CYAN);
        content.add(title).left().row();

        AirlineType type = airline.type;
        addRow(content, skin, "Description", type.description);
        addRow(content, skin, "Price", "$" + type.price);
        addRow(content, skin, "Yield", String.valueOf(type.yield));
        addRow(content, skin, "Gate A", String.valueOf(type.gateA));
        addRow(content, skin, "Gate B", String.valueOf(type.gateB));
        addRow(content, skin, "Luxury range", formatInterval(type.luxuryRange.getFrom(), type.luxuryRange.getTo()));
        addRow(content, skin, "Capacity range", formatInterval(type.capacityRange.getFrom(), type.capacityRange.getTo()));

        if (airline.getPlayer() != null) {
            addRow(content, skin, "Owned by", airline.getPlayer().getName());
        }

        add(content).width(650f);
        pack();
    }

    private String routeTitle(Airline airline) {
        return airline.getPortA().getCityName() + " -> " + airline.getPortB().getCityName();
    }

    private void addRow(Table table, Skin skin, String label, String value) {
        Label row = new SingleLineText(label + ": " + value, skin);
        row.setColor(Color.LIGHT_GRAY);
        table.add(row).left().padBottom(4).row();
    }

    private <T> String formatInterval(T from, T to) {
        String left = from == null ? "-inf" : from.toString();
        String right = to == null ? "+inf" : to.toString();
        return "[" + left + ", " + right + "]";
    }
}
