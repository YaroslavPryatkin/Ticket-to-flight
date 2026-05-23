package com.game.Ticket_To_Flight;

import com.badlogic.gdx.Game;
import com.game.Ticket_To_Flight.backend.MainLogic;
import com.game.Ticket_To_Flight.commonFrontAndBack.StaticGameData;
import com.game.Ticket_To_Flight.frontend.MainClient;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class TicketToFlight extends Game {
    private MainLogic logic;
    @Override
    public void create() {
        StaticGameData.loadAllJsons();
        logic = MainLogic.getInstance();
        MainClient cl = new MainClient(this);
    }
}
