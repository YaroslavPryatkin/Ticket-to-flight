package com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.HUD.HUDDirectory;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PassengerType;

import java.util.function.Consumer;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport; // Не забудьте импорт, если используете переменную
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PlaneType;
import com.game.Ticket_To_Flight.commonFrontAndBack.Route;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.Flights.MainFlightController;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.HUD.HUDDirectory.FlightHUDDirectory.BoardingHUD;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.HUD.HUDDirectory.FlightHUDDirectory.GroupHUD;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.HUD.HUDDirectory.FlightHUDDirectory.PlaneHUD;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.HUD.HUDDirectory.FlightHUDDirectory.FlightHudCenterDownButtons;

public class FlightHUD extends Table {
    private final FlightHudCenterDownButtons flightHudCenterDownButtons;
    private final PlaneHUD planeHUD;
    private final GroupHUD groupHUD;
    private final BoardingHUD boardingHUD;

    public FlightHUD(Skin skin) {
        setFillParent(true);
        setTouchable(Touchable.childrenOnly);
        flightHudCenterDownButtons = new FlightHudCenterDownButtons(skin);
        planeHUD = new PlaneHUD(skin);
        groupHUD = new GroupHUD(skin);
        boardingHUD = new BoardingHUD(skin);

        addActor(flightHudCenterDownButtons);
        addActor(planeHUD);
        addActor(groupHUD);
        addActor(boardingHUD);

        setVisible(false);
    }

    public void setCallbacks(Runnable onReset, Runnable onBack, Runnable onPass, Consumer<Boolean> onFinish) {
        flightHudCenterDownButtons.setCallbacks(onReset, onBack, onPass, onFinish);
    }

    public void setPassengerCallbacks(Consumer<PassengerType> onSelect, Consumer<Integer> onRemove) {
        boardingHUD.setCallbacks(onSelect);
        groupHUD.setCallbacks(onRemove);
    }

    public void updateData(PlaneType selectedPlane, Integer playerId, MainFlightController.Step step, Route route) {
        if(step == MainFlightController.Step.SELECT_PLANE){
            planeHUD.updateData(null, null);
            flightHudCenterDownButtons.updateData(playerId, step, null);
            groupHUD.updateData(null);
            boardingHUD.updateData(null);

            layoutChildren();
        }
        else if(step == MainFlightController.Step.CHOOSING_STARTING_AIRPORT){
            planeHUD.updateData(selectedPlane, null);
            flightHudCenterDownButtons.updateData(playerId, step, null);
            groupHUD.updateData(null);
            boardingHUD.updateData(null);

            layoutChildren();
        }
        else{
            if (route.renderingUpdate()) {
                planeHUD.updateData(route.getPlane(), route);
                flightHudCenterDownButtons.updateData(playerId, step, route);
                groupHUD.updateData(route);
                boardingHUD.updateData(route);

                layoutChildren();
            }
        }
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        flightHudCenterDownButtons.setVisible(visible);
        planeHUD.setVisible(visible);
        groupHUD.setVisible(visible);
        boardingHUD.setVisible(visible);
    }

    @Override
    public void layout() {
        super.layout();
        layoutChildren();
    }

    private void layoutChildren() {
        float width = getStage() == null ? getWidth() : getStage().getWidth();
        float height = getStage() == null ? getHeight() : getStage().getHeight();

        flightHudCenterDownButtons.setBounds(0, 0, width, height);
        flightHudCenterDownButtons.layoutFor(width, height);

        planeHUD.layoutFor(width, height, flightHudCenterDownButtons.getSummaryBottomY());
        groupHUD.layoutFor(width, height, planeHUD.getY());

        boardingHUD.layoutFor(width, height, groupHUD.getY());
    }
}
