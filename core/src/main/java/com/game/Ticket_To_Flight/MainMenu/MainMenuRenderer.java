package com.game.Ticket_To_Flight.MainMenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainMenuRenderer extends ScreenAdapter {
    private final Stage stage;
    private final Skin skin;
    private final MainMenuClient mainMenuClient;

    public MainMenuRenderer(MainMenuClient mainMenuClient) {
        this.mainMenuClient = mainMenuClient;
        this.stage = new Stage(new ScreenViewport());
        this.skin = MainMenuSkinFactory.createMenuSkin();

        Gdx.input.setInputProcessor(stage);

        MainMenuUI mainMenuUI = new MainMenuUI(skin, mainMenuClient);
        stage.addActor(mainMenuUI.getTable());
    }

    @Override
    public void render(float delta) {
        this.renderNoLogic(delta);
    }

    private void renderNoLogic(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.2f, 0.5f, 1f);
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
    }
}
