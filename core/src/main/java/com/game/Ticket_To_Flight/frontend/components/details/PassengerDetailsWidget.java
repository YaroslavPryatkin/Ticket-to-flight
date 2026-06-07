package com.game.Ticket_To_Flight.frontend.components.details;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PassengerType;
import com.game.Ticket_To_Flight.frontend.components.tables.passenger.PassengerTableWidget;
import com.game.Ticket_To_Flight.frontend.components.texts.SingleLineText;
import com.game.Ticket_To_Flight.frontend.components.texts.WrappedText;

public final class PassengerDetailsWidget {
    private static final float LEFT_PADDING = 0f;

    private PassengerDetailsWidget() {}

    public static void fill(Table table, Skin skin, PassengerType passenger) {
        fill(table, skin, passenger, 0f);
    }

    public static void fill(Table table, Skin skin, PassengerType passenger, float wrapWidth) {
        if (passenger == null) return;

        PassengerTableWidget passengerWidget = new PassengerTableWidget(passenger);

        addRow(table, skin, "To", passengerWidget.cityTo(), wrapWidth);
        addRow(table, skin, "Persons", passengerWidget.persons(), wrapWidth);
        addRow(table, skin, "Reward", passengerWidget.reward(), wrapWidth);
        addRow(table, skin, "Solvency", String.valueOf(passenger.solvency), wrapWidth);
        addRow(table, skin, "Luxury range", formatInterval(passenger.luxuryRange.getFrom(), passenger.luxuryRange.getTo()), wrapWidth);
        addRow(table, skin, "Yield range", formatInterval(passenger.yieldRange.getFrom(), passenger.yieldRange.getTo()), wrapWidth);
        addRow(table, skin, "Capacity range", formatInterval(passenger.capacityRange.getFrom(), passenger.capacityRange.getTo()), wrapWidth);
        addRow(table, skin, "Stations range", formatInterval(passenger.stationsRange.getFrom(), passenger.stationsRange.getTo()), wrapWidth);
        addRow(table, skin, "Description", passenger.description, wrapWidth);
    }

    private static void addRow(Table table, Skin skin, String label, String value, float wrapWidth) {
        Label row = wrapWidth > 0
            ? new WrappedText(label + ": " + value, skin, wrapWidth)
            : new SingleLineText(label + ": " + value, skin);
        row.setColor(Color.LIGHT_GRAY);

        if (wrapWidth > 0) {
            table.add(row).width(wrapWidth).left().padBottom(4).row();
        } else {
            table.add(row).left().padLeft(LEFT_PADDING).padBottom(4).row();
        }
    }

    private static <T> String formatInterval(T from, T to) {
        String left = from == null ? "-inf" : from.toString();
        String right = to == null ? "+inf" : to.toString();
        return "[" + left + ", " + right + "]";
    }
}
