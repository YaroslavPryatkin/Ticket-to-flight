package com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.Airlines;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airline;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.frontend.LowLevelHandlerFront;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManager;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.Tooltips.AirlineClickTooltip;
import com.game.Ticket_To_Flight.frontend.components.buttons.RoundedButton;
import com.game.Ticket_To_Flight.frontend.components.subsidiary.ComponentHover;

public class AirlinesControls {
    private final Stage uiStageHUD;
    private final Skin skin;
    private final GameData gameData;
    private final LowLevelHandlerFront llh;
    private final GameUIManager gameUIManager;
    private final OrthographicCamera camera;
    private AirlineClickTooltip currentTooltip;

    private boolean active = false;

    private TextButton passButton;

    public AirlinesControls(Stage uiStageHUD, Skin skin,
                            GameData gameData, LowLevelHandlerFront llh,
                            GameUIManager gameUIManager, OrthographicCamera camera) {
        this.uiStageHUD = uiStageHUD;
        this.skin = skin;
        this.gameData = gameData;
        this.llh = llh;
        this.gameUIManager = gameUIManager;
        this.camera = camera;
    }

    public void setActive(){
        active = true;
    }

    public void updatePassButton() {
        if(active)
            ensureButtons();
        positionPassButton();
    }

    public void positionPassButton() {
        if (passButton == null) return;

        float width = 280f;
        float height = 80f;
        float margin = 40f;

        passButton.setSize(width, height);
        passButton.setPosition((uiStageHUD.getWidth() - width) / 2, margin);
    }

    private void ensureButtons() {
        if (passButton == null) {
            passButton = createPassButton();
            uiStageHUD.addActor(passButton);
        }
    }

    private TextButton createPassButton() {
        TextButton button = new RoundedButton("Pass", skin, "default");

        button.setColor(Color.RED);

        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                llh.sendAirlinePass();
                gameUIManager.showNotificationWindow("Airlines stage is finished for you.");
            }
        });
        return button;
    }

    public void setInactive() {
        if(active) {
            if (passButton != null) {
                passButton.remove();
                passButton = null;
            }
            removeTooltip();
            active = false;
        }
    }



    public void showTooltip(Airline airline) {

        removeTooltip();
        double currentPlayerMoney = gameData.players.get(llh.getMyId()).getMoney();
        boolean currentPlayerAP = gameData.players.get(llh.getMyId()).actionPoints > 0;
        boolean isAvailable = airline.portA.getFreeGates()>=airline.type.gateA && airline.portB.getFreeGates() >= airline.type.gateB;
        boolean canBuyDuringCurrentStage = gameData.currentState == GameData.State.AIRLINES;
        if (!canBuyDuringCurrentStage) return;

        currentTooltip = new AirlineClickTooltip(skin, gameUIManager, airline, currentPlayerMoney, currentPlayerAP, isAvailable, canBuyDuringCurrentStage, llh);
        uiStageHUD.addActor(currentTooltip.asWindow());
        updateTooltip(airline);
    }

    public void removeTooltip() {
        if (currentTooltip != null) {
            currentTooltip.asWindow().remove();
            currentTooltip = null;
        }
    }

    public void updateTooltip(Airline currentAirline){
        if(!active || currentTooltip == null || currentAirline == null) return;
        updateTooltipPosition(currentAirline);
        clearTooltipScrollFocusWhenPointerLeaves();
    }

    private void updateTooltipPosition(Airline airline) {
        if (currentTooltip == null) return;

        var window = currentTooltip.asWindow();

        window.pack();
        float x;
        float y;
        if (airline != null) {
            float worldX = (airline.getPortA().getX() + airline.getPortB().getX()) / 2f;
            float worldY = (airline.getPortA().getY() + airline.getPortB().getY()) / 2f;

            Vector3 screenCoords = new Vector3(worldX, worldY, 0f);
            camera.project(screenCoords);
            Vector2 stageCoords = uiStageHUD.screenToStageCoordinates(new Vector2(screenCoords.x, screenCoords.y));

            x = stageCoords.x - window.getWidth() / 2f;
            y = stageCoords.y + 3;

            x = Math.clamp(x, 20f, uiStageHUD.getWidth() - window.getWidth() - 20f);
            y = Math.clamp(y, 20f, uiStageHUD.getHeight() - window.getHeight() - 20f);
        }
        else {
            x = 20f;
            y = 20f;
        }
        window.setPosition(x, y);
        float paddingTop = 20f;
        float maxHeight = uiStageHUD.getHeight() - y - paddingTop;
        if (window.getHeight() > maxHeight) {
            window.setHeight(maxHeight);
        }
    }

    private void clearTooltipScrollFocusWhenPointerLeaves() {
        Actor scrollFocus = uiStageHUD.getScrollFocus();
        if (scrollFocus == null || isPointerOverTooltip()) return;

        Window clickWindow = currentTooltip == null ? null : currentTooltip.asWindow();
        if (isDescendantOf(scrollFocus, clickWindow)) {
            uiStageHUD.setScrollFocus(null);
        }
    }

    public boolean isPointerOverTooltip() {
        return isPointerOverWindow(currentTooltip == null ? null : currentTooltip.asWindow());
    }

    private boolean isPointerOverWindow(Window window) {
        return window != null && window.getStage() != null && ComponentHover.isMouseOver(window);
    }

    private boolean isDescendantOf(Actor actor, Window window) {
        return actor != null && window != null && (actor == window || window.isAscendantOf(actor));
    }
}
