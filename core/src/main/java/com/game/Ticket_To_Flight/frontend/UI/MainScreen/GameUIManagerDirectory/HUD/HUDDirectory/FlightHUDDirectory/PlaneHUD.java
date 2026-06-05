package com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.HUD.HUDDirectory.FlightHUDDirectory;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PlaneType;
import com.game.Ticket_To_Flight.frontend.components.tables.flight.AbstractFlightPanel;
import com.game.Ticket_To_Flight.frontend.components.texts.SingleLineText;

public class PlaneHUD extends AbstractFlightPanel {
    private PlaneType currentPlane;

    public PlaneHUD(Skin skin) {
        super(skin);
    }

    public void updateData(PlaneType plane) {
        if (this.currentPlane == plane && isInitialized) return;

        this.currentPlane = plane;
        this.isInitialized = true;
        renderContent();
    }

    @Override
    protected void renderContent() {
        clearChildren();

        // Используем метод родителя для создания шапки
        add(buildHeader("Plane Info")).fillX().expandX().row();

        if (!isCollapsed) {
            Table content = new Table();
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
            add(content).right().padTop(10).row();
        }

        if (screenWidth > 0 && screenHeight > 0) recalculatePosition();
    }

    public void layoutFor(float width, float height) {
        this.screenWidth = width;
        this.screenHeight = height;
        recalculatePosition();
    }

    @Override
    protected void recalculatePosition() {
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
