package com.game.Ticket_To_Flight;

import com.badlogic.gdx.Game;
import com.game.Ticket_To_Flight.backend.server.GameServer;
import com.game.Ticket_To_Flight.frontend.GameClient;
import com.game.Ticket_To_Flight.frontend.GameScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    @Override
    public void create() {
        GameServer server = new GameServer();
        server.start();

        GameClient client = new GameClient();
        client.connect("test");
        this.setScreen(new GameScreen(client));
    }
}
