package com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.GameUIManagerDirectory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align; // <-- Обязательный импорт для выравнивания
import com.game.Ticket_To_Flight.frontend.LowLevelHandlerFront;
import com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.GameUIManager;

public class InvestWindow extends Window {

    public InvestWindow(Skin skin, final GameUIManager uiManager, final LowLevelHandlerFront llh) {
        super("Investing", skin);

        this.setSize(450, 350);

        this.getColor().a = 0.8f;

        this.setMovable(false);

        this.getTitleLabel().setAlignment(Align.center);
        this.padTop(50);

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
                System.out.println("Sending response " + investedAmount);
                llh.sendInvestmentResponse(investedAmount);

                remove();

                uiManager.setOverlayActive(false);

                uiManager.showSuccessWindow("Income was invested successfully!");
            }
        });

        this.add(subtitleLabel).padBottom(40).row();
        this.add(slider).width(300).padBottom(10).row();
        this.add(amountLabel).padBottom(40).row();
        this.add(submitBtn).width(150).height(50);
    }
}
