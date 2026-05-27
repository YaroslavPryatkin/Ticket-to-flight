package com.game.Ticket_To_Flight.frontend.UI.MainMenuScreen;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.game.Ticket_To_Flight.frontend.components.buttons.RoundedButton;
import com.game.Ticket_To_Flight.frontend.components.texts.WrappedText;

public class MainMenuMessageDialog extends Dialog {

    public MainMenuMessageDialog(String title, String message, Skin skin) {
        super("", skin);

        padTop(40).padBottom(20).padLeft(20).padRight(20);

        Label messageLabel = new WrappedText(message, skin, 750);
        messageLabel.setWrap(true);
        getContentTable().add(messageLabel).width(750).padBottom(20).row();
        button(new RoundedButton("OK", skin), true);
    }
}
