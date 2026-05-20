package com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.GameUIManagerDirectory;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.GameUIManager;

public class AuctionWindow extends Table {
    private final GameUIManager uiManager;

    public AuctionWindow(Skin skin, GameUIManager uiManager) {
        this.uiManager = uiManager;

        this.setFillParent(true);
        this.setBackground(skin.getDrawable("blue-bg"));
        this.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.enabled);

        Label titleLabel = new Label("Auction", skin);
        titleLabel.setFontScale(1.2f);

        Label subtitleLabel = new Label("Bet more to walk first in this round", skin);

        final Slider slider = new Slider(1, 10, 1, false, skin);
        final Label amountLabel = new Label("1", skin);

        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                amountLabel.setText(String.valueOf((int) slider.getValue()));
            }
        });

        TextButton betBtn = new TextButton("Bet", skin, "default"); // Темная
        TextButton passBtn = new TextButton("Pass", skin, "red");   // Красная

        passBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                remove();
                uiManager.setOverlayActive(false);
                uiManager.showSuccessWindow("You left the auction");
            }
        });

        betBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                int currentBet = (int) slider.getValue();

                remove();
                uiManager.setOverlayActive(false);
            }
        });

        this.add(titleLabel).colspan(2).padBottom(10).row();
        this.add(subtitleLabel).colspan(2).padBottom(25).row();

        this.add(slider).width(250).padRight(15);
        this.add(amountLabel).width(40).left().row();

        Table buttonTable = new Table();
        buttonTable.add(betBtn).width(120).padRight(20);
        buttonTable.add(passBtn).width(120);

        this.add(buttonTable).colspan(2).padTop(30).row();
    }
}
