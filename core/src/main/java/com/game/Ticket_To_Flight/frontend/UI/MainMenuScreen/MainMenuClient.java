package com.game.Ticket_To_Flight.frontend.UI.MainMenuScreen;

import com.badlogic.gdx.Game;
import com.game.Ticket_To_Flight.backend.MainLogic;
import com.game.Ticket_To_Flight.frontend.MainClient;

public class MainMenuClient {
    private MainMenuRenderer mainMenuRenderer;
    private MainLogic mainLogic = null;
    private MainClient mainClient = null;
    private Game gm;

    public MainMenuClient(Game gm) {
        this.mainMenuRenderer = new MainMenuRenderer(this);
        this.gm = gm;

        this.gm.setScreen(this.mainMenuRenderer);
    }

    public boolean createMainLogic() {
        if (this.mainLogic == null) {
            this.mainLogic = MainLogic.getInstance();
            return true;
        }
        return false;
    }

    public void createMainClient() {
        if (this.mainClient == null)
            this.mainClient = new MainClient(this.gm, this.mainMenuRenderer, this);
        this.gm.setScreen(mainClient.connectionRenderer);
    }

    public void killMainClient() {
        this.mainClient = null;
        this.gm.setScreen(mainMenuRenderer);
    }
}
