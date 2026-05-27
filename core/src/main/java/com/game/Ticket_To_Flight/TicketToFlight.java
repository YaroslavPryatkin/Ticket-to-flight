package com.game.Ticket_To_Flight;

import com.badlogic.gdx.Game;
import com.game.Ticket_To_Flight.frontend.UI.MainMenuScreen.MainMenuClient;
import com.game.Ticket_To_Flight.commonFrontAndBack.StaticGameData;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class TicketToFlight extends Game {
    @Override
    public void create() {
        StaticGameData.loadAllJsons();
        MainMenuClient mainMenuClient = new MainMenuClient(this);
    }
}
