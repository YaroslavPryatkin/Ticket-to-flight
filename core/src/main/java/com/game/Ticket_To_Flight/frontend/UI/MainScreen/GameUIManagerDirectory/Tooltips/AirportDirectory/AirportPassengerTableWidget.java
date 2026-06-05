package com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.Tooltips.AirportDirectory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PassengerType;
import com.game.Ticket_To_Flight.commonFrontAndBack.Route;
import com.game.Ticket_To_Flight.frontend.components.tables.passenger.AbstractPassengerTableWidget;
import com.game.Ticket_To_Flight.frontend.components.buttons.RoundedButton;
import com.game.Ticket_To_Flight.frontend.components.tables.passenger.PassengerTableWidget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AirportPassengerTableWidget extends AbstractPassengerTableWidget {
    private final boolean canSelectGroup;
    private final Runnable onSelectionChanged;
    private final List<PassengerType> selectedPassengers = new ArrayList<>();

    private Route route;
    private final List<ButtonRecord> buttonRecords = new ArrayList<>();

    private static class ButtonRecord {
        TextButton button;
        PassengerType passengerType;
        boolean isSelected = false;

        ButtonRecord(TextButton button, PassengerType passengerType) {
            this.button = button;
            this.passengerType = passengerType;
        }
    }

    public AirportPassengerTableWidget(Skin skin, boolean canSelectGroup, Route route, Runnable onSelectionChanged) {
        super(skin, true);
        this.canSelectGroup = canSelectGroup;
        this.onSelectionChanged = onSelectionChanged;
        this.route = route;

        updateAllButtonsState();
    }

    public List<PassengerType> getSelectedPassengers() {
        return Collections.unmodifiableList(selectedPassengers);
    }

    public boolean hasSelection() {
        return !selectedPassengers.isEmpty();
    }

    @Override
    protected Actor createChoiceActor(PassengerTableWidget row) {
        final PassengerType passengerType = row.passengerType;
        final TextButton selectBtn = new RoundedButton("Select", skin);

        final ButtonRecord record = new ButtonRecord(selectBtn, passengerType);
        buttonRecords.add(record);

        if (!canSelectGroup) {
            selectBtn.setDisabled(true);
            selectBtn.getLabel().setColor(Color.DARK_GRAY);
        } else if (route != null) {
            boolean canAdd;
            try {
                canAdd = (route.checkPassengerAdding(passengerType, selectedPassengers) == null);
            } catch (Exception e) {
                canAdd = false;
            }

            if (!canAdd) {
                selectBtn.setDisabled(true);
                selectBtn.getLabel().setColor(Color.DARK_GRAY);
            } else {
                selectBtn.setDisabled(false);
                selectBtn.getLabel().setColor(Color.WHITE);
            }
        }

        selectBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!canSelectGroup || selectBtn.isDisabled()) return;

                record.isSelected = !record.isSelected;
                if (record.isSelected) {
                    selectedPassengers.add(passengerType);
                    selectBtn.setText("Selected");
                    selectBtn.getColor().set(Color.GREEN);
                } else {
                    selectedPassengers.remove(passengerType);
                    selectBtn.setText("Select");
                    selectBtn.getColor().set(Color.WHITE);
                }

                updateAllButtonsState();

                if (onSelectionChanged != null) {
                    onSelectionChanged.run();
                }
            }
        });

        return selectBtn;
    }

    private void updateAllButtonsState() {
        if (!canSelectGroup) return;

        for (ButtonRecord record : buttonRecords) {
            if (record.isSelected) continue;

            boolean canAdd;
            try {
                canAdd = (route.checkPassengerAdding(record.passengerType, selectedPassengers) == null);
            } catch (Exception e) {
                canAdd = false;
            }

            if (!canAdd) {
                record.button.setDisabled(true);
                record.button.getLabel().setColor(Color.DARK_GRAY);
            } else {
                record.button.setDisabled(false);
                record.button.getLabel().setColor(Color.WHITE);
            }
        }
    }
}
