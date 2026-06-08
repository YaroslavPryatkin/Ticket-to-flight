package com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.Airlines;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airline;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.frontend.LowLevelHandlerFront;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManager;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.Tooltips.ContextMenuWithButtons;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.MapInput.ContextMenuPositionManager;
import com.game.Ticket_To_Flight.frontend.components.buttons.RoundedButton;

public class AirlinesControls {
    private final Stage uiStageHUD;
    private final Skin skin;
    private final GameData gameData;
    private final LowLevelHandlerFront llh;
    private final GameUIManager gameUIManager;
    private final OrthographicCamera camera;
    private ContextMenuWithButtons currentTooltip;

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

        currentTooltip = new ContextMenuWithButtons(skin, gameUIManager, airline, currentPlayerMoney, currentPlayerAP, isAvailable, canBuyDuringCurrentStage, llh);
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
        ContextMenuPositionManager.updateTooltipPosition(currentTooltip, currentAirline, uiStageHUD, camera);
        ContextMenuPositionManager.clearTooltipScrollFocusWhenPointerLeaves(currentTooltip, uiStageHUD);
    }

    public boolean isPointerOverTooltip() {
        return ContextMenuPositionManager.isPointerOverTooltip(currentTooltip);
    }

}
