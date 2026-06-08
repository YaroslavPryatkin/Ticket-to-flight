package com.game.Ticket_To_Flight.frontend.components.details;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;
import com.game.Ticket_To_Flight.frontend.components.texts.SingleLineText;
import com.game.Ticket_To_Flight.frontend.components.texts.WrappedText;

public final class AirportDetailsWidget {
    private static final float LEFT_PADDING = 0f;

    private AirportDetailsWidget() {}

    public static void fill(Table table, Skin skin, Airport airport) {
        fill(table, skin, airport, 0f);
    }

    public static void fill(Table table, Skin skin, Airport airport, float wrapWidth) {
        if (airport == null || airport.type == null) return;

        addRow(table, skin, "Description", airport.type.description, wrapWidth);
        addRow(
            table,
            skin,
            "Available gates",
            airport.getFreeGates() + "/" + airport.type.gateAmount,
            wrapWidth
        );
    }

    public static void addCityHeader(Table table, Skin skin, Airport airport, float wrapWidth) {
        if (airport == null) return;

        String str = airport.getAirportName() + " : " + airport.type.getCityType();

        Label cityLabel = wrapWidth > 0
            ? new WrappedText(str, skin, wrapWidth)
            : new SingleLineText(str, skin);
        cityLabel.setColor(Color.CYAN);

        if (wrapWidth > 0) {
            table.add(cityLabel).width(wrapWidth).left().row();
        } else {
            table.add(cityLabel).left().row();
        }
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
}
