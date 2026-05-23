package com.game.Ticket_To_Flight.backend;

import com.badlogic.gdx.math.Vector2;
import com.game.Ticket_To_Flight.backend.Handlers.AuctionHandler;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Player;
import com.game.Ticket_To_Flight.backend.server.MainLoopBack;
import com.game.Ticket_To_Flight.backend.Handlers.LowLevelHandlerBack.Flags;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.commonFrontAndBack.StaticGameData;
import com.game.Ticket_To_Flight.network.Network;

public class MainLogic extends MainLoopBack {
    private static MainLogic instance;


    public static synchronized MainLogic getInstance() {
        if (instance == null) {
            instance = new MainLogic();
        }
        return instance;
    }

    public static synchronized void stopServer(){
        instance.stop();
        instance = null;
    }

    private MainLogic(){
        super();
    }

    private final AuctionHandler auctionHandler = new AuctionHandler(gameData, llh.dataChangesCreator);


    private boolean sentChanges =false;
    @Override
    protected void mainCycle(){
        if(llh.getGamePreparationState() == Flags.GamePreparationsState.WAITING_FOR_PLAYERS){
            llh.beforeStartCycle();
        }
        else{
            if(llh.getCurrentPlayerState() == Flags.CurrentPlayerState.NO_PLAYER_STAGE) {
                if (gameData.currentState == GameData.State.WORLD_UPDATE) {
                    if (!sentChanges) {
                        llh.dataChangesCreator.addAirport(1, 101, new Vector2(100, 100), "Test port 1");
                        llh.dataChangesCreator.addAirport(2, 102, new Vector2(200, 200), "Test port 2");
                        llh.dataChangesCreator.addAirport(3, 103, new Vector2(100, 200), "Test port 3");
                        llh.dataChangesCreator.addAirline(201, 1, 2);
                        llh.dataChangesCreator.addAirline(202, 2, 3);
                        sentChanges = true;
                    }
                }
                llh.finishTurnSuccessfully(null);
            }
            else {
                if(llh.getCurrentPlayerState() == Flags.CurrentPlayerState.ANSWERED){
                   //something went wrong
                    llh.setWaitingForResponseFlag();
                }
                else if(llh.getCurrentPlayerState() == Flags.CurrentPlayerState.NOT_FINISHED_STATE){
                    llh.finishTurnSuccessfully(false);
                }
                else if(llh.getCurrentPlayerState() == Flags.CurrentPlayerState.FINISHED_STATE){
                    llh.finishTurnSuccessfully(true);
                }
            }
        }
    }

    @Override
    public void handlePlayerResponse(Network.GameMessage message){
        if(message instanceof Network.PlayerInvestmentChoiceResponse){
            if(gameData.currentState != GameData.State.INVESTMENTS)
                llh.sendWrongStateError();
            else {
                Network.PlayerInvestmentChoiceResponse resp = (Network.PlayerInvestmentChoiceResponse) message;
                Integer addedAmountOfShares = resp.amountOfShares;
                if (addedAmountOfShares == null) {
                    llh.sendError("Could not parse");
                    return;
                }
                if (addedAmountOfShares <= 0) {
                    llh.sendError("Amount of shares should be > 0");
                    return;
                }
                Player pl = gameData.players.get(gameData.currentPlayer);
                if (pl.amountOfShares + addedAmountOfShares <= StaticGameData.maxAmountOfShares) {
                    llh.dataChangesCreator.addAmountOfShares(addedAmountOfShares);
                    llh.setFinishedStateFlag();
                } else {
                    llh.sendError("Amount of shares should be < maximum amount of shares");
                }
            }
        }
        else if(message instanceof Network.PlayerAuctionChoiceResponse){
            if(gameData.currentState != GameData.State.AUCTION)
                llh.sendWrongStateError();
            else {
                Network.PlayerAuctionChoiceResponse resp = (Network.PlayerAuctionChoiceResponse) message;
                if (resp.isPass) {
                    if (auctionHandler.pass()) {
                        if (auctionHandler.areAllPlayersReady())
                            auctionHandler.finishAndResetAuction();
                        llh.setFinishedStateFlag();
                    } else {
                        llh.sendError("Already passed");
                    }
                } else {
                    Integer newPlayerBet = resp.betAmount;
                    String betRepl = auctionHandler.canBet(newPlayerBet);
                    if (betRepl == null) {
                        auctionHandler.bet(newPlayerBet);
                        llh.setNotFinishedStateFlag();
                    } else {
                        llh.sendError(betRepl);
                    }
                }
            }
        }
        else if(message instanceof Network.PlayerAbilityChoiceResponse){
            if(gameData.currentState != GameData.State.ABILITIES)
                llh.sendWrongStateError();
            else {
                Network.PlayerAbilityChoiceResponse resp = (Network.PlayerAbilityChoiceResponse) message;
                if (StaticGameData.abilityTypes.contains(resp.ability)) {
                    if (resp.ability != 0 && gameData.availableAbilities.contains(resp.ability)) {
                        llh.dataChangesCreator.giveAbility(resp.ability);
                        llh.setFinishedStateFlag();
                    } else
                        llh.sendError("The chosen ability is unavailable");
                } else
                    llh.sendError("Unknown ability id");
            }
        }
        else if(message instanceof Network.PlayerPlaneChoiceResponse){
            if(gameData.currentState != GameData.State.PLANES)
                llh.sendWrongStateError();
            else {
                Network.PlayerPlaneChoiceResponse resp = (Network.PlayerPlaneChoiceResponse) message;
            }
        }
        else if(message instanceof Network.PlayerAirlineChoiceResponse){
            if(gameData.currentState != GameData.State.AIRLINES)
                llh.sendWrongStateError();
            else {
                Network.PlayerAirlineChoiceResponse resp = (Network.PlayerAirlineChoiceResponse) message;
            }
        }
        else if(message instanceof Network.PlayerRouteChoiceResponse){
            if(gameData.currentState != GameData.State.FLIGHTS)
                llh.sendWrongStateError();
            else {
                Network.PlayerRouteChoiceResponse resp = (Network.PlayerRouteChoiceResponse) message;
            }
        }
        else{
            llh.sendUnknownMessageError();
        }
    }


}
