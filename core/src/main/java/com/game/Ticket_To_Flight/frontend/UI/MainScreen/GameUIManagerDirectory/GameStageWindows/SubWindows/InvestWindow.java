package com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.GameStageWindows.SubWindows;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Player;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.commonFrontAndBack.StaticGameData;
import com.game.Ticket_To_Flight.frontend.LowLevelHandlerFront;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManager;
import com.game.Ticket_To_Flight.frontend.components.BaseGameWindow;
import com.game.Ticket_To_Flight.frontend.components.buttons.RoundedButton;
import com.game.Ticket_To_Flight.frontend.components.texts.SingleLineText;

public class InvestWindow extends BaseGameWindow {

    public InvestWindow(Skin skin, final GameUIManager uiManager, final LowLevelHandlerFront llh, GameData gameData) {
        super("Investing", skin, 800, 600);

        Player curPl = gameData.players.get(llh.getMyId());
        Label subtitleLabel = new SingleLineText("invest your incomes to money", skin);
        final Slider slider = new Slider(0, StaticGameData.maxAmountOfShares - curPl.amountOfShares, 1, false, skin);
        final Label amountLabel = new SingleLineText("0", skin);

        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                amountLabel.setText(String.valueOf((int) slider.getValue()));
            }
        });

        TextButton submitBtn = new RoundedButton("Submit", skin);
        submitBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int investedAmount = (int) slider.getValue();
                llh.sendInvestmentResponse(investedAmount);
                remove();
                uiManager.showSuccessWindow("Your request was sent to server");
            }
        });

        this.add(subtitleLabel).padBottom(60).row();
        this.add(slider).width(500).padBottom(20).row();
        this.add(amountLabel).padBottom(60).row();
        this.add(submitBtn).width(300).height(80);
    }
}
