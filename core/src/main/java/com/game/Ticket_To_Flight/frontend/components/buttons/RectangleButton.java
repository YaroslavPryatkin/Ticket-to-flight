package com.game.Ticket_To_Flight.frontend.components.buttons;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.game.Ticket_To_Flight.frontend.components.Button;
import com.game.Ticket_To_Flight.frontend.components.ComponentHover;

public class RectangleButton extends Button {
    public RectangleButton(String text, Skin skin) {
        super(text, skin);
    }

    public RectangleButton(String text, Skin skin, String styleName) {
        super(text, skin, styleName);
    }

    public RectangleButton(String text, Skin skin, Runnable onClick) {
        super(text, skin, onClick);
    }

    public RectangleButton(String text, Skin skin, String styleName, Runnable onClick) {
        super(text, skin, styleName, onClick);
    }

    @Override
    protected boolean isHovered() {
        return ComponentHover.isMouseOver(this);
    }
}
