package com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.GameUIManagerDirectory.GameStageWindows.SubWindows;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.GameUIManager;
import com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.GameUIManagerDirectory.GameStageWindows.BaseGameWindow;
import com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.Managers.WindowManager;

public class SuccessWindow extends BaseGameWindow {
    public SuccessWindow(Skin skin, final GameUIManager uiManager, String message) {
        super("Notification", skin);
        this.setModal(true);

        this.pad(60);

        this.padTop(120);

        Label messageLabel = new Label(message, skin);

        TextButton closeBtn = new TextButton("Close", skin);
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
