package com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.GameStageWindows.SubWindows;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Player;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.commonFrontAndBack.StaticGameData;
import com.game.Ticket_To_Flight.frontend.LowLevelHandlerFront;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManager;
import com.game.Ticket_To_Flight.frontend.components.buttons.RoundedButton;
import com.game.Ticket_To_Flight.frontend.components.texts.SingleLineText;
import com.game.Ticket_To_Flight.frontend.components.windows.BaseInvestWindow;

public class AuctionWindow extends BaseInvestWindow {

    public AuctionWindow(Skin skin, final GameUIManager uiManager, final LowLevelHandlerFront llh, final GameData gameData) {
        super("Auction", skin);

        TextButton passBtn = new RoundedButton("Pass", skin, "red");
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
            buildSliderLayout("Bet more to walk first in this round", leftVal, rightVal, leftVal);

            TextButton betBtn = new RoundedButton("Bet", skin, "default");
            betBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    llh.sendAuctionResponse(getSliderValue());
                    remove();
                    uiManager.showSuccessWindow("Your bet was sent to server");
                }
            });

            Table buttonTable = new Table();
            buttonTable.add(betBtn).width(250).height(80).padRight(40);
            buttonTable.add(passBtn).width(250).height(80);
            add(buttonTable).padTop(30).row();
        } else {
            add(new SingleLineText("You have no money to go on", skin)).padBottom(60).row();
            add(passBtn).width(250).height(80);
        }
    }
}
