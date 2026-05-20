package com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.GameUIManagerDirectory;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.game.Ticket_To_Flight.Utilities.MapHolder;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PassengerType;

import java.util.Iterator;

public class AirportTooltipWindow extends Window {

    public AirportTooltipWindow(Skin skin, Airport airport) {
        super(airport.getCityName(), skin);
        this.pad(20);

        Table table = new Table();
        table.add(new Label("Route", skin)).padRight(10);
        table.add(new Label("Group", skin));
        table.row();

        var guestsMap = airport.getGuests();

        if (guestsMap != null) {
            Iterator<PassengerType> it = MapHolder.viewAsListIterator(guestsMap);
            PassengerType type;
            while ((type = it.next()) != null) {
                Integer groupCount = guestsMap.get(type);
                if (groupCount == null || groupCount == 0) continue;

                String passengerInfo = type.description;
                String countText = groupCount + " гр. (по " + type.size + " чел.)";

                table.add(new Label(passengerInfo, skin)).padRight(10).left();
                table.add(new Label(countText, skin)).right();
                table.row();
            }
        }
        else {
            table.add(new Label("No guests", skin)).colspan(2);
        }

        this.add(table);
        this.pack();
    }
}
