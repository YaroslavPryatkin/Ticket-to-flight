package com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.Tooltips;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PassengerType;
import com.game.Ticket_To_Flight.commonFrontAndBack.StaticGameData;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.Flights.MainFlightController;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.Flights.PassengerSelectionListener;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.Tooltips.AirportDirectory.AirportPassengerTableWidget;
import com.game.Ticket_To_Flight.frontend.components.tables.PassengerTableWidget;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.MapInput.MapSelectionState;
import com.game.Ticket_To_Flight.frontend.components.buttons.RoundedButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AirportTooltipWindow extends Window implements MapTooltipWindow {

    public AirportTooltipWindow(
        Skin skin,
        final Airport airport,
        final MapSelectionState selectionState,
        boolean canSelectGroup,
        final PassengerSelectionListener passengerSelectionListener,
        MainFlightController mainFlightController
    ) {
        super(airport.getCityName(), skin);
        this.setMovable(false);
        this.top().left();
        this.pad(30);
        this.padTop(60);

        this.getColor().a = 0.8f;

        final TextButton chooseButton = new RoundedButton("Choose groups", skin);
        chooseButton.setDisabled(true);
        chooseButton.getLabel().setColor(Color.LIGHT_GRAY);

        final AirportPassengerTableWidget[] contentTableRef = new AirportPassengerTableWidget[1];
        final AirportPassengerTableWidget contentTable = new AirportPassengerTableWidget(skin, canSelectGroup, mainFlightController.getRoute(), () -> {
            boolean hasSelection = contentTableRef[0] != null && contentTableRef[0].hasSelection();
            chooseButton.setDisabled(!hasSelection);
            chooseButton.getLabel().setColor(hasSelection ? Color.WHITE : Color.LIGHT_GRAY);
        });
        contentTableRef[0] = contentTable;

        var guestsMap = airport.getGuests();
        List<PassengerTableWidget> rows = new ArrayList<>();

        if (guestsMap != null) {
            for (Map.Entry<Integer, Integer> e : guestsMap.entrySet()) {
                int passengerId = e.getKey();
                int amount = e.getValue();

                if (amount <= 0) continue;

                final PassengerType pType = StaticGameData.passengerTypes.get(passengerId);
                for (int i = 0; i < amount; i++) {
                    rows.add(new PassengerTableWidget(pType));
                }
            }
        }

        contentTable.setRows(rows);

        ScrollPane scrollPane = new ScrollPane(contentTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(false, false);
        scrollPane.setForceScroll(true, true);
        contentTable.top().left();

        this.add(scrollPane).left().width(650).maxHeight(400).padTop(15).row();
        this.add(chooseButton).left().width(300).height(60).padTop(25);

        this.pack();
        scrollPane.layout();
        scrollPane.setScrollPercentX(0f);
        scrollPane.setScrollPercentY(0f);
        scrollPane.updateVisualScroll();

        chooseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (chooseButton.isDisabled() || !contentTable.hasSelection()) return;

                selectionState.selectAirport(airport);

                for (PassengerType pt : contentTable.getSelectedPassengers()) {
                    selectionState.selectPassengerType(pt);
                    if (passengerSelectionListener != null) {
                        passengerSelectionListener.onPassengerSelected(airport, pt);
                    }
                }

                chooseButton.setText("Groups selected");
                chooseButton.setDisabled(true);
            }
        });
    }

    @Override
    public Window asWindow() {
        return this;
    }
}
