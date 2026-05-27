package com.game.Ticket_To_Flight.frontend.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public final class ComponentTextures {
    private static Texture whitePixel;

    private ComponentTextures() {}

    public static Texture whitePixel() {
        if (whitePixel == null) {
            Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pixmap.setColor(Color.WHITE);
            pixmap.fill();
            whitePixel = new Texture(pixmap);
            pixmap.dispose();
        }
        return whitePixel;
    }

    public static Texture roundedRectangle(float width, float height, float radius, Color color) {
        int w = Math.max(1, Math.round(width));
        int h = Math.max(1, Math.round(height));
        int r = Math.min(Math.max(0, Math.round(radius)), Math.min(w, h) / 2);

        Pixmap pixmap = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fillRectangle(r, 0, w - 2 * r, h);
        pixmap.fillRectangle(0, r, w, h - 2 * r);
        pixmap.fillCircle(r, r, r);
        pixmap.fillCircle(w - r - 1, r, r);
        pixmap.fillCircle(r, h - r - 1, r);
        pixmap.fillCircle(w - r - 1, h - r - 1, r);

        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }
}
