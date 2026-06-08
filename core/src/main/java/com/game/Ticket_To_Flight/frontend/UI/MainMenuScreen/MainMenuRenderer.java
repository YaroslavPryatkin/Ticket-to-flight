package com.game.Ticket_To_Flight.frontend.UI.MainMenuScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class MainMenuRenderer extends ScreenAdapter {
    public static boolean isMusic = true;
    private Music backgroundMusic;
    private final Stage stage;
    private final Skin skin;
    private final MainMenuClient mainMenuClient;
    private final MainMenuUI mainMenuUI;

    public MainMenuRenderer(MainMenuClient mainMenuClient) {
        this.mainMenuClient = mainMenuClient;
        this.stage = new Stage(new FitViewport(1920, 1080));
        this.skin = MainMenuSkinFactory.createMenuSkin();

        this.mainMenuUI = new MainMenuUI(skin, mainMenuClient);
        stage.addActor(mainMenuUI.getTable());

        this.backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("Presets/Preset1/audio/music/Satisfactory_music.mp3"));
        this.backgroundMusic.setLooping(true);
        this.backgroundMusic.setVolume(0.5f);

        this.mainMenuUI.setOnBlurMusicClicked(() -> {
            isMusic = !isMusic;

            if (backgroundMusic != null) {
                if (isMusic) {
                    backgroundMusic.play();
                } else {
                    backgroundMusic.pause();
                }
            }
        });
    }

    @Override
    public void render(float delta) {
        this.renderNoLogic(delta);
    }

    private void renderNoLogic(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.15f, 0.25f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        backgroundMusic.dispose();
    }

    @Override
    public void hide() {
        backgroundMusic.pause();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        backgroundMusic.play();
    }
}
