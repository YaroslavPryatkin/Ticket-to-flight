package com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.Tooltips;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PassengerType;
import com.game.Ticket_To_Flight.commonFrontAndBack.StaticGameData;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.Flights.PassengerSelectionListener;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.MapInput.MapSelectionState;
import com.game.Ticket_To_Flight.frontend.components.buttons.RoundedButton;
import com.game.Ticket_To_Flight.frontend.components.texts.SingleLineText;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AirportTooltipWindow extends Window implements MapTooltipWindow {

    public AirportTooltipWindow(Skin skin, final Airport airport, final MapSelectionState selectionState, boolean canSelectGroup) {
        this(skin, airport, selectionState, canSelectGroup, null);
    }

    public AirportTooltipWindow(
        Skin skin,
        final Airport airport,
        final MapSelectionState selectionState,
        boolean canSelectGroup,
        final PassengerSelectionListener passengerSelectionListener
    ) {
        super(airport.getCityName(), skin);

        this.setMovable(false);
        this.pad(20);

        Table contentTable = new Table();

        Set<PassengerType> selectedGroups = new HashSet<>();

        final TextButton chooseButton = new RoundedButton("Choose groups", skin);
        chooseButton.setDisabled(true); // Выключена, пока ничего не выбрано
        chooseButton.getLabel().setColor(Color.LIGHT_GRAY);

        contentTable.add(new SingleLineText("Route", skin)).padRight(20).padBottom(15).left();
        contentTable.add(new SingleLineText("Persons", skin)).padRight(20).padBottom(15).center();
        contentTable.add(new SingleLineText("Reward", skin)).padRight(20).padBottom(15).center();
        contentTable.add(new SingleLineText("Class", skin)).padRight(20).padBottom(15).center();
        contentTable.add(new SingleLineText("Available", skin)).padRight(20).padBottom(15).center();
        contentTable.add(new SingleLineText("Choice", skin)).padLeft(10).padBottom(15).center();
        contentTable.row();

        var guestsMap = airport.getGuests();
        boolean hasGroups = false;

        if (guestsMap != null) {
            for (Map.Entry<Integer, Integer> e : guestsMap.entrySet()) {
                int passengerId = e.getKey();
                int amount = e.getValue();

                if (amount <= 0) continue;
                hasGroups = true;

                PassengerType pType = StaticGameData.passengerTypes.get(passengerId);

                String routeText = "To " + pType.typeTo.description;
                String personsText = String.valueOf(pType.size);
                String rewardText = "$" + pType.solvency;
                String classText = pType.description;
                String availableText = String.valueOf(amount);

                contentTable.add(new SingleLineText(routeText, skin)).padRight(20).padBottom(8).left();
                contentTable.add(new SingleLineText(personsText, skin)).padRight(20).padBottom(8).center();
                contentTable.add(new SingleLineText(rewardText, skin)).padRight(20).padBottom(8).center();
                contentTable.add(new SingleLineText(classText, skin)).padRight(20).padBottom(8).center();
                contentTable.add(new SingleLineText(availableText, skin)).padRight(20).padBottom(8).center();

                TextButton selectBtn = new RoundedButton("Select", skin);
                selectBtn.setDisabled(!canSelectGroup);

                selectBtn.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        if (!canSelectGroup) return;

                        if (selectedGroups.contains(pType)) {
                            selectedGroups.remove(pType);
                            selectBtn.setText("Select");
                            selectBtn.getColor().set(Color.WHITE);
                        } else {
                            // Если не выбрано -> выбираем
                            selectedGroups.add(pType);
                            selectBtn.setText("Selected");
                            selectBtn.getColor().set(Color.GREEN);
                        }

                        boolean hasSelection = !selectedGroups.isEmpty();
                        chooseButton.setDisabled(!hasSelection);
                        chooseButton.getLabel().setColor(hasSelection ? Color.WHITE : Color.LIGHT_GRAY);
                    }
                });

                contentTable.add(selectBtn).padLeft(10).padBottom(8).center();
                contentTable.row();
            }
        }

        if (!hasGroups) {
            contentTable.clearChildren();
            contentTable.add(new SingleLineText("No passengers waiting here", skin)).center().pad(20);
        }

        ScrollPane scrollPane = new ScrollPane(contentTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);

        chooseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (chooseButton.isDisabled() || selectedGroups.isEmpty()) return;

                selectionState.selectAirport(airport);

                for (PassengerType pt : selectedGroups) {
                    selectionState.selectPassengerType(pt);
                    if (passengerSelectionListener != null) {
                        passengerSelectionListener.onPassengerSelected(airport, pt);
                    }
                }

                chooseButton.setText("Groups selected");
                chooseButton.setDisabled(true);
            }
        });

        this.add(scrollPane).maxHeight(350).row();
        this.add(chooseButton).width(280).height(60).padTop(20);
        this.pack();
    }

    @Override
    public Window asWindow() {
        return this;
    }
}
