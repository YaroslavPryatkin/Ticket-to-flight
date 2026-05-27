package com.game.Ticket_To_Flight.frontend.components;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;

public abstract class BoxContainer extends Group {
    protected Background background;

    public BoxContainer(float x, float y, float width, float height) {
        setPosition(x, y);
        setSize(width, height);
    }

    public void setBackground(Background background) {
        if (this.background != null) {
            this.background.remove();
        }
        this.background = background;
        if (background != null) {
            background.setPosition(0, 0);
            background.setSize(getWidth(), getHeight());
            addActorAt(0, background);
        }
    }

    public void addElement(Actor actor, float x, float y) {
        actor.setPosition(x, y);
        addActor(actor);
    }

    public void setSelected(boolean selected) {
        if (background != null) {
            background.setSelected(selected);
        }
    }

    public abstract void layoutElements();
}
