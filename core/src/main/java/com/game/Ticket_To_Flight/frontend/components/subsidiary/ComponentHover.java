package com.game.Ticket_To_Flight.frontend.components.subsidiary;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

public final class ComponentHover {
    private ComponentHover() {}

    public static boolean isMouseOver(Actor actor) {
        if (actor.getStage() == null) return false;
        Vector2 localCoords = actor.getStage().screenToStageCoordinates(
            new Vector2(Gdx.input.getX(), Gdx.input.getY())
        );
        actor.stageToLocalCoordinates(localCoords);
        return localCoords.x >= 0 &&
            localCoords.x <= actor.getWidth() &&
            localCoords.y >= 0 &&
            localCoords.y <= actor.getHeight();
    }
}
