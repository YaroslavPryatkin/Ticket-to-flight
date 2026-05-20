package com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
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
import com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.GameUIManagerDirectory.*;
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
    private HUDOverlay hudOverlay;

    private boolean isBuyingPhase = true; // maybe delete

    private boolean isOverlayActive = false;

    public GameUIManager(Stage uiStage, MainClient client) {
        this.uiStage = uiStage;
        this.llh = client.getLlh();
        this.gameData = client.getGameData();


        createBasicWindow();
        createInvestWindow();

        this.hudOverlay = new HUDOverlay(skin_default_window);
        this.uiStage.addActor(hudOverlay);
    }

    private void createBasicWindow() {
        skin_default_window = new StyleFactory().createBasicWindow();
    }

    private void createInvestWindow() {
        skin_invest_window = new StyleFactory().createInvestWindow();
    }

    public void updateHUDData() {
        if (hudOverlay != null) {
            int round = 1;
            String stage = gameData.currentState.toString();
            int time = 120;
            double money = 10000; // ask
            double income = 10000;// ask

            hudOverlay.updateHUD(round, stage, time, money, income);
        }
    }

    public void showAirportTooltip(Airport airport) {
        removeTooltip();

        selectedAirline = null;
        selectedAirport = airport;
        currentTooltip = new AirportTooltipWindow(skin_default_window, airport);

        uiStage.addActor(currentTooltip);
    }

    public void showAirlineTooltip(Airline airline, float clickX, float clickY) {
        removeTooltip();

        selectedAirport = null;
        selectedAirline = airline;

        double currentPlayerMoney = 1000000;
        boolean currentBuyingPhase = this.isBuyingPhase;

        currentTooltip = new AirlineTooltipWindow(skin_default_window, this, airline, currentPlayerMoney, currentBuyingPhase);

        uiStage.addActor(currentTooltip);

        Vector2 stageCoords = new Vector2(clickX, clickY);
        uiStage.screenToStageCoordinates(stageCoords);

        currentTooltip.setPosition(stageCoords.x + 10, stageCoords.y + 10);
    }

    public void removeTooltip() {
        if (currentTooltip != null) {
            currentTooltip.remove();
            currentTooltip = null;
        }
    }

    public void showAuctionWindow() {
        uiStage.addActor(new AuctionWindow(skin_invest_window, this));
    }

    public void showInvestWindow() {
        uiStage.addActor(new InvestWindow(skin_invest_window, this, llh));
    }

    public void showSuccessWindow(String message) {
        isOverlayActive = true;

        SuccessWindow successWindow = new SuccessWindow(skin_default_window, this, "Success");

        float centerX = (uiStage.getWidth() - successWindow.getWidth()) / 2f;
        float centerY = (uiStage.getHeight() - successWindow.getHeight()) / 2f;
        successWindow.setPosition(centerX, centerY);

        uiStage.addActor(successWindow);
    }

    public void showAbilitiesWindow() {
        uiStage.addActor(new AbilitiesWindow(skin_invest_window, this, llh));
    }

    public void showPlaneWindow() {
        uiStage.addActor(new PlaneWindow(skin_invest_window, this));
    }

    public boolean isOverlayActive() {
        return isOverlayActive;
    }

    public void setOverlayActive(boolean b) {
        isOverlayActive = b;
    }
}
