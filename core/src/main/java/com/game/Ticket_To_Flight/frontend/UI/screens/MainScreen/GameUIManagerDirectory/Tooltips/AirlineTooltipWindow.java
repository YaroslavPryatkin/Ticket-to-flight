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

public class AirlineTooltipWindow extends Window {

    public AirlineTooltipWindow(Skin skin, final GameUIManager uiManager, final Airline airline, final double playerMoney, boolean isBuyingPhase, LowLevelHandlerFront llh) {
        super("Route Details", skin);
        this.pad(20);

        Table table = new Table();


        if (airline.getPlayer() != null) {
            table.add(new Label("Owned by: " + airline.getPlayer().getName(), skin));
        }
        else {
            final TextButton buyButton = new TextButton("Buy for $" + airline.getPrice(), skin);

            if (!isBuyingPhase) {
                buyButton.setDisabled(true);
            }

            if (playerMoney < airline.getPrice()) {
                buyButton.getLabel().setColor(Color.RED);
            }
            else {
                buyButton.getLabel().setColor(Color.WHITE);
            }

            buyButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (buyButton.isDisabled() || playerMoney < airline.getPrice()) return;
                    System.out.println("Buying route!");
                    if(airline == null) llh.sendAirlinePass();
                    else llh.sendAirlineResponse(airline, false);
                    uiManager.removeTooltip();
                    uiManager.showSuccessWindow("Airline was bought successfully!");
                }
            });

            table.add(buyButton).width(150).height(40);
        }

        this.add(table);
        this.pack();
    }
}
