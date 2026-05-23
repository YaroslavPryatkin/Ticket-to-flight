package com.game.Ticket_To_Flight.frontend;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
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
    private ConnectionRenderer connectionRenderer;

    public MainClient(Game gm){
        this.myGame = gm;
        this.connectionRenderer = new ConnectionRenderer(myGame, llh, this);
        this.myGame.setScreen(this.connectionRenderer);
    }

    public void mainCycleWithUpdate(float delta) {
        llh.update();
        gameData.acquireReadLock();
        mainCycle(delta);
        gameData.releaseReadLock();
    }

    private void mainCycle(float delta){
        if(llh.flags.gamePreparationsState == Flags.GamePreparationsState.RUNNING) {
            if (gameData.currentState == GameData.State.WORLD_UPDATE) {
                this.myGame.setScreen(this.worldMapRenderer);
            } else if (gameData.currentState == GameData.State.INVESTMENTS && llh.getMyId() == gameData.currentPlayer) {
                if (llh.flags.currentStateState == Flags.CurrentStateState.PLAYER_STAGE) {
                    worldMapRenderer.drawInvestmentWindow();
                    llh.flags.currentStateState = LowLevelHandlerFront.Flags.CurrentStateState.WAITING_FOR_PLAYER_CHOICE;
                }
            } else if (gameData.currentState == GameData.State.AUCTION && llh.getMyId() == gameData.currentPlayer) {
                if (llh.flags.currentStateState == Flags.CurrentStateState.PLAYER_STAGE) {
                    worldMapRenderer.drawAuctionWindow();
                    llh.flags.currentStateState = Flags.CurrentStateState.WAITING_FOR_PLAYER_CHOICE;
                }
                worldMapRenderer.drawAuctionWindow();
            } else if (gameData.currentState == GameData.State.ABILITIES && llh.getMyId() == gameData.currentPlayer) {
                if (llh.flags.currentStateState == Flags.CurrentStateState.PLAYER_STAGE) {
                    worldMapRenderer.drawAbilitiesWindow();
                    llh.flags.currentStateState = Flags.CurrentStateState.WAITING_FOR_PLAYER_CHOICE;
                }
            } else if (gameData.currentState == GameData.State.PLANES && llh.getMyId() == gameData.currentPlayer) {
                if (llh.flags.currentStateState == Flags.CurrentStateState.PLAYER_STAGE) {
                    worldMapRenderer.drawPlaneWindow();
                    llh.flags.currentStateState = Flags.CurrentStateState.WAITING_FOR_PLAYER_CHOICE;
                }
            } else if (gameData.currentState == GameData.State.AIRLINES && llh.getMyId() == gameData.currentPlayer) {
                // mainDrawer.reDrawAirlinesWindow();
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


    public void mainPreparationCycle(float delta) {
        llh.update();
        gameData.acquireReadLock();
        GamePreparationStage();
        gameData.releaseReadLock();
    }


    private void GamePreparationStage() {
        if(llh.flags.gamePreparationsState == Flags.GamePreparationsState.WAITING_FOR_CONNECT_CALL) {
            llh.connectToServer();
        }
        else if(llh.flags.gamePreparationsState == Flags.GamePreparationsState.SEARCHING_FOR_SERVER) {
            connectionRenderer.showLoadingScreen("Searching for server");
        }
        else if(llh.flags.gamePreparationsState == Flags.GamePreparationsState.READY_TO_JOIN_THE_GAME) {
            if (llh.flags.joinGameResponse == null) {
                connectionRenderer.showNicknameInput();
            }
            else if(llh.flags.joinGameResponse == Network.JoinGameResponse.Response.NAME_ALREADY_EXISTS){
                connectionRenderer.setInputIsPrinted(false);
                connectionRenderer.showNicknameInput();
            }

        }
        else if(llh.flags.gamePreparationsState == Flags.GamePreparationsState.WAITING_FOR_SERVER_RESPONSE){
            connectionRenderer.showLoadingScreen("Waiting for server response");
        }
        else if(llh.flags.gamePreparationsState == Flags.GamePreparationsState.WAITING_FOR_OTHER_PLAYERS_TO_JOIN){
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
