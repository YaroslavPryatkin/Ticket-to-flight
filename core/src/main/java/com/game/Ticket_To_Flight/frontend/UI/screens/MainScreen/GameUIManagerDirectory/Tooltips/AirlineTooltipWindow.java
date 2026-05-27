package com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.GameUIManagerDirectory.Tooltips;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airline;
import com.game.Ticket_To_Flight.frontend.LowLevelHandlerFront;
import com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.GameUIManager;
import com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.GameUIManagerDirectory.GameStageWindows.BaseGameWindow;

public class AirlineTooltipWindow extends BaseGameWindow {

    public AirlineTooltipWindow(Skin skin, final GameUIManager uiManager, final Airline airline, final double playerMoney, boolean canBuyDuringCurrentStage, LowLevelHandlerFront llh) {
        super("Route Details", skin, 150, 200);
        this.pad(35);
        this.defaults().pad(8);

        this.getTitleTable().padTop(20);

        Table table = new Table();
        table.defaults().pad(10);


        if (airline.getPlayer() != null) {
            table.add(new Label("Owned by:", skin)).padBottom(8).row();
            table.add(new Label(airline.getPlayer().getName(), skin)).minWidth(360);
        }
        else {
            table.add(new Label("Buy for", skin)).padBottom(12).row();

            final TextButton buyButton = new TextButton("$" + airline.getPrice(), skin);
            boolean canAfford = playerMoney >= airline.getPrice();
            boolean canBuyNow = canBuyDuringCurrentStage && canAfford;

            if (!canBuyDuringCurrentStage) {
                buyButton.setDisabled(true);
                buyButton.getLabel().setColor(Color.LIGHT_GRAY);
            } else if (!canAfford) {
                buyButton.setDisabled(true);
                buyButton.getLabel().setColor(Color.RED);
            }
            else {
                buyButton.getLabel().setColor(Color.WHITE);
            }

            buyButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (buyButton.isDisabled() || !canBuyNow) return;
                    llh.sendAirlineResponse(airline, false);
                    uiManager.removeTooltip();
                    uiManager.showSuccessWindow("Airline was bought successfully!");
                }
            });

            table.add(buyButton).width(360).height(110);
        }

        this.add(table);
        this.pack();
        this.setSize(Math.max(this.getWidth(), 460), Math.max(this.getHeight(), 240));
    }
}
