package com.game.Ticket_To_Flight.frontend.components.texts;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.game.Ticket_To_Flight.frontend.components.Text;

public class SingleLineText extends Text {
    public SingleLineText(CharSequence text, Skin skin) {
        super(text, skin);
        setWrap(false);
    }

    public SingleLineText(CharSequence text, Skin skin, float x, float y) {
        super(text, skin, x, y);
        setWrap(false);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        drawLabel(batch, parentAlpha);
    }
}
