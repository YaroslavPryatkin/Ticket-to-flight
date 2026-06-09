package com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.Tooltips;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airline;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.AirlineType;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PlaneType;
import com.game.Ticket_To_Flight.frontend.components.texts.WrappedText;
import com.game.Ticket_To_Flight.frontend.components.windows.LeftDownCornerTooltip;

public class PlaneLeftDownCornerTooltip  extends LeftDownCornerTooltip {
    private static final float CONTENT_WIDTH = 590f;
    private static final float CONTENT_MAX_HEIGHT = 430f;

    public PlaneLeftDownCornerTooltip(Skin skin, PlaneType plane)  {
        super("Plane", skin);

        Table content = new Table();
        content.top().left();
        content.defaults().left().padBottom(8);

        Label title = new WrappedText(plane.description, skin, CONTENT_WIDTH);
        title.setColor(Color.CYAN);
        content.add(title).width(CONTENT_WIDTH).left().row();

        addRow(content, skin, "Price", "$" + plane.price);
        addRow(content, skin, "Luxury", String.valueOf(plane.luxury));
        addRow(content, skin, "Stations", String.valueOf(plane.stations));
        addRow(content, skin, "Fuel", String.valueOf(plane.fuel));
        addRow(content, skin, "Gate Range", formatInterval(plane.gateRange.getFrom(), plane.gateRange.getTo()));
        addRow(content, skin, "Dist Range", formatInterval(plane.distRange.getFrom(), plane.distRange.getTo()));


        add(content).width(CONTENT_WIDTH).maxHeight(CONTENT_MAX_HEIGHT);
        pack();
    }

    private void addRow(Table table, Skin skin, String label, String value) {
        Label row = new WrappedText(label + ": " + value, skin, CONTENT_WIDTH);
        row.setColor(Color.LIGHT_GRAY);

        table.add(row).width(CONTENT_WIDTH).left().padBottom(4).row();
    }

    private <T> String formatInterval(T from, T to) {
        String left = from == null ? "-inf" : from.toString();
        String right = to == null ? "+inf" : to.toString();
        return "[" + left + ", " + right + "]";
    }
}
