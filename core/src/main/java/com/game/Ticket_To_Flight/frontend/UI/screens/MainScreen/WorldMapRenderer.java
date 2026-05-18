package com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.game.Ticket_To_Flight.PresetPaths;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airline;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.frontend.MainClient;

public class WorldMapRenderer extends ScreenAdapter {
    private final SpriteBatch batch;
    private final Texture mapTexture;
    private final Texture airportTexture;
    private final Texture airlineTexture;

    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final float WORLD_WIDTH;
    private final float WORLD_HEIGHT;

    private final Stage uiStage;
    private final GameUIManager uiManager;
    private final MapInputController inputCtrl;

    private final MainClient client;
    private final GameData gameData;

    public WorldMapRenderer(MainClient client) {
        this.client = client;
        this.gameData = client.getGameData();

        this.WORLD_WIDTH = Gdx.graphics.getWidth();
        this.WORLD_HEIGHT = Gdx.graphics.getHeight();

        this.batch = new SpriteBatch();

        this.mapTexture = new Texture(Gdx.files.internal(PresetPaths.presetPaths.get(1)));

        int baseRadius = 32;
        Pixmap pixmap = new Pixmap(baseRadius * 2, baseRadius * 2, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fillCircle(baseRadius, baseRadius, baseRadius);
        this.airportTexture = new Texture(pixmap);
        this.airportTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        pixmap.dispose();

        Pixmap linePixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        linePixmap.setColor(Color.WHITE);
        linePixmap.fill();
        this.airlineTexture = new Texture(linePixmap);
        linePixmap.dispose();

        this.camera = new OrthographicCamera();
        this.viewport = new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        this.uiStage = new Stage(new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT));

        this.uiManager = new GameUIManager(uiStage, client);
        this.inputCtrl = new MapInputController(camera, gameData, uiManager);

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(uiStage);
        multiplexer.addProcessor(inputCtrl);
        Gdx.input.setInputProcessor(multiplexer);
    }

    private void clampCamera() {
        float maxZoomX = WORLD_WIDTH / viewport.getWorldWidth();
        float maxZoomY = WORLD_HEIGHT / viewport.getWorldHeight();
        float maxSafeZoom = Math.min(maxZoomX, maxZoomY);

        camera.zoom = MathUtils.clamp(camera.zoom, 0.3f, maxSafeZoom);

        float viewWidth = viewport.getWorldWidth() * camera.zoom;
        float viewHeight = viewport.getWorldHeight() * camera.zoom;

        float halfViewWidth = viewWidth / 2f;
        float halfViewHeight = viewHeight / 2f;

        camera.position.x = MathUtils.clamp(camera.position.x, halfViewWidth, WORLD_WIDTH - halfViewWidth);
        camera.position.y = MathUtils.clamp(camera.position.y, halfViewHeight, WORLD_HEIGHT - halfViewHeight);
    }

    public void addAirportsOnTheMap() {
        for (Airport airport : gameData.airports) {
            batch.setColor(airport.getColor());
            float currentRadius = airport.getRadius();
            float diameter = currentRadius * 2f;
            float drawX = airport.getX() - currentRadius;
            float drawY = airport.getY() - currentRadius;

            batch.draw(airportTexture, drawX, drawY, diameter, diameter);
        }
    }

    public void addAirlinesOnTheMap() {
        float lineThickness = 6f;
        for (Airline airline : gameData.airlines) {
            if (airline.getPlayer() != null) {
                batch.setColor(airline.getPlayer().getColor());
            } else {
                batch.setColor(Color.LIGHT_GRAY);
            }

            Airport a = airline.getPortA();
            Airport b = airline.getPortB();

            float dx = b.getX() - a.getX();
            float dy = b.getY() - a.getY();
            float length = (float) Math.sqrt(dx * dx + dy * dy);
            float angle = (float) Math.toDegrees(Math.atan2(dy, dx));

            batch.draw(airlineTexture,
                a.getX(), a.getY() - lineThickness / 2f,
                0, lineThickness / 2f,
                length, lineThickness,
                1f, 1f,
                angle,
                0, 0, 1, 1, false, false);
        }
    }

    public void showInvestWindow() {
        uiManager.showInvestWindow();
    }

    public void showAuctionWindow() {
        uiManager.showAuctionWindow();
    }

    @Override
    public void render(float delta) {
        client.mainCycleWithUpdate(delta);
        renderNoLogic(delta);
        gameData.releaseReadLock();
    }

    public void renderNoLogic(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        clampCamera();
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        batch.draw(mapTexture, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        addAirlinesOnTheMap();
        addAirportsOnTheMap();
        batch.setColor(Color.WHITE); // Сбрасываем цвет после отрисовки линий и аэропортов
        batch.end();

        uiStage.act(delta);
        uiStage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        uiStage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        batch.dispose();
        mapTexture.dispose();
        if (airportTexture != null) airportTexture.dispose();
        if (airlineTexture != null) airlineTexture.dispose();
        uiStage.dispose();
    }
}
