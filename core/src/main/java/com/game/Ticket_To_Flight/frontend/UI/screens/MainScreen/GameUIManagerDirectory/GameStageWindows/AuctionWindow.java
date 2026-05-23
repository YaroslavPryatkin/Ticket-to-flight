package com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.GameUIManagerDirectory.GameStageWindows;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Player;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.commonFrontAndBack.StaticGameData;
import com.game.Ticket_To_Flight.frontend.LowLevelHandlerFront;
import com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.GameUIManager;
import com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.GameUIManagerDirectory.BaseGameWindow;
import com.game.Ticket_To_Flight.commonFrontAndBack.StaticGameData;

public class AuctionWindow extends BaseGameWindow {

    public AuctionWindow(Skin skin, final GameUIManager uiManager, final LowLevelHandlerFront llh, final GameData gameData) {
        super("Auction", skin, 450, 350);

        Label subtitleLabel;

        TextButton passBtn = new TextButton("Pass", skin, "red");

        passBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                llh.sendAuctionResponse(-1);
                remove();
                uiManager.showSuccessWindow("You left the auction");
            }
        });

        Player pl = gameData.players.get(gameData.currentPlayer);
        int leftVal = gameData.currentBet + StaticGameData.minimalAuctionBetIncrease;
        int rightVal = pl.money + pl.auctionBet;

        if (leftVal <= rightVal) {
            Slider slider;
            Label amountLabel;
            TextButton betBtn;
            subtitleLabel = new Label("Bet more to walk first in this round", skin);
            amountLabel = new Label(String.valueOf(leftVal), skin);
            slider = new Slider(leftVal, rightVal, 1, false, skin);
            slider.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                    amountLabel.setText(String.valueOf((int) slider.getValue()));
                }
            });

            betBtn = new TextButton("Bet", skin, "gray");

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
        }
        else {
            subtitleLabel = new Label("You have no money to go on", skin);
            this.add(subtitleLabel).padBottom(40).row();
            this.add(passBtn).width(150).height(50);
        }
    }
}
