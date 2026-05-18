package com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.game.Ticket_To_Flight.Utilities.MapHolder;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airline;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PassengerType;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.frontend.LowLevelHandlerFront;
import com.game.Ticket_To_Flight.frontend.MainClient;
import com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.GameUIManagerDirectory.StyleFactory;
import com.game.Ticket_To_Flight.network.Network;

import java.util.Iterator;

public class GameUIManager {
    private final Stage uiStage;
    private final LowLevelHandlerFront llh;
    private final GameData gameData;

    private Skin skin_default_window;
    private Skin skin_invest_window;

    private Label roundLabel;
    private Label stageLabel;
    private Label timeLabel;
    private Label moneyLabel;
    private Label incomeLabel;

    private Window currentTooltip;

    private Airport selectedAirport;
    private Airline selectedAirline;

    private boolean isBuyingPhase = true; // maybe delete

    private boolean isOverlayActive = false;

    public GameUIManager(Stage uiStage, MainClient client) {
        this.uiStage = uiStage;
        this.llh = client.getLlh();
        this.gameData = client.getGameData();

        createBasicWindow();
        createInvestWindow();
        createHUD();
    }

    private void createBasicWindow() {
        skin_default_window = new StyleFactory().createBasicWindow();
    }

    private void createInvestWindow() {
        skin_invest_window = new StyleFactory().createInvestWindow();
    }

    private void createHUD() {
        Table topBar = new Table();
        topBar.setFillParent(true);
        topBar.top();
        topBar.pad(15);

        Table leftStats = new Table();
        leftStats.add(showRound()).padRight(30);
        leftStats.add(showStage()).padRight(30);
        leftStats.add(showTime());

        Table rightStats = showMoneyAndIncome();

        topBar.add(leftStats).expandX().left();
        topBar.add(rightStats).expandX().right();

        uiStage.addActor(topBar);
    }

    private Table showMoneyAndIncome() {
        Table table = new Table();
        moneyLabel = new Label("Money: $" + 10000, skin_default_window); // ask
        incomeLabel = new Label("Income: +$" + 10000, skin_default_window); // ask
        incomeLabel.setColor(Color.GREEN);

        table.add(moneyLabel).left().row();
        table.add(incomeLabel).left().padTop(5).row();
        return table;
    }

    private Label showRound() {
        roundLabel = new Label("Round: " + 1, skin_default_window); // ask
        return roundLabel;
    }

    private Label showStage() {
        stageLabel = new Label("Stage: " + gameData.currentState, skin_default_window);
        return stageLabel;
    }

    private Label showTime() {
        timeLabel = new Label("Time: " + 120 + "s", skin_default_window); // ask
        timeLabel.setColor(Color.ORANGE); // Сделаем время оранжевым для красоты
        return timeLabel;
    }

    public void showAirportTooltip(Airport airport) {
        if (currentTooltip != null) currentTooltip.remove();

        selectedAirport = airport;

        currentTooltip = new Window(airport.getCityName(), skin_default_window);
        currentTooltip.pad(20);

        Table table = new Table();
        table.add(new Label("Route", skin_default_window)).padRight(10);
        table.add(new Label("Group", skin_default_window));
        table.row();

        var guestsMap = airport.getGuests();

        if (guestsMap != null) {
            Iterator<PassengerType> it = MapHolder.viewAsListIterator(guestsMap);
            PassengerType type;
            while ((type = it.next()) != null) {
                Integer groupCount = guestsMap.get(type);
                if (groupCount == null || groupCount == 0) continue;

                String passengerInfo = type.description;
                String countText = groupCount + " гр. (по " + type.size + " чел.)";

                table.add(new Label(passengerInfo, skin_default_window)).padRight(10).left();
                table.add(new Label(countText, skin_default_window)).right();
                table.row();
            }
        }
        else {
            table.add(new Label("No guests", skin_default_window)).colspan(2);
        }

        currentTooltip.add(table);
        currentTooltip.pack();
        uiStage.addActor(currentTooltip);
    }

