package com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.GameUIManagerDirectory;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.GameUIManager;

public class SuccessWindow extends Window {

    public SuccessWindow(Skin skin, GameUIManager uiManager, String message) {
        super(message, skin);

        this.pad(30);
        this.padTop(50);
        this.setModal(true);
        this.setMovable(false);

        Label messageLabel = new Label(message, skin);

        TextButton closeBtn = new TextButton("Close", skin);
        closeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                remove();
                uiManager.setOverlayActive(false);
            }
        });

        this.add(messageLabel).padBottom(30).row();
        this.add(closeBtn).width(120).height(40);

        this.pack();
    }
}
