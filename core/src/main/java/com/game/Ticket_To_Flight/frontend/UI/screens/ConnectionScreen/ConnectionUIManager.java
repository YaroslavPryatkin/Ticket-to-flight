package com.game.Ticket_To_Flight.frontend.UI.screens.ConnectionScreen;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.game.Ticket_To_Flight.frontend.LowLevelHandlerFront;

public class ConnectionUIManager extends Table {

    private final Skin skin;
    private final LowLevelHandlerFront llh;

    public ConnectionUIManager(Skin skin, LowLevelHandlerFront llh) {
        this.skin = skin;
        this.llh = llh;
        this.setFillParent(true); // Растягиваем на весь экран
    }

    public void showLoadingScreen(String message) {
        this.clearChildren();

        Image loadingIcon = new Image(skin.getDrawable("loading-icon"));
        loadingIcon.setOrigin(Align.center);
        loadingIcon.addAction(Actions.forever(Actions.rotateBy(-360f, 1.5f)));

        Label messageLabel = new Label(message, skin);
        messageLabel.setFontScale(1.2f);
        messageLabel.setAlignment(Align.center);

        this.add(loadingIcon).size(50, 50).padBottom(20).row();
        this.add(messageLabel);
    }

    public void showNicknameInputScreen() {
        this.clearChildren();

        Label promptLabel = new Label("Enter your Nickname", skin);
        promptLabel.setFontScale(1.5f);

        final TextField nicknameField = new TextField("", skin);
        nicknameField.setAlignment(Align.center);

        TextButton acceptBtn = new TextButton("Accept", skin);
        acceptBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String nickname = nicknameField.getText().trim();

                if (!nickname.isEmpty()) {
                    System.out.println("Nickname submitted: " + nickname);
                    llh.sendJoinRequest(nickname);
                }
            }
        });

        this.add(promptLabel).padBottom(30).row();
        this.add(nicknameField).width(300).height(50).padBottom(20).row();
        this.add(acceptBtn).width(150).height(50);
    }
}
