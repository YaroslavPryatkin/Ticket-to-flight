package com.game.Ticket_To_Flight.MainMenu;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class MainMenuMessageDialog extends Dialog {

    public MainMenuMessageDialog(String title, String message, Skin skin) {
        super("", skin);

        padTop(40).padBottom(20).padLeft(20).padRight(20);

        Label messageLabel = new Label(message, skin);
        messageLabel.setWrap(true);
        getContentTable().add(messageLabel).width(750).padBottom(20).row();
        button("OK", true);
    }
}
