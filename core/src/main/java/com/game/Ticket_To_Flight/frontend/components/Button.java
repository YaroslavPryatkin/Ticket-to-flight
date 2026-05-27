package com.game.Ticket_To_Flight.frontend.components;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public abstract class Button extends TextButton {
    private final Runnable onClick;

    public Button(String text, Skin skin) {
        this(text, skin, "default", null);
    }

    public Button(String text, Skin skin, String styleName) {
        this(text, skin, styleName, null);
    }

    public Button(String text, Skin skin, Runnable onClick) {
        this(text, skin, "default", onClick);
    }

    public Button(String text, Skin skin, String styleName, Runnable onClick) {
        super(text, skin, styleName);
        this.onClick = onClick;
        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onClick();
            }
        });
    }

    public void onClick() {
        if (onClick != null) {
            onClick.run();
        }
    }

    protected abstract boolean isHovered();
}
