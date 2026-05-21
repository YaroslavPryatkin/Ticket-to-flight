package com.game.Ticket_To_Flight.backend.Handlers;

import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;

public class TurnAndRoundHandler {
    private final GameData gameData;

    public TurnAndRoundHandler(GameData gameData){this.gameData = gameData;}
    public Integer nextPlayer(){
        return 1;
    }
}
