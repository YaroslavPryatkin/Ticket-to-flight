package com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.HUD.HUDDirectory.FlightHUDDirectory;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PlaneType;
import com.game.Ticket_To_Flight.frontend.components.buttons.RoundedButton;
import com.game.Ticket_To_Flight.frontend.components.texts.SingleLineText;

public class PlaneHUD extends Table {
    private final Skin skin;

    private float screenWidth = 0;
    private float screenHeight = 0;

    private boolean isCollapsed = false;
    private PlaneType currentPlane;
    private Runnable onToggle;

    private boolean isInitialized = false;

    public PlaneHUD(Skin skin) {
        this.skin = skin;
        top().right();
        pad(20);
        setBackground(skin.getDrawable("flight-panel-bg"));
    }

    public void setOnToggle(Runnable onToggle) {
        this.onToggle = onToggle;
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
        clearChildren();

        Table header = new Table();
        header.add(new SingleLineText("Plane Info", skin)).expandX().left();

        TextButton toggleBtn = new RoundedButton(isCollapsed ? "Expand" : "Collapse", skin);
        toggleBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                isCollapsed = !isCollapsed;
                renderContent();
                if (onToggle != null) onToggle.run();
            }
        });
        header.add(toggleBtn).right().width(200).height(70);
        add(header).fillX().expandX().row();

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
