package com.game.Ticket_To_Flight.frontend;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.game.Ticket_To_Flight.frontend.UI.MainMenuScreen.MainMenuClient;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.frontend.UI.MainMenuScreen.MainMenuRenderer;
import com.game.Ticket_To_Flight.frontend.UI.ConnectionScreen.ConnectionRenderer;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.WorldMapRenderer;
import com.game.Ticket_To_Flight.network.Network;
import com.game.Ticket_To_Flight.frontend.LowLevelHandlerFront.Flags;

public class MainClient {
    private final Game myGame;
    private final GameData gameData = new GameData();
    private final LowLevelHandlerFront llh = new LowLevelHandlerFront(gameData, this);
    private WorldMapRenderer worldMapRenderer;
    private MainMenuRenderer mainMenuRenderer;
    public ConnectionRenderer connectionRenderer;
    private MainMenuClient mainMenuClient;

    public MainClient(Game gm, MainMenuRenderer mainMenuRenderer, MainMenuClient mainMenuClient) {
        this.myGame = gm;
        this.mainMenuRenderer = mainMenuRenderer;
        this.mainMenuClient = mainMenuClient;
        this.connectionRenderer = new ConnectionRenderer(myGame, llh, this);
    }

    public void mainCycleWithUpdate(float delta) {
        llh.update();
        gameData.acquireReadLock();
        mainCycle(delta);
        gameData.releaseReadLock();
    }

    boolean wroteGameFinished = false;
    private void mainCycle(float delta){
        if(llh.getGamePreparationState() == Flags.GamePreparationsState.RUNNING) {
            if(llh.getCurrentStateState() == Flags.CurrentStateState.SERVER_DISCONNECTED){
                //stop game and return to connecting screen
            }
            else if(gameData.currentState == GameData.State.GAME_FINISHED && llh.getCurrentStateState() == Flags.CurrentStateState.GAME_FINISHED){
                //do something with llh.getGameFinishRating();
                if(!wroteGameFinished) {
                    System.out.println("Game is finished");
                    worldMapRenderer.getGameUiManager().showFinishWindow();
                    wroteGameFinished = true;
                }
            }
            else if (gameData.currentState == GameData.State.WORLD_UPDATE) {
                this.myGame.setScreen(this.worldMapRenderer);
            } else if (gameData.currentState == GameData.State.INVESTMENTS && llh.getMyId() == gameData.currentPlayer) {
                if (llh.getCurrentStateState() == Flags.CurrentStateState.PLAYER_STAGE) {
                    if (worldMapRenderer.getGameUiManager().showInvestWindow()) {
                        llh.setWaitingForPlayerChoiceFlag();
                    }
                }
            } else if (gameData.currentState == GameData.State.AUCTION && llh.getMyId() == gameData.currentPlayer) {
                if (llh.getCurrentStateState() == Flags.CurrentStateState.PLAYER_STAGE) {
                    if (worldMapRenderer.getGameUiManager().showAuctionWindow()) {
                        llh.setWaitingForPlayerChoiceFlag();
                    }
                }
            } else if (gameData.currentState == GameData.State.ABILITIES && llh.getMyId() == gameData.currentPlayer) {
                if (llh.getCurrentStateState() == Flags.CurrentStateState.PLAYER_STAGE) {
                    if (worldMapRenderer.getGameUiManager().showAbilitiesWindow()) {
                        llh.setWaitingForPlayerChoiceFlag();
                    }
                }
            } else if (gameData.currentState == GameData.State.PLANES && llh.getMyId() == gameData.currentPlayer) {
                if (llh.getCurrentStateState() == Flags.CurrentStateState.PLAYER_STAGE) {
                    if (gameData.players.get(gameData.currentPlayer).actionPoints <= 0) {
                        llh.sendPlanePass();
                        worldMapRenderer.getGameUiManager().showNotificationWindow("You are out of Action Points.\n You can not buy a plane.\nAutomatically Passing");
                        llh.setWaitingForPlayerChoiceFlag();
                    }
                    else if(worldMapRenderer.getGameUiManager().showPlaneWindow()) {
                        llh.setWaitingForPlayerChoiceFlag();
                    }
                }
            } else if (gameData.currentState == GameData.State.AIRLINES && llh.getMyId() == gameData.currentPlayer) {
                if (llh.getCurrentStateState() == Flags.CurrentStateState.PLAYER_STAGE) {
                    if (gameData.players.get(gameData.currentPlayer).actionPoints <= 0) {
                        llh.sendAirlinePass();
                        worldMapRenderer.getGameUiManager().showNotificationWindow("You are out of Action Points.\n You can not buy an airline.\nAutomatically Passing");
                    }
                    else
                        worldMapRenderer.getGameUiManager().showNotificationWindow("Your turn. Buy the airline.");


                    llh.setWaitingForPlayerChoiceFlag();
                }
            } else if (gameData.currentState == GameData.State.EVENT && llh.getMyId() == gameData.currentPlayer) {
                //mainDrawer.eventWindow();
            } else if (gameData.currentState == GameData.State.FLIGHTS && llh.getMyId() == gameData.currentPlayer) {
                if (llh.getCurrentStateState() == Flags.CurrentStateState.PLAYER_STAGE) {
                    if (gameData.players.get(gameData.currentPlayer).actionPoints <= 0) {
                        llh.sendRoutePass();
                        worldMapRenderer.getGameUiManager().showNotificationWindow("You are out of Action Points.\n You can not make a route.\nAutomatically Passing");
                    }

                    llh.setWaitingForPlayerChoiceFlag();
                }
            } else if (gameData.currentState == GameData.State.INCOME_AND_TAXES && llh.getMyId() == gameData.currentPlayer) {
                // change Income
            }
        }
        worldMapRenderer.renderNoLogic(delta);
    }

