package com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.HUD.HUDDirectory;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PassengerType;

import java.util.function.Consumer;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport; // Не забудьте импорт, если используете переменную
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PlaneType;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.commonFrontAndBack.Route;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.Flights.MainFlightController;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.HUD.HUDDirectory.FlightHUDDirectory.BoardingHUD;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.HUD.HUDDirectory.FlightHUDDirectory.GroupHUD;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.HUD.HUDDirectory.FlightHUDDirectory.PlaneHUD;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.HUD.HUDDirectory.FlightHUDDirectory.StandardHUD;

public class FlightHUD extends Table {
    private final StandardHUD standardHUD;
    private final PlaneHUD planeHUD;
    private final GroupHUD groupHUD;
    private final BoardingHUD boardingHUD;

    public FlightHUD(Skin skin) {
        setFillParent(true);
        setTouchable(Touchable.childrenOnly);
        standardHUD = new StandardHUD(skin);
        planeHUD = new PlaneHUD(skin);
        groupHUD = new GroupHUD(skin);
        boardingHUD = new BoardingHUD(skin);

        addActor(standardHUD);
        addActor(planeHUD);
        addActor(groupHUD);
        addActor(boardingHUD);

        setVisible(false);
    }

    public void setCallbacks(Runnable onReset, Runnable onBack, Runnable onPass, Consumer<Boolean> onFinish) {
        standardHUD.setCallbacks(onReset, onBack, onPass, onFinish);
    }

    public void setPassengerCallbacks(Consumer<PassengerType> onSelect, Consumer<PassengerType> onRemove) {
        boardingHUD.setCallbacks(onSelect);
        groupHUD.setCallbacks(onRemove);
    }

    public void forcePassengerUpdate() {
        boardingHUD.forceUpdate();
        groupHUD.forceUpdate();
    }

    public void updateData(Integer playerId, MainFlightController.Step step, PlaneType plane, Route route) {
        standardHUD.updateData(playerId, step, route);
        planeHUD.updateData(plane, route);
        if(step == MainFlightController.Step.IN_FLIGHT) {
            groupHUD.updateData(route);
            Airport currentAirport = route.getCurrentAirport();
            boardingHUD.updateData(currentAirport, route);
        }

        layoutChildren();
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        standardHUD.setVisible(visible);
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

        standardHUD.setBounds(0, 0, width, height);
        standardHUD.layoutFor(width, height);

        planeHUD.layoutFor(width, height, standardHUD.getSummaryBottomY());
        groupHUD.layoutFor(width, height, planeHUD.getY());

        boardingHUD.layoutFor(width, height, groupHUD.getY());
    }
}
