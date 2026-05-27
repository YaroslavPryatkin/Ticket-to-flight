package com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.frontend.MainClient;
import com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.MapStrategies.MapInteractionStrategy;

public class WorldMapRenderer extends ScreenAdapter {
    private final MainClient client;
    private final GameData gameData;

    private final MapDrawer mapDrawer;
    private final MapSelectionState selectionState;

    private final Stage uiStageWindow;
    private final Stage uiStageHUD;

    private final GameUIManager uiManager;
    private final MapInputController inputCtrl;
    private final InputMultiplexer multiplexer;

    private void showUiStage(Stage stage, float delta) {
        stage.getViewport().apply();
        stage.act(delta);
        stage.draw();
    }

    public WorldMapRenderer(MainClient client) {
        this.client = client;
        this.gameData = client.getGameData();

        this.selectionState = new MapSelectionState();
        this.mapDrawer = new MapDrawer(selectionState);

        this.uiStageWindow = new Stage(new FitViewport(1920, 1080));
        this.uiStageHUD = new Stage(new ExtendViewport(1920, 1080));

        this.uiManager = new GameUIManager(uiStageWindow, uiStageHUD, client, selectionState, mapDrawer.getCamera());

        this.inputCtrl = new MapInputController(mapDrawer.getCamera(), gameData, uiManager, client, selectionState);

        this.multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(uiStageWindow);
        multiplexer.addProcessor(uiStageHUD);
        multiplexer.addProcessor(inputCtrl);
    }

    public void updateHUDData() {
        uiManager.updateHUDData();
    }

    public boolean drawInvestmentWindow() { return uiManager.showInvestWindow(); }
    public boolean drawAuctionWindow() { return uiManager.showAuctionWindow(); }
    public boolean drawPlaneWindow() { return uiManager.showPlaneWindow(); }
    public boolean drawAbilitiesWindow() { return uiManager.showAbilitiesWindow(); }
    public void drawSuccessWindow(String message) { uiManager.showSuccessWindow(message); }

    public void setMapCurrentStrategy(MapInteractionStrategy currentStrategy) {
        inputCtrl.setCurrentStrategy(currentStrategy);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void render(float delta) {
        client.mainCycleWithUpdate(delta);
    }

    public void renderNoLogic(float delta) {
        inputCtrl.updateCurrentStrategy();
        mapDrawer.render(gameData);
        uiManager.updateDynamicControls();
        showUiStage(uiStageHUD, delta);
        showUiStage(uiStageWindow, delta);
    }

    @Override
    public void resize(int width, int height) {
        mapDrawer.resize(width, height);
        uiStageWindow.getViewport().update(width, height, true);
        uiStageHUD.getViewport().update(width, height, true);
        uiManager.resize(width, height);
    }

    @Override
    public void dispose() {
        mapDrawer.dispose();
        uiStageWindow.dispose();
        uiStageHUD.dispose();
    }
}
