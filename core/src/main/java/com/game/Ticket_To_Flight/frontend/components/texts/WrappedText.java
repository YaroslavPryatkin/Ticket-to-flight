package com.game.Ticket_To_Flight.frontend.components.texts;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class WrappedText extends Text {
    private final float maxWidth;

    public WrappedText(CharSequence text, Skin skin, float maxWidth) {
        super(text, skin);
        this.maxWidth = maxWidth;
        setWrap(true);
        setWidth(maxWidth);
    }

    public WrappedText(CharSequence text, Skin skin, float x, float y, float maxWidth) {
        super(text, skin, x, y);
        this.maxWidth = maxWidth;
        setWrap(true);
        setWidth(maxWidth);
    }

    public float getMaxWidth() {
        return maxWidth;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        drawLabel(batch, parentAlpha);
    }
}
