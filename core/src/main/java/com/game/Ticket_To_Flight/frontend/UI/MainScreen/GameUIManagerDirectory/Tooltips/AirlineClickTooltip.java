package com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.Tooltips;

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
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManager;
import com.game.Ticket_To_Flight.frontend.components.buttons.RoundedButton;
import com.game.Ticket_To_Flight.frontend.components.windows.BaseGameWindow;
import com.game.Ticket_To_Flight.frontend.components.texts.SingleLineText;

public class AirlineClickTooltip extends BaseGameWindow implements MapTooltipWindow {
    public AirlineClickTooltip(
        Skin skin,
        final GameUIManager uiManager,
        final Airline airline,
        final double playerMoney,
        final boolean playerAP,
        final boolean isAvailable,
        boolean canBuyDuringCurrentStage,
        final LowLevelHandlerFront llh
    ) {
        super("Route Details", skin, 560, 320);
        pad(35);
        padTop(110);
        defaults().pad(8);

        Table table = new Table();
        table.defaults().pad(10);

        Label routeLabel = new SingleLineText(routeTitle(airline), skin);
        routeLabel.setColor(Color.CYAN);
        table.add(routeLabel).colspan(2).padBottom(18).row();

        table.add(new SingleLineText("Price: $" + airline.getPrice(), skin)).colspan(2).row();

        if (airline.getPlayer() != null) {
            table.add(new SingleLineText("Owned by: " + airline.getPlayer().getName(), skin)).colspan(2).row();
        } else {
            boolean canAfford = playerMoney >= airline.getPrice() && playerAP && isAvailable;
            boolean canBuyNow = canBuyDuringCurrentStage && canAfford;

            TextButton buyButton = new RoundedButton("Buy", skin, "default");
            TextButton buyAndFinishButton = new RoundedButton("Buy and finish", skin, "default");
            configureBuyButton(buyButton, canBuyDuringCurrentStage, canAfford);
            configureBuyButton(buyAndFinishButton, canBuyDuringCurrentStage, canAfford);

            buyButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    buyAirline(uiManager, llh, airline, canBuyNow, false);
                }
            });

            buyAndFinishButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    buyAirline(uiManager, llh, airline, canBuyNow, true);
                }
            });

            table.add(buyButton).width(220).height(80);
            table.add(buyAndFinishButton).width(350).height(80).row();
        }

        add(table);
        pack();
        setSize(Math.max(getWidth(), 560), Math.max(getHeight(), 320));
    }

    private void configureBuyButton(TextButton button, boolean canBuyDuringCurrentStage, boolean canAfford) {
        if (!canBuyDuringCurrentStage) {
            button.setDisabled(true);
            button.getLabel().setColor(Color.LIGHT_GRAY);
        } else if (!canAfford) {
            button.setDisabled(true);
            button.getLabel().setColor(Color.RED);
        } else {
            button.getLabel().setColor(Color.WHITE);
        }
    }

    private void buyAirline(GameUIManager uiManager, LowLevelHandlerFront llh, Airline airline, boolean canBuyNow, boolean finishStage) {
        if (!canBuyNow) return;

        llh.sendAirlineResponse(airline, finishStage);
        uiManager.removeTooltip();
    }

    private String routeTitle(Airline airline) {
        return airline.getPortA().getCityName() + " -> " + airline.getPortB().getCityName();
    }

    @Override
    public Window asWindow() {
        return this;
    }
}
