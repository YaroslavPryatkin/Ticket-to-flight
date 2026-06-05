package com.game.Ticket_To_Flight.frontend.components.background;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.game.Ticket_To_Flight.frontend.components.subsidiary.ComponentHover;
import com.game.Ticket_To_Flight.frontend.components.subsidiary.ComponentTextures;

public class SolidRectangleBackground extends Background {
    private final Color normalColor;
    private final Color hoveredColor;
    private final Color selectedColor;

    public SolidRectangleBackground(float x, float y, float width, float height, Color normalColor, Color hoveredColor, Color selectedColor) {
        super(x, y, width, height);
        this.normalColor = normalColor;
        this.hoveredColor = hoveredColor;
        this.selectedColor = selectedColor;
    }

    @Override
    protected boolean isHovered() {
        return ComponentHover.isMouseOver(this);
    }

    @Override
    protected void drawSelected(Batch batch, float parentAlpha) {
        drawColor(batch, selectedColor, parentAlpha);
    }

    @Override
    protected void drawHovered(Batch batch, float parentAlpha) {
        drawColor(batch, hoveredColor, parentAlpha);
    }

    @Override
    protected void drawNormal(Batch batch, float parentAlpha) {
        drawColor(batch, normalColor, parentAlpha);
    }

    private void drawColor(Batch batch, Color color, float parentAlpha) {
        Color oldColor = new Color(batch.getColor());
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        batch.draw(ComponentTextures.whitePixel(), getX(), getY(), getWidth(), getHeight());
        batch.setColor(oldColor);
    }
}
