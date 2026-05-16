package com.game.Ticket_To_Flight.backend;

import com.badlogic.gdx.math.Vector2;
import com.game.Ticket_To_Flight.backend.server.MainLoopBack;
import com.game.Ticket_To_Flight.backend.LowLevelHandlerBack.Flags;

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

    private boolean sendedChanges=false;
    @Override
    protected void mainCycle(){
        if(llh.flags.gamePreparationsState == Flags.GamePreparationsState.WAITING_FOR_PLAYERS){
            beforeStartCycle();
        }
        else{
            if(!sendedChanges){
                llh.addAirport(1,101,new Vector2(100,100), "Test port 1");
                llh.addAirport(2,102,new Vector2(200,200), "Test port 2");
                llh.addAirport(3,103,new Vector2(100,200), "Test port 3");
                llh.addAirline(201, 1, 2 );
                llh.addAirline(202, 2, 3 );
                llh.applyAndSendDataChanges();
                System.out.println("Game data");
                System.out.println("Players:");
                gameData.players.printAllToConsole();
                System.out.println("Airports:");
                gameData.airports.printAllToConsole();
                System.out.println("Airlines:");
                gameData.airlines.printAllToConsole();
                sendedChanges = true;
            }
        }
    }

    private void beforeStartCycle(){
        if(llh.areAllPlayersReadyToStart())
            llh.startGame();
    }

}
