package com.game.Ticket_To_Flight.backend.Handlers;

import com.game.Ticket_To_Flight.backend.gameLogicEntities.Player;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;

public class TurnOrderIterator {

    private final GameData gameData;
    private int currentPlayerInTurnOrder = -1;
    private int playerWithAbility = -1;

    public TurnOrderIterator(GameData gameData){
        this.gameData = gameData;
    }

    public void reset(Integer abilityId){
        currentPlayerInTurnOrder=-1;
        playerWithAbility=-1;
        if(abilityId != null && gameData.turnOrder!=null && !gameData.turnOrder.isEmpty()){
            for(int i=0;i<gameData.turnOrder.size();++i){
                if(gameData.turnOrder.get(i).ability.getId() == abilityId) {
                    playerWithAbility = i;
                    return;
                }
            }
        }
    }

    public Integer getNextPlayer(){
        if(gameData.turnOrder == null || gameData.turnOrder.isEmpty()) return null;

        int size = gameData.turnOrder.size();
        for (int i = 0; i < size; ++i) {
            int cur = next();
            Player curPlayer = gameData.turnOrder.get(cur);
            if (!curPlayer.hasPassed) {
                return curPlayer.getId();
            }
        }
        return null;
    }
    private int next(){
        ++currentPlayerInTurnOrder;
        currentPlayerInTurnOrder%=gameData.turnOrder.size();
        if(playerWithAbility == -1){
            return currentPlayerInTurnOrder;
        }
        else{
            if(currentPlayerInTurnOrder == 0)
                return playerWithAbility;
            if(currentPlayerInTurnOrder <= playerWithAbility)
                return currentPlayerInTurnOrder - 1;
            return currentPlayerInTurnOrder;
        }
    }
}
