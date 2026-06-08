package com.game.Ticket_To_Flight.frontend.UI.ConnectionScreen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.game.Ticket_To_Flight.frontend.LowLevelHandlerFront;
import com.game.Ticket_To_Flight.frontend.MainClient;
import com.game.Ticket_To_Flight.frontend.UI.ConnectionScreen.ConnectionScreenRenderer.StyleFactoryConnection;
import com.game.Ticket_To_Flight.frontend.UI.MainMenuScreen.MainMenuRenderer;
import com.game.Ticket_To_Flight.frontend.components.background.Background;

public class ConnectionRenderer extends ScreenAdapter {
    private Music backgroundMusic;
    private final Game game;
    private final LowLevelHandlerFront llh;
    private final MainClient mainClient;

    private final Stage uiStage;
    private final Skin skin;
    private final ConnectionUIManager uiManager;

    private String lastLoadingScreenTitle = null;

    public ConnectionRenderer(Game game, LowLevelHandlerFront llh, MainClient mainClient) {
        this.game = game;
        this.llh = llh;
        this.mainClient = mainClient;

        this.uiStage = new Stage(new FitViewport(1920, 1080));
        this.skin = new StyleFactoryConnection().createConnectionSkin();
        this.uiManager = new ConnectionUIManager(skin, llh);

        this.uiStage.addActor(uiManager);
        if (MainMenuRenderer.isMusic == true && backgroundMusic != null) {
            this.backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("Presets/Preset1/audio/music/Satisfactory_music.mp3"));
            this.backgroundMusic.setLooping(true);
            this.backgroundMusic.setVolume(0.5f);
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(uiStage);
        backgroundMusic.play();
    }

    public void showLoadingScreen(String message) {
        if (message.equals(lastLoadingScreenTitle))
            return;
        uiManager.shouldShowNicknameInput = true;
        //System.out.println("Hello");
        uiManager.showLoadingScreen(message);
        lastLoadingScreenTitle = message;
    }

    public void showNicknameInput() {
        uiManager.showNicknameInputScreen();
    }


    public void showMessageWindow(String message) {
        uiManager.showMessageWindow("", message);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mainClient.mainPreparationCycle(delta);

        uiStage.act(delta);
        uiStage.draw();
    }

    @Override
    public void resize(int width, int height) {
        uiStage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        uiStage.dispose();
        skin.dispose();
    }

    @Override
    public void hide() {
        backgroundMusic.pause();
    }
}
