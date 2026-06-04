package com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.Tooltips.AirportDirectory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PassengerType;
import com.game.Ticket_To_Flight.frontend.components.AbstractPassengerTableWidget;
import com.game.Ticket_To_Flight.frontend.components.buttons.RoundedButton;
import com.game.Ticket_To_Flight.frontend.components.tables.PassengerTableWidget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AirportPassengerTableWidget extends AbstractPassengerTableWidget {
    private final boolean canSelectGroup;
    private final Runnable onSelectionChanged;
    private final List<PassengerType> selectedPassengers = new ArrayList<>();

    private int maxCapacity = Integer.MAX_VALUE;
    private final List<ButtonRecord> buttonRecords = new ArrayList<>();

    private static class ButtonRecord {
        TextButton button;
        int groupSize;
        boolean isSelected = false;

        ButtonRecord(TextButton button, int groupSize) {
            this.button = button;
            this.groupSize = groupSize;
        }
    }

    public AirportPassengerTableWidget(Skin skin, boolean canSelectGroup, Integer maxCapacity, Runnable onSelectionChanged) {
        super(skin, true);
        this.canSelectGroup = canSelectGroup;
        this.onSelectionChanged = onSelectionChanged;
        this.maxCapacity = maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
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
        selectBtn.setDisabled(!canSelectGroup);

        int groupSize = 0;
        try {
            groupSize = Integer.parseInt(row.persons().trim());
        } catch (Exception e) {
            groupSize = 0;
        }

        final ButtonRecord record = new ButtonRecord(selectBtn, groupSize);
        buttonRecords.add(record);

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

        int usedSeats = 0;
        for (ButtonRecord record : buttonRecords) {
            if (record.isSelected) {
                usedSeats += record.groupSize;
            }
        }

        int remainingSeats = maxCapacity - usedSeats;

        for (ButtonRecord record : buttonRecords) {
            if (record.isSelected) continue;

            if (record.groupSize > remainingSeats) {
                record.button.setDisabled(true);
                record.button.getLabel().setColor(Color.DARK_GRAY);
            } else {
                record.button.setDisabled(false);
                record.button.getLabel().setColor(Color.WHITE);
            }
        }
    }
}
