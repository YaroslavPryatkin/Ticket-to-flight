package com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.GameStageWindows.SubWindows;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.AbilityType;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManager;
import com.game.Ticket_To_Flight.frontend.components.windows.BaseGameWindow;
import com.game.Ticket_To_Flight.frontend.components.buttons.RoundedButton;
import com.game.Ticket_To_Flight.frontend.components.texts.SingleLineText;

public class NotificationWindow extends BaseGameWindow {
    public NotificationWindow(Skin skin, final GameUIManager uiManager, String message) {
        super("Notification", skin);

        this.pad(60);
        this.pad(120,10,10,10);

        Label messageLabel = new SingleLineText(message, skin);
        this.setWidth(messageLabel.getWidth() + 20);


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
