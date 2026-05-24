package com.game.Ticket_To_Flight.frontend.UI.screens.ConnectionScreen;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.game.Ticket_To_Flight.frontend.LowLevelHandlerFront;

public class ConnectionUIManager extends Table {

    private final Skin skin;
    private final LowLevelHandlerFront llh;

    public boolean isNicknameInput = false;

    public ConnectionUIManager(Skin skin, LowLevelHandlerFront llh) {
        this.skin = skin;
        this.llh = llh;
        this.setFillParent(true);
    }

    public void showLoadingScreen(String message) {
        this.clearChildren();

        Image loadingIcon = new Image(skin.getDrawable("loading-icon"));

        loadingIcon.setSize(50, 50);
        loadingIcon.setOrigin(Align.center);

        loadingIcon.addAction(Actions.forever(Actions.rotateBy(-360f, 1.5f)));

        Label messageLabel = new Label(message, skin);
        messageLabel.setFontScale(1.2f);
        messageLabel.setAlignment(Align.center);

        this.add(loadingIcon).size(50, 50).padBottom(20).row();
        this.add(messageLabel);
    }

    public void showNicknameInputScreen() {
        isNicknameInput = true;
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
                    isNicknameInput = false;
                }
            }
        });

        nicknameField.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ENTER || keycode == Input.Keys.NUMPAD_ENTER) {
                    String nickname = nicknameField.getText().trim();

                    if (!nickname.isEmpty()) {
                        System.out.println("Nickname submitted via Enter: " + nickname);
                        llh.sendJoinRequest(nickname);
                    }
                    return true;
                }
                return false;
            }
        });

        this.add(promptLabel).padBottom(30).row();
        this.add(nicknameField).width(800).height(150).padBottom(50).row();
        this.add(acceptBtn).width(500).height(150);

        if (this.getStage() != null) {
            this.getStage().setKeyboardFocus(nicknameField);
        }
    }

    public void showMessageWindow(String title, String message) {
        if (this.getStage() == null) return;

        final Window popupWindow = new Window(title, skin);
        popupWindow.setMovable(false);
        popupWindow.setModal(true);

        Label msgLabel = new Label(message, skin);
        msgLabel.setWrap(true);
        msgLabel.setAlignment(com.badlogic.gdx.utils.Align.center);

        TextButton okBtn = new TextButton("OK", skin);
        okBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                popupWindow.remove();
            }
        });

        popupWindow.add(msgLabel).width(500).pad(30).row();
        popupWindow.add(okBtn).width(200).height(60).padBottom(20);

        popupWindow.pack();
        popupWindow.setPosition(
            (this.getStage().getWidth() - popupWindow.getWidth()) / 2f,
            (this.getStage().getHeight() - popupWindow.getHeight()) / 2f
        );

        this.getStage().addActor(popupWindow);
    }
}
