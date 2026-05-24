package com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airline;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.frontend.LowLevelHandlerFront;
import com.game.Ticket_To_Flight.frontend.MainClient;
import com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.GameUIManagerDirectory.*;
import com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.GameUIManagerDirectory.GameStageWindows.*;

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

    private Window window;

    private boolean blueWindowPrinted = false;

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

    private void setPositionForWindow(Window window) {
        window.setPosition(
            (uiStage.getWidth() - window.getWidth()) / 2f,
            (uiStage.getHeight() - window.getHeight()) / 2f
        );
    }

    public void updateHUDData() {
        if (hudOverlay != null) {
            int round = gameData.roundNumber;
            String stage = gameData.currentState.toString();
            int time = 120;
            double money = gameData.players.get(llh.getMyId()).getMoney();
            double income = gameData.players.get(llh.getMyId()).getIncome();

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

    public void showAirlineTooltip(Airline airline) {
        removeTooltip();

        selectedAirport = null;
        selectedAirline = airline;

        double currentPlayerMoney = 1000000;
        boolean currentBuyingPhase = (gameData.currentState == GameData.State.AIRLINES);

        currentTooltip = new AirlineTooltipWindow(skin_default_window, this, airline, currentPlayerMoney, currentBuyingPhase, llh);

        uiStage.addActor(currentTooltip);

        Vector2 stageCoords = new Vector2(10, 10);
        uiStage.screenToStageCoordinates(stageCoords);

        currentTooltip.setPosition(stageCoords.x + 10, stageCoords.y + 10);
    }

    public void removeTooltip() {
        if (currentTooltip != null) {
            currentTooltip.remove();
            currentTooltip = null;
        }
    }

    public boolean showAuctionWindow() {
        if (blueWindowPrinted)
            return false;
        if (window != null) {
            window.remove();
        }

        window = new AuctionWindow(skin_invest_window, this, llh, gameData);
        uiStage.addActor(window);

        setPositionForWindow(window);
        blueWindowPrinted = true;
        return true;
    }

    public boolean showInvestWindow() {
        if (blueWindowPrinted)
            return false;
        if (window != null) {
            window.remove();
        }

        window = new InvestWindow(skin_invest_window, this, llh);
        uiStage.addActor(window);

        setPositionForWindow(window);
        blueWindowPrinted = true;
        return true;
    }

    public void showSuccessWindow(String message) {
        if (window != null) {
            window.remove();
        }

        window = new SuccessWindow(skin_default_window, this, message);
        uiStage.addActor(window);

        this.setPositionForWindow(window);
    }

    public boolean showAbilitiesWindow() {
        if (blueWindowPrinted)
            return false;
        if (window != null) {
            window.remove();
        }

        window = new AbilitiesWindow(skin_invest_window, this, llh, gameData);
        uiStage.addActor(window);

        setPositionForWindow(window);
        blueWindowPrinted = true;
        return true;
    }

    public boolean showPlaneWindow() {
        if (blueWindowPrinted)
            return false;
        if (window != null) {
            window.remove();
        }

        window = new PlaneWindow(skin_invest_window, this, llh, gameData);
        uiStage.addActor(window);

        setPositionForWindow(window);
        blueWindowPrinted = true;
        return true;
    }

    public void setBlueWindowPrinted(boolean blueWindowPrinted) {
        this.blueWindowPrinted = blueWindowPrinted;
    }

    public void resize(int width, int height) {
        if (window != null && window.getStage() != null) {
            setPositionForWindow(window);
        }
    }
}
