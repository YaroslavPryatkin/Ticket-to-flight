package com.game.Ticket_To_Flight.frontend.components.windows;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;

public abstract class BaseHoverTooltip extends BaseGameWindow {
    protected BaseHoverTooltip(String title, Skin skin) {
        super(title, skin);
        top().left();
        getColor().a = 0.85f;
        pad(30);
        padTop(70);
        getTitleLabel().setAlignment(Align.left);
    }
}
