package com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.Flights.FlightDirectory;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.game.Ticket_To_Flight.Utilities.MapHolder;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Player;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PlaneType;
import com.game.Ticket_To_Flight.frontend.components.BaseGameWindow;
import com.game.Ticket_To_Flight.frontend.components.buttons.RoundedButton;
import com.game.Ticket_To_Flight.frontend.components.texts.SingleLineText;

import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;

public class PlaneSelectionWindow extends BaseGameWindow {

    public PlaneSelectionWindow(Skin skin, Player player, Consumer<PlaneType> onPlaneSelected) {
        super("Choose the plane", skin);

        this.padLeft(40).padRight(40).padBottom(40);

        Table planesTable = new Table();

        Iterator<Map.Entry<PlaneType, Integer>> iterator = MapHolder.viewAsEntrySet(player.planes);
        Map.Entry<PlaneType, Integer> entry;
        boolean hasPlanes = false;

        while ((entry = iterator.next()) != null) {
            hasPlanes = true;
            final PlaneType plane = entry.getKey();
            Integer count = entry.getValue();

            TextButton button = new RoundedButton(plane.description + " x" + count, skin);
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    onPlaneSelected.accept(plane);
                }
            });
            planesTable.add(button).width(900).height(80).padBottom(20).row();
        }

        if (!hasPlanes) {
            planesTable.add(new SingleLineText("No planes available", skin)).padBottom(20).row();
        }

        ScrollPane scrollPane = new ScrollPane(planesTable, skin);
        scrollPane.setFadeScrollBars(false);

        this.add(scrollPane).width(1000).height(500);
        this.pack();
    }
}
