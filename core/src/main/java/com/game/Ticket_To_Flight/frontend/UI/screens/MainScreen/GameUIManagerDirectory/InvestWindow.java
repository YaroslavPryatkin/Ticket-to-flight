package com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.GameUIManagerDirectory;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.game.Ticket_To_Flight.frontend.LowLevelHandlerFront;
import com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.GameUIManager;
import com.game.Ticket_To_Flight.network.Network;

public class InvestWindow extends Table {
    public InvestWindow(Skin skin, GameUIManager uiManager, LowLevelHandlerFront llh) {
        uiManager.setOverlayActive(true);

        final Table overlayWindow = new Table();
        overlayWindow.setFillParent(true);
        overlayWindow.setBackground(skin.getDrawable("blue-bg"));
        overlayWindow.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.enabled);

        Label titleLabel = new Label("Investing", skin);
        titleLabel.setFontScale(1.5f);

        Label subtitleLabel = new Label("invest your incomes to money", skin);

        final Slider slider = new Slider(1, 10, 1, false, skin);
        final Label amountLabel = new Label("1", skin);

        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                amountLabel.setText(String.valueOf((int) slider.getValue()));
            }
        });

        TextButton submitBtn = new TextButton("Submit", skin);
        submitBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int investedAmount = (int) slider.getValue();
                llh.setNewMessage(new Network.UserInvestment(llh.getMyId(), investedAmount));
                overlayWindow.remove();
                uiManager.showSuccessWindow("Income was invested successfully!");
            }
        });

        overlayWindow.add(titleLabel).padBottom(15).row();
        overlayWindow.add(subtitleLabel).padBottom(40).row();
        overlayWindow.add(slider).width(300).padBottom(10).row();
        overlayWindow.add(amountLabel).padBottom(40).row();
        overlayWindow.add(submitBtn).width(150).height(50);
    }
}