    public void showAirlineTooltip(Airline airline, float clickX, float clickY) {
        if (currentTooltip != null) currentTooltip.remove();

        selectedAirport = null;
        selectedAirline = airline;

        currentTooltip = new Window("Route Details", skin_default_window);
        currentTooltip.pad(20);
        Table table = new Table();

        if (airline.getPlayer() != null) {
            table.add(new Label("Owned by: " + airline.getPlayer().getName(), skin_default_window));
        } else {
            TextButton buyButton = new TextButton("Buy for $" + airline.getPrice(), skin_default_window);

            if (!isBuyingPhase) {
                buyButton.setDisabled(true);
            }

            if (1000000 < airline.getPrice()) { // currentPlayerMoney, ask
                buyButton.getLabel().setColor(Color.RED);
            } else {
                buyButton.getLabel().setColor(Color.WHITE);
            }

            buyButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (buyButton.isDisabled() || 100000 < airline.getPrice()) return; // ask
                    System.out.println("Buying route!");
                }
            });

            table.add(buyButton).width(150).height(40);
        }

        currentTooltip.add(table);
        currentTooltip.pack();
        uiStage.addActor(currentTooltip);

        currentTooltip.setPosition(10, 10);
    }

    public void removeTooltip() {
        if (currentTooltip != null) {
            currentTooltip.remove();
            currentTooltip = null;
        }
    }

    public void showInvestWindow() {
        isOverlayActive = true;

        final Table overlayWindow = new Table();
        overlayWindow.setFillParent(true);
        overlayWindow.setBackground(skin_invest_window.getDrawable("blue-bg"));
        overlayWindow.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.enabled);

        Label titleLabel = new Label("Investing", skin_invest_window);
        titleLabel.setFontScale(1.5f);

        Label subtitleLabel = new Label("invest your incomes to money", skin_invest_window);

        final Slider slider = new Slider(1, 10, 1, false, skin_invest_window);
        final Label amountLabel = new Label("1", skin_invest_window);

        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                amountLabel.setText(String.valueOf((int) slider.getValue()));
            }
        });

        TextButton submitBtn = new TextButton("Submit", skin_invest_window);
        submitBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int investedAmount = (int) slider.getValue();
                llh.setNewMessage(new Network.UserInvestment(llh.getMyId(), investedAmount));
                overlayWindow.remove();
                showSuccessWindow("Income was invested successfully!");
            }
        });

        overlayWindow.add(titleLabel).padBottom(15).row();
        overlayWindow.add(subtitleLabel).padBottom(40).row();
        overlayWindow.add(slider).width(300).padBottom(10).row();
        overlayWindow.add(amountLabel).padBottom(40).row();
        overlayWindow.add(submitBtn).width(150).height(50);

        uiStage.addActor(overlayWindow);
    }

    public void showSuccessWindow(String message) {
        final Window successWindow = new Window(" Success", skin_default_window);
        successWindow.pad(30);
        successWindow.padTop(50);
        successWindow.setModal(true);
        successWindow.setMovable(false);

        Label messageLabel = new Label(message, skin_default_window);

        TextButton closeBtn = new TextButton("Close", skin_default_window);
        closeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                successWindow.remove();
                isOverlayActive = false;
            }
        });

        successWindow.add(messageLabel).padBottom(30).row();
        successWindow.add(closeBtn).width(120).height(40);

        successWindow.pack();
        float centerX = (uiStage.getWidth() - successWindow.getWidth()) / 2f;
        float centerY = (uiStage.getHeight() - successWindow.getHeight()) / 2f;
        successWindow.setPosition(centerX, centerY);

        uiStage.addActor(successWindow);
    }

    public void showAuctionWindow() {
        isOverlayActive = true;

        final Table overlayWindow = new Table();
        overlayWindow.setFillParent(true);
        overlayWindow.setBackground(skin_invest_window.getDrawable("blue-bg"));
        overlayWindow.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.enabled);

        Label titleLabel = new Label("Auction", skin_invest_window);
        titleLabel.setFontScale(1.2f);

        Label subtitleLabel = new Label("Bet more to walk first in this round", skin_invest_window);

        final Slider slider = new Slider(1, 10, 1, false, skin_invest_window);
        final Label amountLabel = new Label("1", skin_invest_window);

        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                amountLabel.setText(String.valueOf((int) slider.getValue()));
            }
        });

        TextButton betBtn = new TextButton("Bet", skin_invest_window, "default"); // Темная
        TextButton passBtn = new TextButton("Pass", skin_invest_window, "red");   // Красная

        passBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                overlayWindow.remove();
                showSuccessWindow("You left the auction");
            }
        });

        betBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                int currentBet = (int) slider.getValue();

                overlayWindow.remove();
            }
        });

        overlayWindow.add(titleLabel).colspan(2).padBottom(10).row();
        overlayWindow.add(subtitleLabel).colspan(2).padBottom(25).row();

        overlayWindow.add(slider).width(250).padRight(15);
        overlayWindow.add(amountLabel).width(40).left().row();

        Table buttonTable = new Table();
        buttonTable.add(betBtn).width(120).padRight(20);
        buttonTable.add(passBtn).width(120);

        overlayWindow.add(buttonTable).colspan(2).padTop(30).row();

        uiStage.addActor(overlayWindow);
    }

    public void showPlaneWindow() {
        isOverlayActive = true;

        final Table overlayWindow = new Table();
        overlayWindow.setFillParent(true);
        overlayWindow.setBackground(skin_invest_window.getDrawable("blue-bg"));
        overlayWindow.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.enabled);

        Label titleLabel = new Label("Purchase planes", skin_invest_window);
        titleLabel.setFontScale(1.5f);
        Label subtitleLabel = new Label("Buy new planes", skin_invest_window);

        Table planesTable = new Table();

        planesTable.add(new Label("Regional Jet", skin_invest_window)).pad(20);
        planesTable.add(new Label("Business Plane", skin_invest_window)).pad(20);
        planesTable.add(new Label("Usual Jet", skin_invest_window)).pad(20);
        planesTable.row();

        planesTable.add(new Label("$500", skin_invest_window)).padBottom(20);
        planesTable.add(new Label("$1200", skin_invest_window)).padBottom(20);
        planesTable.add(new Label("$2500", skin_invest_window)).padBottom(20);
        planesTable.row();

        TextButton buyRegBtn = new TextButton("BUY", skin_invest_window);
        TextButton buyBusBtn = new TextButton("BUY", skin_invest_window);
        TextButton buyUsuBtn = new TextButton("BUY", skin_invest_window);

        buyRegBtn.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                System.out.println("Куплен Regional Jet");
                overlayWindow.remove();
                showSuccessWindow("Regional Jet purchased successfully!");
            }
        });

        buyBusBtn.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                System.out.println("Куплен Business Plane");
                overlayWindow.remove();
                showSuccessWindow("Business Plane purchased successfully!");
            }
        });

        buyUsuBtn.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                System.out.println("Куплен Usual Jet");
                overlayWindow.remove();
                showSuccessWindow("Usual Jet purchased successfully!");
            }
        });

        planesTable.add(buyRegBtn).width(140).height(45);
        planesTable.add(buyBusBtn).width(140).height(45);
        planesTable.add(buyUsuBtn).width(140).height(45);

        TextButton closeBtn = new TextButton("Cancel", skin_invest_window);
        closeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                overlayWindow.remove();
            }
        });

        overlayWindow.add(titleLabel).padBottom(10).row();
        overlayWindow.add(subtitleLabel).padBottom(50).row();
        overlayWindow.add(planesTable).padBottom(50).row(); // Вставляем всю таблицу с самолетами целиком
        overlayWindow.add(closeBtn).width(150).height(50);

        uiStage.addActor(overlayWindow);
    }

    public boolean isOverlayActive() {
        return isOverlayActive;
    }
}
