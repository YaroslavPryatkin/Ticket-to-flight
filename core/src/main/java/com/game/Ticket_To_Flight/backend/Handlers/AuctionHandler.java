package com.game.Ticket_To_Flight.backend.Handlers;

import com.game.Ticket_To_Flight.backend.gameLogicEntities.Player;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.commonFrontAndBack.StaticGameData;

import java.util.ArrayList;
import java.util.List;

public class AuctionHandler {
    private final List<Integer> exitList = new ArrayList<>();
    private final GameData gameData;
    private final DataChangesCreator dataChangesCreator;

    public AuctionHandler(GameData gameData, DataChangesCreator dataChangesCreator) {
        this.gameData = gameData; this.dataChangesCreator = dataChangesCreator;}

    public boolean pass(){
        if(!exitList.contains(gameData.currentPlayer)) {
            exitList.add(gameData.currentPlayer);
            return true;
        }
        else return false;
    }

    public boolean areAllPlayersReady(){
        return exitList.size() == gameData.players.size();
    }

    /**
     * @return null if ok, else error message
     */
    public String canBet(Integer newPlayerBet){
        if(newPlayerBet == null) return "Could not parse";
        if(newPlayerBet < gameData.currentBet + StaticGameData.minimalAuctionBetIncrease)
            return "The bet increase is less then minimal bet increase";
        Player pl = gameData.players.get(gameData.currentPlayer);
        if(pl.money + pl.auctionBet < newPlayerBet) return "Not enough money";
        return null;
    }

    public void bet(Integer newPlayerBet){
        Integer betChange = newPlayerBet - gameData.players.get(gameData.currentPlayer).auctionBet;
        dataChangesCreator.playerMakeBet(gameData.currentPlayer, betChange);
        dataChangesCreator.setCurrentBet(newPlayerBet);
    }

    public void finishAndResetAuction(){
        List<Integer> res = new ArrayList<>();
        for(int i=exitList.size()-1; i>=0;--i){
            int id = exitList.get(i);
            dataChangesCreator.returnBetPercent(id, returnPercent(i));
            res.add(id);
        }
        dataChangesCreator.setTurnOrderInt(res);
        dataChangesCreator.resetCurrentBet();
        exitList.clear();
    }

    private double returnPercent(int i){
        return 1 - Math.pow((double) i/(gameData.players.size()-1), 1.5);
    }
}
