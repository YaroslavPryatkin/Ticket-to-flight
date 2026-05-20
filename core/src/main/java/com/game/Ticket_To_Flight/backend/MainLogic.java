package com.game.Ticket_To_Flight.backend;

import com.badlogic.gdx.math.Vector2;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Player;
import com.game.Ticket_To_Flight.backend.server.MainLoopBack;
import com.game.Ticket_To_Flight.backend.LowLevelHandlerBack.Flags;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;

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
            if(gameData.currentState == GameData.State.NO_STATE){
                llh.setCurrentRound(1);
                llh.setCurrentPLayer(-1);
                llh.setCurrentState(GameData.State.WORLD_UPDATE);
                llh.applyAndSendDataChanges();
            }
            else if(gameData.currentState == GameData.State.WORLD_UPDATE) {
                if(!sendedChanges){
                    llh.setCurrentPLayer(1);
                    llh.setCurrentState(GameData.State.INVESTMENTS);
                    llh.addAirport(1,101,new Vector2(100,100), "Test port 1");
                    llh.addAirport(2,102,new Vector2(200,200), "Test port 2");
                    llh.addAirport(3,103,new Vector2(100,200), "Test port 3");
                    llh.addAirline(201, 1, 2 );
                    llh.addAirline(202, 2, 3 );
                    llh.applyAndSendDataChanges();
                    sendedChanges = true;
                }
            }
            else if (gameData.currentState == GameData.State.INVESTMENTS) {
                if(llh.flags.currentPlayerState == Flags.CurrentPlayerState.ANSWERED){
                    llh.setCurrentState(GameData.State.AUCTION);
                    llh.setCurrentPLayer(1);
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

    private void beforeStartCycle(){
        if(llh.areAllPlayersReadyToStart()) {
            llh.startGame();
        }
    }

    @Override
    public void handleInvestmentResponse(Integer addedAmountOfShares){
        if(addedAmountOfShares <= 0) llh.sendError("Amount of shares should be > 0");
        Player pl = gameData.players.get(gameData.currentPlayer);
        if(pl.amountOfShares + addedAmountOfShares <= GameData.maxAmountOfShares){
            llh.addAmountOfShares(addedAmountOfShares);
        }
        else{
            llh.sendError("Amount of shares should be < maximum amount of shares");
        }
    }

    @Override
    public void handleAuctionResponse(Integer shares) {

    }

}
