package com.game.Ticket_To_Flight.frontend.components.details;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PlaneType;
import com.game.Ticket_To_Flight.commonFrontAndBack.Route;
import com.game.Ticket_To_Flight.frontend.components.texts.SingleLineText;

public final class PlaneDetailsWidget {
    private static final float LEFT_PADDING = 15f;

    public enum Align {
        LEFT, RIGHT
    }

    private PlaneDetailsWidget() {}

    public static void fill(Table table, Skin skin, PlaneType plane) {
        fill(table, skin, plane, null, Align.LEFT, false);
    }

    public static void fill(Table table, Skin skin, PlaneType plane, Route route, Align align) {
        fill(table, skin, plane, route, align, true);
    }

    public static void fill(
        Table table,
        Skin skin,
        PlaneType plane,
        Route route,
        Align align,
        boolean includeExtendedInfo
    ) {
        if (plane == null) return;

        if (route == null) {
            addRow(table, skin, align, "Fuel", String.valueOf(plane.fuel));
            addRow(table, skin, align, "Stations", String.valueOf(plane.stations));
            addRow(table, skin, align, "Luxury", String.valueOf(plane.luxury));
            addRow(table, skin, align, "Capacity", String.valueOf(plane.capacity));
        } else {
            addRow(table, skin, align, "Fuel", route.getRemainingFuel() + "/" + plane.fuel);
            addRow(table, skin, align, "Stations", route.getRemainingStations() + "/" + plane.stations);
            addRow(table, skin, align, "Luxury", String.valueOf(plane.luxury));
            addRow(table, skin, align, "Capacity", route.getRemainingCapacity() + "/" + plane.capacity);
        }

        addRow(table, skin, align, "Gate Range", formatInterval(plane.gateRange.getFrom(), plane.gateRange.getTo()));
        addRow(table, skin, align, "Dist Range", formatInterval(plane.distRange.getFrom(), plane.distRange.getTo()));

        if (includeExtendedInfo) {
            addRow(table, skin, align, "Price", "$" + plane.price);
            addRow(table, skin, align, "Description", plane.description);
        }
    }

    private static void addRow(Table table, Skin skin, Align align, String label, String value) {
        Label row = new SingleLineText(label + ": " + value, skin);
        row.setColor(Color.LIGHT_GRAY);

        if (align == Align.RIGHT) {
            table.add(row).right().padBottom(8).row();
        } else {
            table.add(row).left().padLeft(LEFT_PADDING).padBottom(3).row();
        }
    }

    private static <T> String formatInterval(T from, T to) {
        String left = from == null ? "-inf" : from.toString();
        String right = to == null ? "+inf" : to.toString();
        return "[" + left + ", " + right + "]";
    }
}
