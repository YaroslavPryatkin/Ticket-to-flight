package com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.GameUIManagerDirectory.GameStageWindows;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Align;

public abstract class BaseGameWindow extends Window {

    public BaseGameWindow(String title, Skin skin, float width, float height) {
        super(title, skin);
        applyDefaultStyles();
        this.setSize(width, height);
    }

    public BaseGameWindow(String title, Skin skin) {
        super(title, skin);
        applyDefaultStyles();
    }

    private void applyDefaultStyles() {
        this.getColor().a = 0.8f;
        this.setMovable(false);
        this.getTitleLabel().setAlignment(Align.center);

        this.padTop(90);
    }
}
