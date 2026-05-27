package com.game.Ticket_To_Flight.frontend.components;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public abstract class Text extends Label {
    private final GlyphLayout glyphLayout = new GlyphLayout();

    public Text(CharSequence text, Skin skin) {
        super(text, skin);
    }

    public Text(CharSequence text, Skin skin, float x, float y) {
        super(text, skin);
        setPosition(x, y);
    }

    public float getTextWidth() {
        glyphLayout.setText(getStyle().font, getText());
        return glyphLayout.width;
    }

    protected void drawLabel(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    @Override
    public abstract void draw(Batch batch, float parentAlpha);
}
