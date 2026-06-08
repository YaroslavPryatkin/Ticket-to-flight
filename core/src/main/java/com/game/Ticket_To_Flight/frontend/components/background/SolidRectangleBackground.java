package com.game.Ticket_To_Flight.frontend.components.background;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.game.Ticket_To_Flight.frontend.components.subsidiary.ComponentHover;
import com.game.Ticket_To_Flight.frontend.components.subsidiary.ComponentTextures;

public class SolidRectangleBackground extends Background {
    private final Color normalColor;
    private final Color hoveredColor;
    private final Color selectedColor;

    private Color borderColor = null;
    private float borderWidth = 0f;;

    public SolidRectangleBackground(float x, float y, float width, float height, Color normalColor, Color hoveredColor, Color selectedColor) {
        super(x, y, width, height);
        this.normalColor = normalColor;
        this.hoveredColor = hoveredColor;
        this.selectedColor = selectedColor;
    }

    public void setBorder(Color borderColor, float borderWidth) {
        this.borderColor = borderColor;
        this.borderWidth = borderWidth;
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

        if (borderColor != null && borderWidth > 0) {
            batch.setColor(borderColor.r, borderColor.g, borderColor.b, borderColor.a * parentAlpha);

            float x = getX();
            float y = getY();
            float w = getWidth();
            float h = getHeight();

            batch.draw(ComponentTextures.whitePixel(), x, y, w, borderWidth);
            batch.draw(ComponentTextures.whitePixel(), x, y + h - borderWidth, w, borderWidth);
            batch.draw(ComponentTextures.whitePixel(), x, y + borderWidth, borderWidth, h - 2 * borderWidth);
            batch.draw(ComponentTextures.whitePixel(), x + w - borderWidth, y + borderWidth, borderWidth, h - 2 * borderWidth);
        }

        batch.setColor(oldColor);
    }
}
