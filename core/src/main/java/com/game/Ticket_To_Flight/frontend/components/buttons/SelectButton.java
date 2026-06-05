package com.game.Ticket_To_Flight.frontend.components.buttons;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class SelectButton extends RoundedButton {

    public interface OnPassengerSelected {
        void onSelect();
    }

    public SelectButton(String text, Skin skin, OnPassengerSelected listener) {
        super(text, skin);

        addListener(new ClickListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (!isDisabled()) {
                    setColor(Color.GREEN); // Делаем кнопку зеленой при нажатии
                }
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                if (!isDisabled()) {
                    setColor(Color.WHITE);
                }
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!isDisabled() && listener != null) {
                    listener.onSelect();
                }
            }
        });
    }
}
