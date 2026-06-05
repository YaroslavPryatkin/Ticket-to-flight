package com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.HUD.HUDDirectory.FlightHUDDirectory;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PlaneType;
import com.game.Ticket_To_Flight.frontend.components.ExpandableListWidget;
import com.game.Ticket_To_Flight.frontend.components.texts.SingleLineText;

public class PlaneHUD extends Table {
    private final Skin skin;

    private float screenWidth = 0;
    private float screenHeight = 0;

    private PlaneType currentPlane;
    private Runnable onToggle;
    private boolean isInitialized = false;

    private final ExpandableListWidget expandList;

    public PlaneHUD(Skin skin) {
        this.skin = skin;
        top().right();
        pad(20);
        setBackground(skin.getDrawable("flight-panel-bg"));

        expandList = new ExpandableListWidget("Plane Info", skin);
        add(expandList).fillX().expandX().row();

        expandList.expand();
    }

    public void setOnToggle(Runnable onToggle) {
        this.onToggle = onToggle;

        expandList.setCallbacks(null, () -> {
            if (screenWidth > 0 && screenHeight > 0) recalculatePosition();
            if (this.onToggle != null) this.onToggle.run();
        });
    }

    public void updateData(PlaneType plane) {
        if (this.currentPlane == plane && isInitialized) {
            return;
        }

        this.currentPlane = plane;
        this.isInitialized = true;
        renderContent();
    }

    private void renderContent() {
        Table content = expandList.getContentTable();
        content.clearChildren();

        if (currentPlane == null) {
            content.add(new SingleLineText("No plane selected", skin)).right().row();
        } else {
            addRow(content, "fuel", String.valueOf(currentPlane.fuel));
            addRow(content, "stations", String.valueOf(currentPlane.stations));
            addRow(content, "luxury", String.valueOf(currentPlane.luxury));
            addRow(content, "capacity", String.valueOf(currentPlane.capacity));
            addRow(content, "gateRange", formatInterval(currentPlane.gateRange.getFrom(), currentPlane.gateRange.getTo()));
            addRow(content, "distRange", formatInterval(currentPlane.distRange.getFrom(), currentPlane.distRange.getTo()));
            addRow(content, "price", "$" + currentPlane.price);
            addRow(content, "description", currentPlane.description);
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

    private void addRow(Table table, String label, String value) {
        table.add(new SingleLineText(label + ": " + value, skin)).right().padBottom(8).row();
    }

    private <T> String formatInterval(T from, T to) {
        String left = from == null ? "-inf" : from.toString();
        String right = to == null ? "+inf" : to.toString();
        return "[" + left + ", " + right + "]";
    }
}
