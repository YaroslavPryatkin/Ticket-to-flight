package com.game.Ticket_To_Flight.frontend.UI.MainScreen.Managers;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.frontend.LowLevelHandlerFront;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManager;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.GameStageWindows.SubWindows.*;
import com.game.Ticket_To_Flight.frontend.components.windows.BaseGameWindow;

public class WindowManager {
    private final Stage uiStageWindow;
    private final Skin defaultSkin;
    private final Skin investSkin;
    private final GameUIManager facade;
    private final GameData gameData;
    private final LowLevelHandlerFront llh;

    private Window currentWindow;
    private boolean isWindowOpen = false;

    public WindowManager(Stage uiStageWindow, Skin defaultSkin, Skin investSkin, GameUIManager facade, GameData gameData, LowLevelHandlerFront llh) {
        this.uiStageWindow = uiStageWindow;
        this.defaultSkin = defaultSkin;
        this.investSkin = investSkin;
        this.facade = facade;
        this.gameData = gameData;
        this.llh = llh;
    }

    private void closeCurrentWindow() {
        if (currentWindow != null) {
            currentWindow.remove();
            currentWindow = null;
        }
        uiStageWindow.setScrollFocus(null);
        isWindowOpen = false;
    }

    public void centerCurrentWindow() {
        if (currentWindow != null) {
            currentWindow.setPosition(
                (uiStageWindow.getWidth() - currentWindow.getWidth()) / 2f,
                (uiStageWindow.getHeight() - currentWindow.getHeight()) / 2f
            );
            if (currentWindow instanceof BaseGameWindow) {
                ((BaseGameWindow) currentWindow).updateScrollFocusUnderMouse();
            }
        }
    }

    public boolean showAuctionWindow() {
        if (isWindowOpen) return false;

        currentWindow = new AuctionWindow(investSkin, facade, llh, gameData);
        openWindow(currentWindow);
        return true;
    }

    public boolean showInvestWindow() {
        if (isWindowOpen) return false;

        currentWindow = new InvestWindow(investSkin, facade, llh, gameData);
        openWindow(currentWindow);
        return true;
    }

    public void showSuccessWindow(String message) {
        closeCurrentWindow();
        currentWindow = new SuccessWindow(defaultSkin, facade, message);
        openWindow(currentWindow);
    }

    public boolean showAbilitiesWindow() {
        if (isWindowOpen) return false;

        currentWindow = new AbilitiesWindow(investSkin, facade, llh, gameData);
        openWindow(currentWindow);
        return true;
    }

    public boolean showPlaneWindow() {
        if (isWindowOpen) return false;

        currentWindow = new PlaneWindow(investSkin, facade, llh, gameData);
        openWindow(currentWindow);
        return true;
    }

    private void openWindow(Window window) {
        uiStageWindow.addActor(window);
        centerCurrentWindow();
        isWindowOpen = true;
    }

    public void setWindowOpen(boolean windowOpen) {
        if (!windowOpen) {
            closeCurrentWindow();
            return;
        }
        this.isWindowOpen = windowOpen;
    }

    public boolean isWindowOpen() {
        return isWindowOpen;
    }
}
