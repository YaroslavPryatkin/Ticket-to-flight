package com.game.Ticket_To_Flight.frontend.UI.MainScreen.MapInput;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airline;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.Tooltips.ContextMenuWithButtons;
import com.game.Ticket_To_Flight.frontend.components.subsidiary.ComponentHover;

public class ContextMenuPositionManager {
    public static void updateTooltipPosition(
        ContextMenuWithButtons currentTooltip, Airport airport,
        Stage uiStageHUD, OrthographicCamera camera) {
        if (currentTooltip == null) return;

        var window = currentTooltip.asWindow();

        window.pack();

        Float worldX = null;
        Float worldY = null;
        if(airport!=null) {
            worldX = airport.getX();
            worldY = airport.getY();
        }
        setCordsFromWorld(window, worldX, worldY, uiStageHUD, camera);
    }



    public static void updateTooltipPosition(
        ContextMenuWithButtons currentTooltip, Airline airline,
        Stage uiStageHUD, OrthographicCamera camera) {
        if (currentTooltip == null) return;

        var window = currentTooltip.asWindow();

        window.pack();
        Float worldX = null;
        Float worldY = null;
        if(airline!=null) {
            worldX = (airline.getPortA().getX() + airline.getPortB().getX()) / 2f;
            worldY = (airline.getPortA().getY() + airline.getPortB().getY()) / 2f;
        }
        setCordsFromWorld(window, worldX, worldY, uiStageHUD, camera);
    }

    private static void setCordsFromWorld(Window window, Float x, Float y, Stage uiStageHUD, OrthographicCamera camera){
        if (x != null && y!=null) {
            Vector3 screenCoords = new Vector3(x, y, 0f);
            camera.project(screenCoords);
            Vector2 stageCoords = uiStageHUD.screenToStageCoordinates(new Vector2(screenCoords.x, screenCoords.y));

            x = stageCoords.x - window.getWidth() / 2f;
            y = uiStageHUD.getHeight() - stageCoords.y - 3;

            x = Math.clamp(x, 20f, uiStageHUD.getWidth() - window.getWidth() - 20f);
            y = Math.clamp(y, 20f, uiStageHUD.getHeight() - window.getHeight() - 20f);
        } else {
            x = (uiStageHUD.getWidth() - window.getWidth()) / 2;
            y = (uiStageHUD.getHeight() - window.getHeight()) / 2;
        }
        window.setPosition(x, y);
    }

    public static void clearTooltipScrollFocusWhenPointerLeaves(ContextMenuWithButtons currentTooltip, Stage uiStageHUD) {
        Actor scrollFocus = uiStageHUD.getScrollFocus();
        if (scrollFocus == null || isPointerOverTooltip(currentTooltip)) return;

        Window clickWindow = currentTooltip == null ? null : currentTooltip.asWindow();
        if (isDescendantOf(scrollFocus, clickWindow)) {
            uiStageHUD.setScrollFocus(null);
        }
    }

    public static boolean isPointerOverTooltip(ContextMenuWithButtons currentTooltip) {
        return isPointerOverWindow(currentTooltip == null ? null : currentTooltip.asWindow());
    }

    private static boolean isPointerOverWindow(Window window) {
        return window != null && window.getStage() != null && ComponentHover.isMouseOver(window);
    }

    private static boolean isDescendantOf(Actor actor, Window window) {
        return actor != null && window != null && (actor == window || window.isAscendantOf(actor));
    }
}
