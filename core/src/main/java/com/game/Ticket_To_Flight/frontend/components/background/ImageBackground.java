package com.game.Ticket_To_Flight.frontend.components.background;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.game.Ticket_To_Flight.frontend.components.Background;
import com.game.Ticket_To_Flight.frontend.components.*;

public class ImageBackground extends Background {
    private final Texture texture;

    public ImageBackground(float x, float y, float width, float height, Texture texture) {
        super(x, y, width, height);
        this.texture = texture;
    }

    @Override
    protected boolean isHovered() {
        return ComponentHover.isMouseOver(this);
    }

    @Override
    protected void drawSelected(Batch batch, float parentAlpha) {
        drawImage(batch, parentAlpha);
    }

    @Override
    protected void drawHovered(Batch batch, float parentAlpha) {
        drawImage(batch, parentAlpha);
    }

    @Override
    protected void drawNormal(Batch batch, float parentAlpha) {
        drawImage(batch, parentAlpha);
    }

    private void drawImage(Batch batch, float parentAlpha) {
        Color oldColor = new Color(batch.getColor());
        batch.setColor(1f, 1f, 1f, parentAlpha);
        batch.draw(texture, getX(), getY(), getWidth(), getHeight());
        batch.setColor(oldColor);
    }
}
