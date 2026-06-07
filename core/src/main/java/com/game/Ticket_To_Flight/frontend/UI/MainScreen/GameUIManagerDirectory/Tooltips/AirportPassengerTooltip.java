package com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.Tooltips;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PassengerType;
import com.game.Ticket_To_Flight.commonFrontAndBack.Route;
import com.game.Ticket_To_Flight.frontend.components.buttons.SelectButton;

import java.util.function.Consumer;

public class AirportPassengerTooltip extends AirportHoverTooltip {

    private final Route currentRoute;
    private final Consumer<PassengerType> onPassengerSelected;

    public AirportPassengerTooltip(Skin skin, Airport airport, Route currentRoute, Consumer<PassengerType> onPassengerSelected) {
        super(skin, airport);
        this.currentRoute = currentRoute;
        this.onPassengerSelected = onPassengerSelected;

        getTitleLabel().setColor(Color.ORANGE);
        getTitleLabel().setText("Boarding: " + airport.getCityName());
    }

    @Override
    protected void addExtraPassengerUI(Table passengerContent, PassengerType passenger, Skin skin) {
        SelectButton selectBtn = new SelectButton("Select", skin, () -> {
            if (onPassengerSelected != null) {
                onPassengerSelected.accept(passenger);
            }
        });

        boolean canAdd = (currentRoute.checkPassengerAdding(passenger, 1) == null);
        selectBtn.setDisabled(!canAdd);

        selectBtn.getLabel().setColor(canAdd ? Color.WHITE : Color.DARK_GRAY);

        passengerContent.add(selectBtn).left().padTop(12).row();
    }
}
