package com.game.Ticket_To_Flight.frontend.components.background;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.game.Ticket_To_Flight.frontend.components.Background;
import com.game.Ticket_To_Flight.frontend.components.ComponentHover;
import com.game.Ticket_To_Flight.frontend.components.ComponentTextures;

public class RoundedRectangleBackground extends Background {
    private final Texture normalTexture;
    private final Texture hoveredTexture;
    private final Texture selectedTexture;

    public RoundedRectangleBackground(float x, float y, float width, float height, float radius, Color normalColor, Color hoveredColor, Color selectedColor) {
        super(x, y, width, height);
        this.normalTexture = ComponentTextures.roundedRectangle(width, height, radius, normalColor);
        this.hoveredTexture = ComponentTextures.roundedRectangle(width, height, radius, hoveredColor);
        this.selectedTexture = ComponentTextures.roundedRectangle(width, height, radius, selectedColor);
    }

    @Override
    protected boolean isHovered() {
        return ComponentHover.isMouseOver(this);
    }

    @Override
    protected void drawSelected(Batch batch, float parentAlpha) {
        drawTexture(batch, selectedTexture, parentAlpha);
    }

    @Override
    protected void drawHovered(Batch batch, float parentAlpha) {
        drawTexture(batch, hoveredTexture, parentAlpha);
    }

    @Override
    protected void drawNormal(Batch batch, float parentAlpha) {
        drawTexture(batch, normalTexture, parentAlpha);
    }

    private void drawTexture(Batch batch, Texture texture, float parentAlpha) {
        Color oldColor = new Color(batch.getColor());
        batch.setColor(1f, 1f, 1f, parentAlpha);
        batch.draw(texture, getX(), getY(), getWidth(), getHeight());
        batch.setColor(oldColor);
    }
}
