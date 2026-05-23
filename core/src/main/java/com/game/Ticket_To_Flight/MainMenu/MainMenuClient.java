package com.game.Ticket_To_Flight.MainMenu;

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
        this.mainClient = new MainClient(this.gm);
    }
}
