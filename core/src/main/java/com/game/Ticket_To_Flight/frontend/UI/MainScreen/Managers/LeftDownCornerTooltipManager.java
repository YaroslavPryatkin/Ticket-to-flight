package com.game.Ticket_To_Flight.frontend.UI.MainScreen.Managers;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airline;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.Tooltips.AirlineLeftDownCornerTooltip;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.Tooltips.AirportLeftDownCornerTooltip;
import com.game.Ticket_To_Flight.frontend.components.windows.LeftDownCornerTooltip;
import com.game.Ticket_To_Flight.frontend.components.subsidiary.ComponentHover;

public class LeftDownCornerTooltipManager {
    private final Stage uiStageHUD;
    private final Skin skin;


    private LeftDownCornerTooltip leftDownCornerTooltip;

    public LeftDownCornerTooltipManager(Stage uiStageHUD, Skin skin) {
        this.uiStageHUD = uiStageHUD;
        this.skin = skin;
    }

    public void showAirportTooltip(Airport airport) {
        removeTooltip();
        leftDownCornerTooltip = new AirportLeftDownCornerTooltip(skin, airport);
        uiStageHUD.addActor(leftDownCornerTooltip);
        updatePosition();
    }

    public void showAirlineTooltip(Airline airline) {
        removeTooltip();
        leftDownCornerTooltip = new AirlineLeftDownCornerTooltip(skin, airline);
        uiStageHUD.addActor(leftDownCornerTooltip);
        updatePosition();
    }


    public void removeTooltip() {
        if (leftDownCornerTooltip != null) {
            leftDownCornerTooltip.remove();
            leftDownCornerTooltip = null;
        }
    }

    private void updatePosition() {
        if (leftDownCornerTooltip == null) return;

        leftDownCornerTooltip.pack();

        float paddingLeft = 20f;
        float paddingBottom = 20f;
        float paddingTop = 20f;
        float maxHeight = uiStageHUD.getHeight() - paddingBottom - paddingTop;

        if (leftDownCornerTooltip.getHeight() > maxHeight) {
            leftDownCornerTooltip.setHeight(maxHeight);
        }

        leftDownCornerTooltip.setPosition(paddingLeft, paddingBottom);
    }

    public boolean isPointerOverTooltip(){
        return leftDownCornerTooltip != null &&
            leftDownCornerTooltip.getStage() != null &&
            ComponentHover.isMouseOver(leftDownCornerTooltip);
    }

    private void clearTooltipScrollFocusWhenPointerLeaves(){
        Actor scrollFocus = uiStageHUD.getScrollFocus();
        if (scrollFocus == null || isPointerOverTooltip()) return;

        if (scrollFocus != null && leftDownCornerTooltip != null &&
            (scrollFocus == leftDownCornerTooltip || leftDownCornerTooltip.isAscendantOf(scrollFocus))) {
            uiStageHUD.setScrollFocus(null);
        }
    }

    public void update(){
        updatePosition();
        clearTooltipScrollFocusWhenPointerLeaves();
    }
}
