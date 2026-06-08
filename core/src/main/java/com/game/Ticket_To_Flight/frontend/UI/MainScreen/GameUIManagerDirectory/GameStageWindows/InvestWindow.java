package com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.GameStageWindows;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Player;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.commonFrontAndBack.StaticGameData;
import com.game.Ticket_To_Flight.frontend.LowLevelHandlerFront;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManager;
import com.game.Ticket_To_Flight.frontend.components.buttons.RoundedButton;
import com.game.Ticket_To_Flight.frontend.components.windows.BaseInvestWindow;

public class InvestWindow extends BaseInvestWindow {

    public InvestWindow(Skin skin, final GameUIManager uiManager, final LowLevelHandlerFront llh, GameData gameData) {
        super("Investing", skin);

        Player curPl = gameData.players.get(llh.getMyId());
        int maxAdditionalShares = StaticGameData.maxAmountOfShares - curPl.amountOfShares;

        buildSliderLayout("invest your incomes to money", 0, maxAdditionalShares, 0);

        TextButton submitBtn = new RoundedButton("Submit", skin);
        submitBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                llh.sendInvestmentResponse(getSliderValue());
                remove();
                uiManager.showNotificationWindow("Your request was sent to server");
            }
        });

        add(submitBtn).width(300).height(80);
    }
}
