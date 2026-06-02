package com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.Tooltips;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.game.Ticket_To_Flight.Utilities.MapHolder;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PassengerType;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.Flights.PassengerSelectionListener;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.MapInput.MapSelectionState;
import com.game.Ticket_To_Flight.frontend.components.buttons.RoundedButton;
import com.game.Ticket_To_Flight.frontend.components.texts.SingleLineText;

import java.util.Iterator;

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

        Table table = new Table();

        table.add(new SingleLineText("Route", skin)).padRight(20).left();
        table.add(new SingleLineText("Persons", skin)).padRight(20).center();

        table.add(new SingleLineText("Reward", skin)).padRight(20).center();
        table.add(new SingleLineText("Class", skin)).padRight(20).center();

        table.add(new SingleLineText("Available", skin)).padRight(20).center();
        table.add(new SingleLineText("Choice", skin)).padLeft(10);
        table.row();

        var guestsMap = airport.getGuests();
        final com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup<TextButton> groupButtons = new com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup<>();
        groupButtons.setMaxCheckCount(1);
        groupButtons.setMinCheckCount(0);
        boolean hasGroups = false;

        int totalColumns = 6;

        if (guestsMap != null) {
            Iterator<PassengerType> it = MapHolder.viewAsListIterator(guestsMap);
            PassengerType type;
            while ((type = it.next()) != null) {
                Integer groupCount = guestsMap.get(type);
                if (groupCount == null || groupCount == 0) continue;
                hasGroups = true;

                table.add(new SingleLineText(type.description, skin)).padRight(20).left();
                table.add(new SingleLineText(String.valueOf(type.size), skin)).padRight(20).center();

                table.add(new SingleLineText("100$", skin)).padRight(20).center();
                table.add(new SingleLineText("Economy", skin)).padRight(20).center();

                table.add(new SingleLineText(groupCount + " grp", skin)).padRight(20).center();

                TextButton groupButton = new RoundedButton("Select", skin);
                groupButton.setUserObject(type);
                groupButton.setDisabled(!canSelectGroup);
                if (selectionState.isPassengerTypeSelected(type)) {
                    groupButton.setChecked(true);
                }
                groupButtons.add(groupButton);
                table.add(groupButton).width(160).height(40).padLeft(10);

                table.row().padTop(10);
            }
        }
        else {
            table.add(new SingleLineText("No guests", skin)).colspan(totalColumns).center().padTop(10);
        }

        if (guestsMap != null && !hasGroups) {
            table.add(new SingleLineText("No groups ready to fly", skin)).colspan(totalColumns).center().padTop(10);
            table.row();
        }

        final TextButton chooseButton = new RoundedButton("Choose group", skin);
        chooseButton.setDisabled(!canSelectGroup || groupButtons.getChecked() == null);
        chooseButton.getLabel().setColor(chooseButton.isDisabled() ? Color.LIGHT_GRAY : Color.WHITE);

        for (TextButton button : groupButtons.getButtons()) {
            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    boolean hasGroup = groupButtons.getChecked() != null;
                    chooseButton.setDisabled(!hasGroup);
                    chooseButton.getLabel().setColor(hasGroup ? Color.WHITE : Color.LIGHT_GRAY);
                }
            });
        }

        chooseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                TextButton checked = groupButtons.getChecked();
                if (chooseButton.isDisabled() || checked == null) return;

                PassengerType passengerType = (PassengerType) checked.getUserObject();
                selectionState.selectAirport(airport);
                selectionState.selectPassengerType(passengerType);
                if (passengerSelectionListener != null) {
                    passengerSelectionListener.onPassengerSelected(airport, passengerType);
                }
                chooseButton.setText("Group selected");
            }
        });

        this.add(table).row();
        this.add(chooseButton).width(280).height(60).padTop(20);
        this.pack();
    }

    @Override
    public Window asWindow() {
        return this;
    }
}
