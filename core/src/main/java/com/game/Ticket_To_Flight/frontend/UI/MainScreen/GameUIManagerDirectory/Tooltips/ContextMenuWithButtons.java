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
import com.game.Ticket_To_Flight.frontend.components.texts.WrappedText;
import com.game.Ticket_To_Flight.frontend.components.windows.BaseGameWindow;

public class ContextMenuWithButtons extends BaseGameWindow implements MapTooltipWindow {
    private static final float CONTENT_WIDTH = 570f;
    private static final float flightTooltipButtonWidth = 300;
    private static final float flightTooltipButtonHeight = 60;

    //for buying
    public ContextMenuWithButtons(
        Skin skin,
        final GameUIManager uiManager,
        final Airline airline,
        final double playerMoney,
        final boolean playerAP,
        final boolean isAvailable,
        boolean canBuyDuringCurrentStage,
        final LowLevelHandlerFront llh
    ) {
        super("", skin, 600, 320);
        this.getTitleTable().remove();
        this.padTop(10);
        this.setMovable(false);
        defaults().pad(10);

        Table table = new Table();
        table.defaults().pad(10);

        Label routeLabel = new WrappedText(routeTitle(airline), skin, CONTENT_WIDTH);
        routeLabel.setColor(Color.CYAN);
        table.add(routeLabel).width(CONTENT_WIDTH).colspan(2).padBottom(18).row();

        Label priceLabel = new WrappedText("Price: $" + airline.getPrice(), skin, CONTENT_WIDTH);
        table.add(priceLabel).width(CONTENT_WIDTH).colspan(2).row();

        if (airline.getPlayer() != null) {
            Label ownerLabel = new WrappedText("Owned by: " + airline.getPlayer().getName(), skin, CONTENT_WIDTH);
            table.add(ownerLabel).width(CONTENT_WIDTH).colspan(2).row();
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
        setSize(Math.max(getWidth(), 600), Math.max(getHeight(), 320));
    }

    public ContextMenuWithButtons(Skin skin, Runnable rn){
        super("", skin, flightTooltipButtonWidth + 20, flightTooltipButtonHeight + 20);
        this.getTitleTable().remove();
        this.padTop(10);
        this.setMovable(false);
        TextButton okButton = new RoundedButton("Choose this", skin);
        okButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (rn != null) {
                    rn.run();
                }
            }
        });

        this.add(okButton).width(flightTooltipButtonWidth).height(flightTooltipButtonHeight).center();

        this.pack();

        this.setSize(600, 320);
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
    }

    private String routeTitle(Airline airline) {
        return airline.getPortA().getCityName() + " - " + airline.getPortB().getCityName();
    }

    @Override
    public Window asWindow() {
        return this;
    }
}
