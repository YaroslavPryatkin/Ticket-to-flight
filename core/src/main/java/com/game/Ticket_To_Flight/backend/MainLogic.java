package com.game.Ticket_To_Flight.backend;

import com.game.Ticket_To_Flight.backend.server.MainLoopBack;

public class MainLogic extends MainLoopBack {
    private static MainLogic instance;

    public static synchronized MainLogic getInstance() {
        if (instance == null) {
            instance = new MainLogic();
        }
        return instance;
    }

    private MainLogic(){
        super();
    }

    @Override
    protected void mainCycle(){

    }
}
