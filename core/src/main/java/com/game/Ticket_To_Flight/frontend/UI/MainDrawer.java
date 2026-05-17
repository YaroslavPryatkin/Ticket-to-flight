package com.game.Ticket_To_Flight.frontend.UI;

import com.badlogic.gdx.Game;
import com.game.Ticket_To_Flight.frontend.MainClient;
import com.game.Ticket_To_Flight.frontend.UI.renderers.WorldMapRenderer;
import com.game.Ticket_To_Flight.packages.PackageCreateWorldMap;
import com.game.Ticket_To_Flight.packages.PackageInitAirlines;
import com.game.Ticket_To_Flight.packages.PackageInitAirports;

public class MainDrawer {
    private final Game myGame;

    private WorldMapRenderer currentMapScreen;

    public MainDrawer(Game myGame, MainClient mainClient) {
        this.myGame = myGame;
        this.currentMapScreen = new WorldMapRenderer(mainClient);
        myGame.setScreen(currentMapScreen);
    }

    public void drawAirports(PackageInitAirports packet) {
        if (this.currentMapScreen != null) {
            this.currentMapScreen.updateAirportData(packet.getAirports());
        }
    }

    public void drawAirlines(PackageInitAirlines packet) {
        if (this.currentMapScreen != null) {
            this.currentMapScreen.updateAirlinesData(packet.getAirlines());
        }
    }

    public void drawMap(float delta){
        currentMapScreen.renderNoLogic(delta);
    }

}
