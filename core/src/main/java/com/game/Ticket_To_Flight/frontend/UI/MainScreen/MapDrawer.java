package com.game.Ticket_To_Flight.frontend.UI.MainScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.game.Ticket_To_Flight.PresetPaths;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airline;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.MapInput.MapSelectionState;

public class MapDrawer {
    private final SpriteBatch batch;
    private final Texture mapTexture;
    private final Texture airportTexture;
    private final Texture airlineTexture;
    private final GameData gameData;

    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final float WORLD_WIDTH;
    private final float WORLD_HEIGHT;
    private final MapSelectionState selectionState;

    public MapDrawer(MapSelectionState selectionState, GameData gameData) {
        this.gameData = gameData;

        this.batch = new SpriteBatch();
        this.selectionState = selectionState;
        this.mapTexture = new Texture(Gdx.files.internal(PresetPaths.presetPaths.get(1) + "map.png"));

        this.WORLD_WIDTH = 2750f;
        this.WORLD_HEIGHT = 1536f;

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

    private void addAirportsOnTheMap(GameData gameData) {
        for (Airport airport : gameData.airports) {
            batch.setColor(airport.getColor());
            float currentRadius = 15f;

            if (selectionState.isAirportSelected(airport)) {
                currentRadius *= 1.8f;
            }

            if (selectionState.isAirportFirst(airport)) {
                batch.setColor(Color.ROYAL);
            }

            float diameter = currentRadius * 2f;
            float drawX = airport.getX() - currentRadius;
            float drawY = airport.getY() - currentRadius;

            batch.draw(airportTexture, drawX, drawY, diameter, diameter);
        }
    }

    private void addAirlinesOnTheMap(GameData gameData) {
        float lineThickness = 6f;
        for (Airline airline : gameData.airlines) {
            Color airlineColor;
            if (airline.getPlayer() != null) {
                airlineColor = airline.getPlayer().getColor();
            } else {
                airlineColor = Color.LIGHT_GRAY;
            }
            if (selectionState.isAirlineSelected(airline) || selectionState.isAirlineInRoute(airline)) {
                airlineColor = new Color(airlineColor.r, airlineColor.g, airlineColor.b, 0.4f);
            }
            batch.setColor(airlineColor);

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

    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        clampCamera();
        camera.update();

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();
        batch.draw(mapTexture, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        addAirlinesOnTheMap(gameData);
        addAirportsOnTheMap(gameData);
        batch.setColor(Color.WHITE);
        batch.end();
    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public void dispose() {
        batch.dispose();
        mapTexture.dispose();
        if (airportTexture != null) airportTexture.dispose();
        if (airlineTexture != null) airlineTexture.dispose();
    }
}
