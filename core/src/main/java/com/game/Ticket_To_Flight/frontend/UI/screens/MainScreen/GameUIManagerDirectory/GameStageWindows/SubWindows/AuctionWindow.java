package com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.GameUIManagerDirectory.GameStageWindows.SubWindows;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Player;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.commonFrontAndBack.StaticGameData;
import com.game.Ticket_To_Flight.frontend.LowLevelHandlerFront;
import com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.GameUIManager;
import com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.GameUIManagerDirectory.GameStageWindows.BaseGameWindow;

public class AuctionWindow extends BaseGameWindow {

    public AuctionWindow(Skin skin, final GameUIManager uiManager, final LowLevelHandlerFront llh, final GameData gameData) {
        super("Auction", skin, 800, 600);

        Label subtitleLabel;

        TextButton passBtn = new TextButton("Pass", skin, "red");

        passBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                llh.sendAuctionPass();
                remove();
                uiManager.showSuccessWindow("You left the auction. Wait when other players finish it.");
            }
        });

        Player pl = gameData.players.get(gameData.currentPlayer);
        int leftVal = gameData.currentBet + StaticGameData.minimalAuctionBetIncrease;
        int rightVal = pl.money + pl.auctionBet;

        if (leftVal <= rightVal) {
            final Slider slider;
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

            betBtn = new TextButton("Bet", skin, "default");

            betBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    int currentBet = (int) slider.getValue();
                    llh.sendAuctionResponse(currentBet);
                    remove();
                    uiManager.showSuccessWindow("Your bet was sent to server");
                }
            });

            this.add(subtitleLabel).colspan(2).padBottom(60).row();

            this.add(slider).width(500).padLeft(110).padRight(30).padBottom(40);
            this.add(amountLabel).width(80).left().padBottom(40).row();

            Table buttonTable = new Table();

            buttonTable.add(betBtn).width(250).height(80).padRight(40);
            buttonTable.add(passBtn).width(250).height(80);

            this.add(buttonTable).colspan(2).padTop(30).row();
        }
        else {
            subtitleLabel = new Label("You have no money to go on", skin);
            this.add(subtitleLabel).padBottom(60).row();
            this.add(passBtn).width(250).height(8);
        }
    }
}
