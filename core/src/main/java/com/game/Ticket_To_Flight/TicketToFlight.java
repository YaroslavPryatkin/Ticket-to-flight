package com.game.Ticket_To_Flight;

import com.badlogic.gdx.Game;
import com.esotericsoftware.kryonet.Server;
import com.game.Ticket_To_Flight.frontend.GameClient;
import com.game.Ticket_To_Flight.frontend.UI.MainDrawer;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class TicketToFlight extends Game {
    @Override
    public void create() {
        GameClient gameClient = new GameClient();
        MainDrawer mainDrawer = new MainDrawer(this, gameClient.getMainClient());
        Server server = new Server();

        gameClient.connect("127.0.0.1");
        gameClient.sendWorldMapPacket();
    }
}
