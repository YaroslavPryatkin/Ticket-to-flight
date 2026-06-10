package com.game.Ticket_To_Flight.backend;

import com.game.Ticket_To_Flight.PresetPaths;
import com.game.Ticket_To_Flight.backend.Handlers.AuctionHandler;
import com.game.Ticket_To_Flight.backend.Handlers.RouteChecker;
import com.game.Ticket_To_Flight.backend.Handlers.WorldMapUpdater;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airline;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Player;
import com.game.Ticket_To_Flight.backend.server.MainLoopBack;
import com.game.Ticket_To_Flight.backend.Handlers.LowLevelHandlerBack.Flags;
import com.game.Ticket_To_Flight.commonFrontAndBack.DTO.RouteDTO;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.commonFrontAndBack.StaticGameData;
import com.game.Ticket_To_Flight.network.Network;

import static com.game.Ticket_To_Flight.network.Network.FinishStatus.*;

public class MainLogic extends MainLoopBack {
    private static MainLogic instance;
    public static boolean canServerBeStopped = false;

    public static synchronized MainLogic getInstance() {
        if (instance == null) {
            instance = new MainLogic();
        }
        return instance;
    }

    public static boolean isServerExist(){
        return instance != null;
    }

    public static synchronized void stopServer(){
        if(instance!=null)
            instance.stop();
        instance = null;
    }

    private MainLogic(){
        super();
    }

    private final AuctionHandler auctionHandler = new AuctionHandler(gameData, llh.dataChangesCreator);
    private final WorldMapUpdater worldMapUpdater = new WorldMapUpdater(
        PresetPaths.presetPaths.get(1), gameData, llh.dataChangesCreator);
    private final RouteChecker routeChecker = new RouteChecker(gameData, llh.dataChangesCreator);


    @Override
    protected void mainCycle(){
        if(llh.getGamePreparationState() == Flags.GamePreparationsState.WAITING_FOR_PLAYERS){
            llh.beforeStartCycle();
        }
        else{
            if(gameData.players.isEmpty()){
                MainLogic.stopServer();
            }
            else if(gameData.currentState == GameData.State.GAME_FINISHED){
                //do nothing
            }
            else if(llh.getCurrentPlayerState() == Flags.CurrentPlayerState.NO_PLAYER_STAGE) {
                if (gameData.currentState == GameData.State.WORLD_UPDATE) {
                    worldMapUpdater.loadRound();
                }
                else if(gameData.currentState == GameData.State.INCOME_AND_TAXES){
                    llh.dataChangesCreator.addIncomeToMoneyForEveryPlayer();
                    llh.dataChangesCreator.takeTaxesFromIncomeForEveryPlayer();
                    llh.dataChangesCreator.resetPlayersIfNeeded();
                }
                llh.finishTurnSuccessfully();
            }
            else {
                if(llh.getCurrentPlayerState() == Flags.CurrentPlayerState.ANSWERED){
                   //something went wrong
                    llh.setWaitingForResponseFlag();
                }
                else if(llh.getCurrentPlayerState() == Flags.CurrentPlayerState.GOOD_RESPONSE){
                    llh.finishTurnSuccessfully();
                }
            }
        }
    }

