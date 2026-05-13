package com.game.Ticket_To_Flight.frontend.UI.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;
import com.game.Ticket_To_Flight.packages.PackageCreateWorldMap;

import java.util.ArrayList;
import java.util.List;

public class WorldMapRenderer extends ScreenAdapter {
    private final SpriteBatch batch;
    private final Texture mapTexture;

    private final Texture airportTexture;
    private List<Airport> airportsToDraw = new ArrayList<>();
    private final float AIRPORT_RADIUS = 15f; // Радиус точки города в координатах карты

    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final float WORLD_WIDTH;
    private final float WORLD_HEIGHT;
    private final Vector3 lastMousePos = new Vector3();

    public WorldMapRenderer(PackageCreateWorldMap packet) {
        this.batch = new SpriteBatch();
        this.WORLD_WIDTH = packet.worldWidth;
        this.WORLD_HEIGHT = packet.worldHeight;
        this.mapTexture = new Texture(Gdx.files.internal(packet.mapTextureName));
        this.camera = new OrthographicCamera();
        this.viewport = new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);

        int pixmapRadius = (int) AIRPORT_RADIUS;
        Pixmap pixmap = new Pixmap(pixmapRadius * 2, pixmapRadius * 2, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fillCircle(pixmapRadius, pixmapRadius, pixmapRadius);
        this.airportTexture = new Texture(pixmap);
        pixmap.dispose();

        setupInput();
    }

    public void updateAirportsData(List<Airport> airports) {
        this.airportsToDraw = airports;
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

    private void setupInput() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                lastMousePos.set(screenX, screenY, 0);
                return true;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                float deltaX = lastMousePos.x - screenX;
                float deltaY = screenY - lastMousePos.y;

                camera.translate(deltaX * camera.zoom, deltaY * camera.zoom);
                lastMousePos.set(screenX, screenY, 0);
                return true;
            }

            @Override
            public boolean scrolled(float amountX, float amountY) {
                camera.zoom += amountY * 0.1f;
                return true;
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        clampCamera();
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        batch.draw(mapTexture, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);

        for (Airport airport : airportsToDraw) {
            batch.setColor(airport.getColor());

            float drawX = airport.getX() - AIRPORT_RADIUS;
            float drawY = airport.getY() - AIRPORT_RADIUS;

            batch.draw(airportTexture, drawX, drawY, AIRPORT_RADIUS * 2, AIRPORT_RADIUS * 2);
        }

        batch.setColor(Color.WHITE);

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        batch.dispose();
        mapTexture.dispose();
        if (airportTexture != null) airportTexture.dispose();
    }
}
