package com.game.Ticket_To_Flight.frontend;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.game.Ticket_To_Flight.MainMenu.MainMenuClient;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.MainMenu.MainMenuRenderer;
import com.game.Ticket_To_Flight.frontend.UI.screens.ConnectionScreen.ConnectionRenderer;
import com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.WorldMapRenderer;
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

    private void mainCycle(float delta){
        if(llh.getGamePreparationState() == Flags.GamePreparationsState.RUNNING) {
            if(llh.getCurrentStateState() == Flags.CurrentStateState.SERVER_DISCONNECTED){
                //stop game and return to connecting screen
            }
            else if (gameData.currentState == GameData.State.WORLD_UPDATE) {
                this.myGame.setScreen(this.worldMapRenderer);
            } else if (gameData.currentState == GameData.State.INVESTMENTS && llh.getMyId() == gameData.currentPlayer) {
                if (llh.getCurrentStateState() == Flags.CurrentStateState.PLAYER_STAGE) {
                    if (worldMapRenderer.drawInvestmentWindow()) {
                        llh.setWaitingForPlayerChoiceFlag();
                    }
                }
            } else if (gameData.currentState == GameData.State.AUCTION && llh.getMyId() == gameData.currentPlayer) {
                if (llh.getCurrentStateState() == Flags.CurrentStateState.PLAYER_STAGE) {
                    if (worldMapRenderer.drawAuctionWindow()) {
                        llh.setWaitingForPlayerChoiceFlag();
                    }
                }
                worldMapRenderer.drawAuctionWindow();
            } else if (gameData.currentState == GameData.State.ABILITIES && llh.getMyId() == gameData.currentPlayer) {
                if (llh.getCurrentStateState() == Flags.CurrentStateState.PLAYER_STAGE) {
                    if (worldMapRenderer.drawAbilitiesWindow()) {
                        llh.setWaitingForPlayerChoiceFlag();
                    }
                }
            } else if (gameData.currentState == GameData.State.PLANES && llh.getMyId() == gameData.currentPlayer) {
                if (llh.getCurrentStateState() == Flags.CurrentStateState.PLAYER_STAGE) {
                    if (worldMapRenderer.drawPlaneWindow()) {
                        llh.setWaitingForPlayerChoiceFlag();
                    }
                }
            } else if (gameData.currentState == GameData.State.AIRLINES && llh.getMyId() == gameData.currentPlayer) {

            } else if (gameData.currentState == GameData.State.EVENT && llh.getMyId() == gameData.currentPlayer) {
                //mainDrawer.eventWindow();
            } else if (gameData.currentState == GameData.State.FLIGHTS && llh.getMyId() == gameData.currentPlayer) {
                // smth
            } else if (gameData.currentState == GameData.State.INCOME && llh.getMyId() == gameData.currentPlayer) {
                // change Income
            } else if (gameData.currentState == GameData.State.TAXES && llh.getMyId() == gameData.currentPlayer) {
                // Taxes
            }
        }
        worldMapRenderer.renderNoLogic(delta);
    }

    public void gameDataWasUpdated(){
        worldMapRenderer.updateHUDData();
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
}
