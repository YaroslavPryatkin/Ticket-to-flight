package com.game.Ticket_To_Flight.frontend.UI;

import com.badlogic.gdx.Game;
import com.game.Ticket_To_Flight.frontend.MainClient;
import com.game.Ticket_To_Flight.frontend.UI.renderers.WorldMapRenderer;
import com.game.Ticket_To_Flight.packages.PackageInitAirlines;
import com.game.Ticket_To_Flight.packages.PackageInitAirports;

public class MainDrawer {
    private final Game myGame;

    private WorldMapRenderer WorldMapScreen;

    public MainDrawer(Game myGame, MainClient mainClient) {
        this.myGame = myGame;
        this.WorldMapScreen = new WorldMapRenderer(mainClient);
        myGame.setScreen(WorldMapScreen);
    }

    public void createWorldMap(MainClient mainClient) {
        this.WorldMapScreen = new WorldMapRenderer(mainClient);
    }

    public void drawInvestmentWindow() {
        WorldMapScreen.showInvestWindow();
    }

    public void drawAuctionWindow() {
        WorldMapScreen.showAuctionWindow();
    }

    public void drawMap(float delta){
        WorldMapScreen.renderNoLogic(delta);
    }

}
