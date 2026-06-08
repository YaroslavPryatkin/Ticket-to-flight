package com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.HUD.HUDDirectory.FlightHUDDirectory;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PlaneType;
import com.game.Ticket_To_Flight.commonFrontAndBack.Route;
import com.game.Ticket_To_Flight.frontend.components.details.PlaneDetailsWidget;
import com.game.Ticket_To_Flight.frontend.components.tables.flight.AbstractFlightPanel;
import com.game.Ticket_To_Flight.frontend.components.texts.SingleLineText;

public class PlaneHUD extends AbstractFlightPanel {
    private PlaneType currentPlane;
    private float currentTopY = 0;
    private Route route;

    public PlaneHUD(Skin skin) {
        super(skin);
    }

    public void updateData(PlaneType currentPlane, Route route) {
        this.currentPlane = currentPlane;
        this.route = route;
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
                PlaneDetailsWidget.fill(
                    content,
                    skin,
                    currentPlane,
                    route,
                    PlaneDetailsWidget.Align.RIGHT
                );
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
}
