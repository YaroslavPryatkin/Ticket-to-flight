package com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.HUD.HUDDirectory.FlightHUDDirectory;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PlaneType;
import com.game.Ticket_To_Flight.frontend.components.texts.SingleLineText;

public class PlaneHUD extends Table {
    private final Skin skin;

    private float screenWidth = 0;
    private float screenHeight = 0;

    public PlaneHUD(Skin skin) {
        this.skin = skin;
        top().right();
        pad(20);
        setBackground(skin.getDrawable("flight-panel-bg"));
    }

    public void updateData(PlaneType plane) {
        clearChildren();

        if (plane == null) {
            add(new SingleLineText("No plane selected", skin)).right().row();
        } else {
            addRow("fuel", String.valueOf(plane.fuel));
            addRow("stations", String.valueOf(plane.stations));
            addRow("luxury", String.valueOf(plane.luxury));
            addRow("capacity", String.valueOf(plane.capacity));
            addRow("gateRange", formatInterval(plane.gateRange.getFrom(), plane.gateRange.getTo()));
            addRow("distRange", formatInterval(plane.distRange.getFrom(), plane.distRange.getTo()));
            addRow("price", "$" + plane.price);
            addRow("description", plane.description);
        }

        if (screenWidth > 0 && screenHeight > 0) {
            recalculatePosition();
        }
    }

    public void layoutFor(float width, float height) {
        this.screenWidth = width;
        this.screenHeight = height;
        recalculatePosition();
    }

    private void recalculatePosition() {
        pack();
        setWidth(Math.max(getWidth(), 520));

        setPosition(screenWidth - getWidth() - 20, screenHeight - getHeight() - 360);
    }

    private void addRow(String label, String value) {
        add(new SingleLineText(label + ": " + value, skin)).right().padBottom(8).row();
    }

    private <T> String formatInterval(T from, T to) {
        String left = from == null ? "-inf" : from.toString();
        String right = to == null ? "+inf" : to.toString();
        return "[" + left + ", " + right + "]";
    }
}
