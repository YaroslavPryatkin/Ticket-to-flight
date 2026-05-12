package com.game.Ticket_To_Flight.frontend.UI;

import com.badlogic.gdx.Game;
import com.game.Ticket_To_Flight.frontend.MainClient;
import com.game.Ticket_To_Flight.frontend.UI.renderers.WorldMapRenderer;
import com.game.Ticket_To_Flight.packages.PackageCreateWorldMap;

public class MainDrawer {
    private final Game myGame;

    public MainDrawer(Game myGame, MainClient mainClient) {
        this.myGame = myGame;
        mainClient.setMainDrawer(this);
    }

    public void drawWorldMap(PackageCreateWorldMap packet) {
        WorldMapRenderer mapRenderer = new WorldMapRenderer(packet);
        myGame.setScreen(mapRenderer);
    }
}