    public void bankruptcyMessage(){
        //show notification
    }

    public void gameDataWasUpdated(){
        worldMapRenderer.getGameUiManager().updateHUDData();
    }

    public void mainPreparationCycle(float delta) {
        llh.update();
        gameData.acquireReadLock();
        GamePreparationStage();
        gameData.releaseReadLock();
    }


    private void GamePreparationStage() {
        if(llh.getGamePreparationState() == Flags.GamePreparationsState.WAITING_FOR_CONNECT_CALL) {
            if(!llh.connectToServer()){
                this.myGame.setScreen(this.mainMenuRenderer);
            }
        }
        else if(llh.getGamePreparationState() == Flags.GamePreparationsState.READY_TO_JOIN_THE_GAME) {
            if (llh.getJoinGameResponse() == null) {
                connectionRenderer.showNicknameInput();
            }
            else if(llh.getJoinGameResponse() == Network.JoinGameResponse.Response.NAME_ALREADY_EXISTS){
                llh.truncateJoinGameResponse();
                connectionRenderer.showMessageWindow("Name already exists");
            }
            else if(llh.getJoinGameResponse() == Network.JoinGameResponse.Response.GAME_IS_RUNNING){
                mainMenuClient.killMainClient();
            }
        }
        else if(llh.getGamePreparationState() == Flags.GamePreparationsState.WAITING_FOR_SERVER_RESPONSE){
                connectionRenderer.showLoadingScreen("Waiting for server response");
        }
        else if(llh.getGamePreparationState() == Flags.GamePreparationsState.WAITING_FOR_OTHER_PLAYERS_TO_JOIN){
            connectionRenderer.showLoadingScreen("Waiting for other players");
        }
    }

    public void changeScreenToRunning(){
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                worldMapRenderer = new WorldMapRenderer(MainClient.this);
                myGame.setScreen(worldMapRenderer);
            }
        });
    }

    public GameData getGameData(){return gameData;}

    public LowLevelHandlerFront getLlh() {
        return this.llh;
    }

    public Game getMyGame() {
        return myGame;
    }
}
