package com.game.Ticket_To_Flight;

import com.badlogic.gdx.Game;
import com.game.Ticket_To_Flight.frontend.MainClient;
import com.game.Ticket_To_Flight.frontend.UI.MainDrawer;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class TicketToFlight extends Game {
    @Override
    public void create() {
        MainClient cl = new MainClient();
        MainDrawer mainDrawer = new MainDrawer(this, cl);
        cl.setMainDrawer(mainDrawer);
        cl.sendWorldMapPacket();

    }
}
