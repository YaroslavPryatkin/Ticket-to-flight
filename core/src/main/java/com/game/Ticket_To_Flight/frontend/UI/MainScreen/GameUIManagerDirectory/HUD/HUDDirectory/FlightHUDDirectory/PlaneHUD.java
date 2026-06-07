package com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.HUD.HUDDirectory.FlightHUDDirectory;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PlaneType;
import com.game.Ticket_To_Flight.commonFrontAndBack.Route;
import com.game.Ticket_To_Flight.frontend.components.tables.flight.AbstractFlightPanel;
import com.game.Ticket_To_Flight.frontend.components.texts.SingleLineText;

public class PlaneHUD extends AbstractFlightPanel {
    private PlaneType currentPlane;
    private float currentTopY = 0;
    private Route route;

    private double lastFuel = -1;
    private int lastStations = -1;
    private int lastCapacity = -1;

    public PlaneHUD(Skin skin) {
        super(skin);
    }

    public void updateData(PlaneType plane, Route route) {
        double currentFuel = route != null ? route.getRemainingFuel() : -1;
        int currentStations = route != null ? route.getRemainingStations() : -1;
        int currentCapacity = route != null ? route.getRemainingCapacity() : -1;

        if (this.currentPlane == plane &&
            this.route == route &&
            this.lastFuel == currentFuel &&
            this.lastStations == currentStations &&
            this.lastCapacity == currentCapacity &&
            this.isInitialized) {
            return;
        }

        this.currentPlane = plane;
        this.route = route;
        this.lastFuel = currentFuel;
        this.lastStations = currentStations;
        this.lastCapacity = currentCapacity;
        this.isInitialized = true;

        renderContent();
    }

    @Override
    protected void renderContent() {
        clearChildren();

        add(buildHeader("Plane Info")).fillX().expandX().row();

        if (!isCollapsed) {
            Table content = new Table();
            if (currentPlane == null) {
                content.add(new SingleLineText("No plane selected", skin)).right().row();
            } else {
                if(route == null) {
                    addRow(content, "fuel", String.valueOf(currentPlane.fuel));
                    addRow(content, "stations", String.valueOf(currentPlane.stations));
                    addRow(content, "luxury", String.valueOf(currentPlane.luxury));
                    addRow(content, "capacity", String.valueOf(currentPlane.capacity));
                    addRow(content, "gateRange", formatInterval(currentPlane.gateRange.getFrom(), currentPlane.gateRange.getTo()));
                    addRow(content, "distRange", formatInterval(currentPlane.distRange.getFrom(), currentPlane.distRange.getTo()));
                    addRow(content, "price", "$" + currentPlane.price);
                    addRow(content, "description", currentPlane.description);
                }
                else{
                    addRow(content, "fuel", route.getRemainingFuel() + "/" + currentPlane.fuel );
                    addRow(content, "stations", route.getRemainingStations() + "/" + currentPlane.stations);
                    addRow(content, "luxury", String.valueOf(currentPlane.luxury));
                    addRow(content, "capacity", route.getRemainingCapacity() + "/" + currentPlane.capacity);
                    addRow(content, "gateRange", formatInterval(currentPlane.gateRange.getFrom(), currentPlane.gateRange.getTo()));
                    addRow(content, "distRange", formatInterval(currentPlane.distRange.getFrom(), currentPlane.distRange.getTo()));
                    addRow(content, "price", "$" + currentPlane.price);
                    addRow(content, "description", currentPlane.description);
                }
            }
            add(content).right().padTop(10).row();
        }

        if (screenWidth > 0 && screenHeight > 0) recalculatePosition();
    }

    public void layoutFor(float width, float height, float topY) {
        this.screenWidth = width;
        this.screenHeight = height;
        this.currentTopY = topY;
        recalculatePosition();
    }

    @Override
    protected void recalculatePosition() {
        pack();
        setWidth(Math.max(getWidth(), 520));
        setPosition(screenWidth - getWidth() - 20, currentTopY - getHeight() - 14);
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
