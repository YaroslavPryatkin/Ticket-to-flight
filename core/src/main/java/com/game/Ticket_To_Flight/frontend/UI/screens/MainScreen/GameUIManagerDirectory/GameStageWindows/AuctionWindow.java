package com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.GameUIManagerDirectory.GameStageWindows;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.commonFrontAndBack.StaticGameData;
import com.game.Ticket_To_Flight.frontend.LowLevelHandlerFront;
import com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.GameUIManager;
import com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.GameUIManagerDirectory.BaseGameWindow;
import com.game.Ticket_To_Flight.commonFrontAndBack.StaticGameData;

public class AuctionWindow extends BaseGameWindow {

    public AuctionWindow(Skin skin, final GameUIManager uiManager, final LowLevelHandlerFront llh, final GameData gameData) {
        super("Auction", skin, 450, 350);

        Label subtitleLabel = new Label("Bet more to walk first in this round", skin);
        final Slider slider = new Slider(gameData.currentBet + StaticGameData.minimalAuctionBetIncrease, 10, 1, false, skin);
        final Label amountLabel = new Label("1", skin);

        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                amountLabel.setText(String.valueOf((int) slider.getValue()));
            }
        });

        TextButton betBtn = new TextButton("Bet", skin, "default");
        TextButton passBtn = new TextButton("Pass", skin, "red");

        passBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                llh.sendAuctionResponse(-1);
                remove();
                uiManager.showSuccessWindow("You left the auction");
            }
        });

        betBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int currentBet = (int) slider.getValue();
                llh.sendAuctionResponse(currentBet);
                remove();
            }
        });

        this.add(subtitleLabel).colspan(2).padBottom(40).row();
        this.add(slider).width(250).padRight(15).padBottom(20);
        this.add(amountLabel).width(40).left().padBottom(20).row();

        Table buttonTable = new Table();
        buttonTable.add(betBtn).width(120).padRight(20);
        buttonTable.add(passBtn).width(120);

        this.add(buttonTable).colspan(2).padTop(10).row();
    }
}
