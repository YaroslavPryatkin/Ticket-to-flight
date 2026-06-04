package com.game.Ticket_To_Flight.frontend.components.tables;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PassengerType;
import com.game.Ticket_To_Flight.frontend.components.AbstractPassengerTableWidget;
import com.game.Ticket_To_Flight.frontend.components.buttons.RoundedButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AirportPassengerTableWidget extends AbstractPassengerTableWidget {
    private final boolean canSelectGroup;
    private final Runnable onSelectionChanged;
    private final List<PassengerType> selectedPassengers = new ArrayList<>();

    public AirportPassengerTableWidget(Skin skin, boolean canSelectGroup, Runnable onSelectionChanged) {
        super(skin, true);
        this.canSelectGroup = canSelectGroup;
        this.onSelectionChanged = onSelectionChanged;
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

        selectBtn.addListener(new ClickListener() {
            private boolean selected = false;

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!canSelectGroup) return;

                selected = !selected;
                if (selected) {
                    selectedPassengers.add(passengerType);
                    selectBtn.setText("Selected");
                    selectBtn.getColor().set(Color.GREEN);
                } else {
                    selectedPassengers.remove(passengerType);
                    selectBtn.setText("Select");
                    selectBtn.getColor().set(Color.WHITE);
                }

                if (onSelectionChanged != null) {
                    onSelectionChanged.run();
                }
            }
        });

        return selectBtn;
    }
}
