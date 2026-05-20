package com.game.Ticket_To_Flight.frontend.UI.screens.MainMenu;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.game.Ticket_To_Flight.frontend.MainClient;
import com.sun.tools.javac.Main;

public class MainMenuRenderer extends ScreenAdapter {
    private final Stage stage;
    private final Skin skin;
    private final MainClient mainClient;

    public MainMenuRenderer(MainClient mainClient) {
        this.mainClient = mainClient;
        this.stage = new Stage(new ScreenViewport());
        this.skin = createMenuSkin();

        Gdx.input.setInputProcessor(stage);

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.center();

        Label titleLabel = new Label("Ticket to Flight", skin);
        titleLabel.setFontScale(2.5f);
        mainTable.add(titleLabel).padBottom(50).row();

        TextButton createServerBtn = new TextButton("Create server", skin);
        TextButton connectServerBtn = new TextButton("Connect to server", skin);

        createServerBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Create server clicked");
                // TODO: Логика запуска MainLogic сервера
            }
        });

        connectServerBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Connect to server clicked");
                // TODO: Инициализация MainClient и переход на игровой экран
            }
        });

        mainTable.add(createServerBtn).width(280).height(55).padBottom(20).row();
        mainTable.add(connectServerBtn).width(280).height(55);

        stage.addActor(mainTable);
    }

    private Skin createMenuSkin() {
        Skin menuSkin = new Skin();
        menuSkin.add("default-font", new BitmapFont());

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = menuSkin.getFont("default-font");
        labelStyle.fontColor = Color.WHITE;
        menuSkin.add("default", labelStyle);

        Pixmap btnUp = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        btnUp.setColor(new Color(0.15f, 0.15f, 0.15f, 1f));
        btnUp.fill();
        menuSkin.add("btn-up", new Texture(btnUp));
        btnUp.dispose();

        Pixmap btnDown = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        btnDown.setColor(new Color(0.25f, 0.25f, 0.25f, 1f));
        btnDown.fill();
        menuSkin.add("btn-down", new Texture(btnDown));
        btnDown.dispose();

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = menuSkin.getFont("default-font");
        btnStyle.fontColor = Color.WHITE;
        btnStyle.up = menuSkin.getDrawable("btn-up");
        btnStyle.down = menuSkin.getDrawable("btn-down");
        menuSkin.add("default", btnStyle);

        return menuSkin;
    }

    @Override
    public void render(float delta) {
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
