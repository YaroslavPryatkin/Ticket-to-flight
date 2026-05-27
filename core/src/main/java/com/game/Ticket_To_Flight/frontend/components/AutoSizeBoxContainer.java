package com.game.Ticket_To_Flight.frontend.components;

import com.badlogic.gdx.scenes.scene2d.Actor;

public class AutoSizeBoxContainer extends BoxContainer {
    private final float paddingX;
    private final float paddingY;

    public AutoSizeBoxContainer(float x, float y, float paddingX, float paddingY) {
        super(x, y, 0, 0);
        this.paddingX = paddingX;
        this.paddingY = paddingY;
    }

    @Override
    public void layoutElements() {
        float maxRight = 0;
        float maxTop = 0;

        for (Actor actor : getChildren()) {
            if (actor == background) continue;
            maxRight = Math.max(maxRight, actor.getX() + actor.getWidth());
            maxTop = Math.max(maxTop, actor.getY() + actor.getHeight());
        }

        setSize(maxRight + paddingX * 2, maxTop + paddingY * 2);
        if (background != null) {
            background.setSize(getWidth(), getHeight());
        }
    }
}
