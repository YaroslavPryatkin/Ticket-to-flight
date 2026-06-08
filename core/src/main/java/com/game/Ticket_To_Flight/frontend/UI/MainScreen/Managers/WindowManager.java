package com.game.Ticket_To_Flight.frontend.UI.MainScreen.Managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
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
        openWindow(new AuctionWindow(investSkin, facade, llh, gameData));
        return true;
    }

    public boolean showInvestWindow() {
        openWindow(new InvestWindow(investSkin, facade, llh, gameData));
        return true;
    }

    public void showNotificationWindow(String message) {
        openWindow(new NotificationWindow(defaultSkin, facade, message));
    }

    public boolean showAbilitiesWindow() {
        openWindow(new AbilitiesWindow(investSkin, facade, llh, gameData));
        return true;
    }

    public boolean showPlaneWindow() {
        openWindow(new PlaneWindow(investSkin, facade, llh, gameData));
        return true;
    }

    public boolean showFinishWindow() {
        openWindow(new FinishGameWindow(investSkin, gameData, llh));
        return true;
    }

    private void openWindow(Window window) {
        closeCurrentWindow();
        isWindowOpen = true;
        currentWindow = window;
        uiStageWindow.addActor(window);
        centerCurrentWindow();
        isWindowOpen = true;
    }



    public void setWindowOpen(boolean windowOpen) {
        if (!windowOpen) {
            closeCurrentWindow();
        }
        this.isWindowOpen = windowOpen;
    }

    public boolean isWindowOpen() {
        return isWindowOpen;
    }

    public boolean isPointerOverCurrentWindow() {
        if (currentWindow == null) return false;

        Vector2 stageCoords = uiStageWindow.screenToStageCoordinates(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
//        if(currentWindow instanceof SuccessWindow) {
//            System.out.println("min x = " + currentWindow.getX() + " x = " + stageCoords.x + " max x = " + currentWindow.getX() + currentWindow.getWidth());
//            System.out.println("min y = " + currentWindow.getY() + " y = " + stageCoords.y + " max y = " + currentWindow.getY() + currentWindow.getHeight());
//        }
        return stageCoords.x >= currentWindow.getX() &&
            stageCoords.x <= currentWindow.getX() + currentWindow.getWidth() &&
            stageCoords.y >= currentWindow.getY() &&
            stageCoords.y <= currentWindow.getY() + currentWindow.getHeight();
    }
}
