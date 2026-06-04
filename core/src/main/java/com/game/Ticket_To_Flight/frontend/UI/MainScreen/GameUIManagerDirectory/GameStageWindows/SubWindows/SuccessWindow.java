package com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.GameStageWindows.SubWindows;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManager;
import com.game.Ticket_To_Flight.frontend.components.BaseGameWindow;
import com.game.Ticket_To_Flight.frontend.components.buttons.RoundedButton;
import com.game.Ticket_To_Flight.frontend.components.texts.SingleLineText;

public class SuccessWindow extends BaseGameWindow {
    public SuccessWindow(Skin skin, final GameUIManager uiManager, String message) {
        super("Notification", skin);
        this.setModal(true);

        this.pad(60);

        this.padTop(120);

        Label messageLabel = new SingleLineText(message, skin);

        TextButton closeBtn = new RoundedButton("Close", skin);
        closeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                remove();
                uiManager.setWindowOpen(false);
            }
        });

        this.add(messageLabel).padBottom(60).row();

        this.add(closeBtn).width(250).height(80);

        this.pack();
    }
}
