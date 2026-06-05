package com.game.Ticket_To_Flight.frontend.components.background;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public abstract class Background extends Table {
    private boolean selected = false;

    public Background(float x, float y, float width, float height) {
        setPosition(x, y);
        setSize(width, height);
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (selected) {
            drawSelected(batch, parentAlpha);
        } else if (isHovered()) {
            drawHovered(batch, parentAlpha);
        } else {
            drawNormal(batch, parentAlpha);
        }
        super.draw(batch, parentAlpha);
    }

    protected abstract boolean isHovered();
    protected abstract void drawSelected(Batch batch, float parentAlpha);
    protected abstract void drawHovered(Batch batch, float parentAlpha);
    protected abstract void drawNormal(Batch batch, float parentAlpha);
}
