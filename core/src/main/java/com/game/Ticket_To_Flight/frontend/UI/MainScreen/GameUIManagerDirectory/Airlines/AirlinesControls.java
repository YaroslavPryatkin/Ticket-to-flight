package com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.Airlines;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.frontend.LowLevelHandlerFront;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManager;
import com.game.Ticket_To_Flight.frontend.components.buttons.RoundedButton;

public class AirlinesControls {
    private final Stage stage;
    private final Skin skin;
    private final GameData gameData;
    private final LowLevelHandlerFront llh;
    private final GameUIManager uiManager;

    private TextButton passButton;
    private TextButton finishButton;
    private boolean finishAfterPurchase = false;

    public AirlinesControls(Stage stage, Skin skin, GameData gameData, LowLevelHandlerFront llh, GameUIManager uiManager) {
        this.stage = stage;
        this.skin = skin;
        this.gameData = gameData;
        this.llh = llh;
        this.uiManager = uiManager;
    }

    public void update() {
        if (!canChooseAirline()) {
            remove();
            resetFinishChoice();
            return;
        }

        ensureButtons();
        position();
    }

    public void position() {
        if (passButton == null || finishButton == null) return;

        float width = 360;
        float height = 80;
        float margin = 40;
        float gap = 20;

        passButton.setSize(width, height);
        finishButton.setSize(width, height);
        passButton.setPosition(stage.getWidth() - width - margin, margin);
        finishButton.setPosition(stage.getWidth() - width - margin, margin + height + gap);
    }

    public boolean shouldFinishAfterPurchase() {
        return finishAfterPurchase;
    }

    public void resetFinishChoice() {
        finishAfterPurchase = false;
        if (finishButton != null) {
            finishButton.setChecked(false);
        }
    }

    private boolean canChooseAirline() {
        return gameData.currentState == GameData.State.AIRLINES &&
            gameData.currentPlayer == llh.getMyId() &&
            llh.getCurrentStateState() != LowLevelHandlerFront.Flags.CurrentStateState.WAITING_FOR_SERVER_RESPONSE;
    }

    private void ensureButtons() {
        if (passButton == null) {
            passButton = createPassButton();
            stage.addActor(passButton);
        }
        if (finishButton == null) {
            finishButton = createFinishButton();
            stage.addActor(finishButton);
        }
    }

    private TextButton createPassButton() {
        TextButton button = new RoundedButton("Pass", skin, "default");
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                llh.sendAirlinePass();
                resetFinishChoice();
                uiManager.removeTooltip();
                uiManager.showSuccessWindow("Airlines stage is finished for you.");
            }
        });
        return button;
    }

    private TextButton createFinishButton() {
        TextButton button = new RoundedButton("Finish the purchase", skin, "default");
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                finishAfterPurchase = !finishAfterPurchase;
                finishButton.setChecked(finishAfterPurchase);
            }
        });
        return button;
    }

    private void remove() {
        if (passButton != null) {
            passButton.remove();
            passButton = null;
        }
        if (finishButton != null) {
            finishButton.remove();
            finishButton = null;
        }
    }
}
