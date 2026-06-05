package com.game.Ticket_To_Flight.frontend.components.buttons;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.game.Ticket_To_Flight.frontend.components.subsidiary.ComponentHover;

public class RoundedButton extends Button {
    public RoundedButton(String text, Skin skin) {
        super(text, skin);
    }

    public RoundedButton(String text, Skin skin, String styleName) {
        super(text, skin, styleName);
    }

    public RoundedButton(String text, Skin skin, Runnable onClick) {
        super(text, skin, onClick);
    }

    public RoundedButton(String text, Skin skin, String styleName, Runnable onClick) {
        super(text, skin, styleName, onClick);
    }

    @Override
    protected boolean isHovered() {
        return ComponentHover.isMouseOver(this);
    }
}
