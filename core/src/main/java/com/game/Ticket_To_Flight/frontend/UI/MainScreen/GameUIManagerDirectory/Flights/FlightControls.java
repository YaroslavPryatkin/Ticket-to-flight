package com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.Flights;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.frontend.LowLevelHandlerFront;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManager;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.MapInput.MapSelectionState;
import com.game.Ticket_To_Flight.frontend.components.buttons.RoundedButton;

public class FlightControls {
    private final Stage stage;
    private final Skin skin;
    private final GameData gameData;
    private final LowLevelHandlerFront llh;
    private final MapSelectionState selectionState;
    private final GameUIManager uiManager;

    private TextButton flyButton;

    public FlightControls(Stage stage, Skin skin, GameData gameData, LowLevelHandlerFront llh, MapSelectionState selectionState, GameUIManager uiManager) {
        this.stage = stage;
        this.skin = skin;
        this.gameData = gameData;
        this.llh = llh;
        this.selectionState = selectionState;
        this.uiManager = uiManager;
    }

    public void update() {
        if (gameData.currentState != GameData.State.FLIGHTS) {
            remove();
            selectionState.clearFlightSelection();
            return;
        }

        ensureButton();
        updateButtonState();
        position();
    }

    public void position() {
        if (flyButton == null) return;
        flyButton.setSize(260, 80);
        flyButton.setPosition((stage.getWidth() - flyButton.getWidth()) / 2f, 40);
    }

    private void ensureButton() {
        if (flyButton != null) return;

        flyButton = new RoundedButton("Fly", skin);
        flyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sendFlightRequest();
            }
        });
        stage.addActor(flyButton);
    }

    private void updateButtonState() {
        boolean isMyTurn = gameData.currentPlayer == llh.getMyId();
        if (!isMyTurn) {
            selectionState.clearFlightSelection();
        }

        boolean canFly = isMyTurn && selectionState.hasCompleteFlightSelection();
        flyButton.setDisabled(!canFly);
        flyButton.getLabel().setColor(canFly ? Color.WHITE : Color.LIGHT_GRAY);
    }

    private void sendFlightRequest() {
        if (flyButton.isDisabled() || !selectionState.hasCompleteFlightSelection()) return;

        llh.sendRouteResponse(
            selectionState.getSelectedAirportId(),
            selectionState.getSelectedAirlineId(),
            selectionState.getSelectedPassengerTypeId()
        );
        selectionState.clearFlightSelection();
        uiManager.removeTooltip();
        uiManager.showSuccessWindow("Flight request was sent to server");
    }

    private void remove() {
        if (flyButton == null) return;
        flyButton.remove();
        flyButton = null;
    }
}