    @Override
    public void handlePlayerResponse(Network.GameMessage message){
        if(message instanceof Network.PlayerInvestmentChoiceResponse resp){
            if(gameData.currentState != GameData.State.INVESTMENTS)
                llh.sendWrongStateError();
            else {
                int addedAmountOfShares = resp.amountOfShares;
                if (addedAmountOfShares < 0) {
                    llh.sendError("Amount of shares should be >= 0");
                    return;
                }
                Player pl = gameData.players.get(gameData.currentPlayer);
                if (pl.amountOfShares + addedAmountOfShares <= StaticGameData.maxAmountOfShares) {
                    llh.dataChangesCreator.addAmountOfShares(addedAmountOfShares);
                    llh.playerFinished();
                } else {
                    llh.sendError("Amount of shares should be < maximum amount of shares");
                }
            }
        }
        else if(message instanceof Network.PlayerAuctionChoiceResponse resp){
            if(gameData.currentState != GameData.State.AUCTION)
                llh.sendWrongStateError();
            else {
                if (resp.isPass) {
                    if (auctionHandler.pass()) {
                        if (auctionHandler.areAllPlayersReady())
                            auctionHandler.finishAndResetAuction();
                        llh.playerFinished();
                    } else {
                        llh.sendError("Already passed");
                    }
                } else {
                    Integer newPlayerBet = resp.betAmount;
                    String betRepl = auctionHandler.canBet(newPlayerBet);
                    if (betRepl == null) {
                        auctionHandler.bet(newPlayerBet);
                        llh.playerNotFinished();
                    } else {
                        llh.sendError(betRepl);
                    }
                }
            }
        }
        else if(message instanceof Network.PlayerAbilityChoiceResponse resp){
            if(gameData.currentState != GameData.State.ABILITIES)
                llh.sendWrongStateError();
            else {
                if (StaticGameData.abilityTypes.contains(resp.ability)) {
                    if (resp.ability != 0 && gameData.availableAbilities.contains(resp.ability)) {
                        llh.dataChangesCreator.giveAbility(resp.ability);
                        if(resp.ability == 4)
                            llh.dataChangesCreator.giveActionPointsAbility();
                        llh.playerFinished();
                    } else
                        llh.sendError("The chosen ability is unavailable");
                } else
                    llh.sendError("Unknown ability id");
            }
        }
        else if(message instanceof Network.PlayerPlaneChoiceResponse resp){
            if(gameData.currentState != GameData.State.PLANES)
                llh.sendWrongStateError();
            else {
                if(resp.finishStatus == PASS){
                    llh.playerFinished();
                }
                else {
                    Player pl = gameData.players.get(gameData.currentPlayer);
                    if (pl.actionPoints > 0) {
                        if (gameData.availablePlanes.getOrDefault(resp.plane, 0) > 0) {
                            int price = StaticGameData.planeTypes.get(resp.plane).price;
                            if(pl.money >= price) {
                                llh.dataChangesCreator.moneyLoss(price);
                                llh.dataChangesCreator.takeActionPoint();
                                llh.dataChangesCreator.sellPlane(resp.plane);
                                if (resp.finishStatus == FINISHED) {
                                    llh.playerFinished();
                                } else {
                                    llh.playerNotFinished();
                                }
                            }
                            else{
                                llh.sendError("Doesn't have enough money.");
                            }
                        } else {
                            llh.sendError("This plane is unavailable.");
                        }
                    } else {
                        llh.sendError("Not enough action points.");
                    }
                }
            }
        }
        else if(message instanceof Network.PlayerAirlineChoiceResponse resp){
            if(gameData.currentState != GameData.State.AIRLINES)
                llh.sendWrongStateError();
            else {
                if(resp.finishStatus == PASS){
                    llh.playerFinished();
                }
                else {
                    Player pl = gameData.players.get(gameData.currentPlayer);
                    if (pl.actionPoints > 0) {
                        Airline line = gameData.availableAirlines.get(resp.line);
                        if (line != null) {
                            if(line.portA.getFreeGates()>=line.type.gateA && line.portB.getFreeGates() >= line.type.gateB) {
                                if (pl.money >= line.getPrice()) {
                                    llh.dataChangesCreator.moneyLoss(line.getPrice());
                                    llh.dataChangesCreator.takeActionPoint();
                                    llh.dataChangesCreator.sellAirline(resp.line);
                                    llh.dataChangesCreator.takeGates(line.portA.getId(), line.type.gateA);
                                    llh.dataChangesCreator.takeGates(line.portB.getId(), line.type.gateB);
                                    if (resp.finishStatus == FINISHED) {
                                        llh.playerFinished();
                                    } else {
                                        llh.playerNotFinished();
                                    }
                                } else {
                                    llh.sendError("Doesn't have enough money.");
                                }
                            }
                            else{
                                llh.sendError("An airport at the end of the line can not have more lines connected to it.");
                            }
                        } else {
                            llh.sendError("This airline is unavailable");
                        }
                    } else {
                        llh.sendError("Not enough action points.");
                    }
                }
            }
        }
        else if(message instanceof Network.PlayerRouteChoiceResponse resp){
            if(gameData.currentState != GameData.State.FLIGHTS)
                llh.sendWrongStateError();
            else {
                if(resp.finishStatus == PASS){
                    llh.playerFinished();
                }
                else {
                    Player pl = gameData.players.get(gameData.currentPlayer);
                    if (pl.actionPoints > 0) {
                        if(resp.dto != null){
                            routeChecker.clearRT();
                            if(routeChecker.downloadAndCheckDTO(resp.dto)){
                                llh.dataChangesCreator.takeActionPoint();
                                routeChecker.applyRT();
                                if (resp.finishStatus == FINISHED) {
                                    llh.playerFinished();
                                } else {
                                    llh.playerNotFinished();
                                }
                            }
                            else{
                                llh.sendError("Invalid route.");
                            }
                        }
                        else{
                            llh.sendError("Route is null.");
                        }
                    } else {
                        llh.sendError("Not enough action points.");
                    }
                }
            }
        }
        else{
            llh.sendUnknownMessageError();
        }
    }
}
