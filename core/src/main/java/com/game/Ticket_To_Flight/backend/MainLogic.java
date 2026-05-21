package com.game.Ticket_To_Flight.backend;

import com.badlogic.gdx.math.Vector2;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Player;
import com.game.Ticket_To_Flight.backend.server.MainLoopBack;
import com.game.Ticket_To_Flight.backend.Handlers.LowLevelHandlerBack.Flags;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.commonFrontAndBack.StatisGameData;

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

    private boolean sentChanges =false;
    @Override
    protected void mainCycle(){
        if(llh.flags.gamePreparationsState == Flags.GamePreparationsState.WAITING_FOR_PLAYERS){
            llh.beforeStartCycle();
        }
        else{
            if(gameData.currentState == GameData.State.NO_STATE){
                llh.dataChangesCreator.setCurrentRound(1);
                llh.dataChangesCreator.setCurrentPLayer(-1);
                llh.dataChangesCreator.setCurrentState(GameData.State.WORLD_UPDATE);
                llh.applyAndSendDataChanges();
            }
            else if(gameData.currentState == GameData.State.WORLD_UPDATE) {
                if(!sentChanges){
                    llh.dataChangesCreator.setCurrentPLayer(1);
                    llh.dataChangesCreator.setCurrentState(GameData.State.INVESTMENTS);
                    llh.dataChangesCreator.addAirport(1,101,new Vector2(100,100), "Test port 1");
                    llh.dataChangesCreator.addAirport(2,102,new Vector2(200,200), "Test port 2");
                    llh.dataChangesCreator.addAirport(3,103,new Vector2(100,200), "Test port 3");
                    llh.dataChangesCreator.addAirline(201, 1, 2 );
                    llh.dataChangesCreator.addAirline(202, 2, 3 );
                    llh.applyAndSendDataChanges();
                    sentChanges = true;
                }
            }
            else if (gameData.currentState == GameData.State.INVESTMENTS) {
                if(llh.flags.currentPlayerState == Flags.CurrentPlayerState.ANSWERED){
                    llh.dataChangesCreator.setCurrentState(GameData.State.AUCTION);
                    llh.dataChangesCreator.setCurrentPLayer(1);
                    llh.flags.currentPlayerState = Flags.CurrentPlayerState.WAITING_FOR_RESPONSE;
                    System.out.println("good answer to investment");
                    llh.applyAndSendDataChanges();
                }
            }
            else if (gameData.currentState == GameData.State.AUCTION) {
                if (llh.flags.currentPlayerState == Flags.CurrentPlayerState.ANSWERED) {
                    llh.applyAndSendDataChanges();
                }
            }
            else if (gameData.currentState == GameData.State.ABILITIES) {

            }
            else if (gameData.currentState == GameData.State.PLANES) {

            }
            else if (gameData.currentState == GameData.State.AIRLINES) {

            }
            else if (gameData.currentState == GameData.State.EVENT) {

            }
            else if (gameData.currentState == GameData.State.FLIGHTS) {

            }
            else if (gameData.currentState == GameData.State.INCOME) {

            }
            else if (gameData.currentState == GameData.State.TAXES) {

            }
        }
    }


    @Override
    public void handleInvestmentResponse(Integer addedAmountOfShares){
        if(addedAmountOfShares == null) return;
        if(addedAmountOfShares <= 0) llh.sendError("Amount of shares should be > 0");
        Player pl = gameData.players.get(gameData.currentPlayer);
        if(pl.amountOfShares + addedAmountOfShares <= StatisGameData.maxAmountOfShares){
            llh.dataChangesCreator.addAmountOfShares(addedAmountOfShares);
        }
        else{
            llh.sendError("Amount of shares should be < maximum amount of shares");
        }
    }

    @Override
    public void handleAuctionResponse(Integer betAmount) {
        Player pl = gameData.players.get(gameData.currentPlayer);
        if(betAmount == null){

        }
        else {
            if (betAmount <= 0) llh.sendError("Amount of bet should be > 0");

        }
    }

    @Override
    public void handleAbilityChoiceResponse(Integer ability) {

    }

}
