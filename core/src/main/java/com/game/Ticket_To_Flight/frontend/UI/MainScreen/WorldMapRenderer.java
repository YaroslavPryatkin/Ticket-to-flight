package com.game.Ticket_To_Flight.frontend.UI.MainScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.commonFrontAndBack.RatingRecord;
import com.game.Ticket_To_Flight.frontend.MainClient;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.MapInput.MapInputController;

public class WorldMapRenderer extends ScreenAdapter {
    private final MainClient client;
    private final GameData gameData;

    private final MapDrawer mapDrawer;

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
        this.uiStageWindow = new Stage(new FitViewport(2750, 1536));
        this.uiStageHUD = new Stage(new ExtendViewport(2750, 1536));

        OrthographicCamera mainCamera = new OrthographicCamera();
        this.uiManager = new GameUIManager(uiStageWindow, uiStageHUD, client, mainCamera);

        this.mapDrawer = new MapDrawer(gameData, mainCamera, uiManager);
        this.inputCtrl = new MapInputController(mainCamera, gameData, uiManager, client);

        this.multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(uiStageWindow);
        multiplexer.addProcessor(uiStageHUD);
        multiplexer.addProcessor(inputCtrl);
    }

    public GameUIManager getGameUiManager(){return uiManager;}

    @Override
    public void show() {
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void render(float delta) {
        client.mainCycleWithUpdate(delta);
    }

    public void renderNoLogic(float delta) {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        mapDrawer.render(delta);

        uiManager.updateDynamicControls();

        showUiStage(uiStageHUD, delta);
        showUiStage(uiStageWindow, delta);
    }

    @Override
    public void resize(int width, int height) {
        mapDrawer.resize(width, height);
        uiStageWindow.getViewport().update(width, height, true);
        uiStageHUD.getViewport().update(width, height, true);
        uiManager.resize();
    }

    @Override
    public void dispose() {
        mapDrawer.dispose();
        uiStageWindow.dispose();
        uiStageHUD.dispose();
    }
}
