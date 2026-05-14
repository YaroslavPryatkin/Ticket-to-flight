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
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Passenger;
import com.game.Ticket_To_Flight.packages.PackageCreateWorldMap;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

import java.util.ArrayList;
import java.util.List;

public class WorldMapRenderer extends ScreenAdapter {
    private final SpriteBatch batch;
    private final Texture mapTexture;

    private final Texture airportTexture;
    private List<Airport> airportsToDraw = new ArrayList<>();

    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final float WORLD_WIDTH;
    private final float WORLD_HEIGHT;
    private final Vector3 lastMousePos = new Vector3();
    private Stage uiStage;
    private Skin skin;
    private Window currentTooltip;
    private Airport selectedAirport;

    private void createBasicSkin() {
        skin = new Skin();
        skin.add("default-font", new BitmapFont()); // Стандартный шрифт LibGDX

        // Создаем темно-серый полупрозрачный фон для окна
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.2f, 0.2f, 0.2f, 0.8f));
        pixmap.fill();
        skin.add("background", new Texture(pixmap));
        pixmap.dispose();

        Window.WindowStyle windowStyle = new Window.WindowStyle();
        windowStyle.titleFont = skin.getFont("default-font");
        windowStyle.background = skin.getDrawable("background");
        skin.add("default", windowStyle);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = skin.getFont("default-font");
        labelStyle.fontColor = Color.WHITE;
        skin.add("default", labelStyle);
    }

    private void showAirportTooltip(Airport airport) {
        // Если уже было открыто окно - удаляем старое
        if (currentTooltip != null) currentTooltip.remove();

        selectedAirport = airport;

        currentTooltip = new Window(airport.getCityName(), skin);
        currentTooltip.pad(20); // Отступы от краев

        Table table = new Table();
        table.add(new Label("Route", skin)).padRight(10);
        table.add(new Label("Group", skin));
        table.row();

        if (airport.getGuests() != null && !airport.getGuests().isEmpty()) {
            for (Passenger group : airport.getGuests()) {
                String routeText = group.getCityFrom() + " -> " + group.getCityTo();
                String countText = group.getSize() + " people";

                table.add(new Label(routeText, skin)).padRight(10);
                table.add(new Label(countText, skin));
                table.row();
            }
        } else {
            table.add(new Label("No guests", skin)).colspan(2);
        }

        currentTooltip.add(table);
        currentTooltip.pack();
        uiStage.addActor(currentTooltip);
    }

    public WorldMapRenderer(PackageCreateWorldMap packet) {
        this.batch = new SpriteBatch();
        this.WORLD_WIDTH = packet.worldWidth;
        this.WORLD_HEIGHT = packet.worldHeight;
        this.mapTexture = new Texture(Gdx.files.internal(packet.mapTextureName));
        this.camera = new OrthographicCamera();
        this.viewport = new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);

        int baseRadius = 32;
        Pixmap pixmap = new Pixmap(baseRadius * 2, baseRadius * 2, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fillCircle(baseRadius, baseRadius, baseRadius);
        this.airportTexture = new Texture(pixmap);

        this.airportTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        pixmap.dispose();

        this.uiStage = new Stage(new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        createBasicSkin();

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
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(uiStage);

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                Vector3 worldClick = new Vector3(screenX, screenY, 0);
                camera.unproject(worldClick);

                boolean hitAirport = false;
                for (Airport airport : airportsToDraw) {
                    float distance = Vector2.dst(airport.getX(), airport.getY(), worldClick.x, worldClick.y);

                    if (distance <= airport.getRadius()) {
                        showAirportTooltip(airport);
                        hitAirport = true;
                        break;
                    }
                }

                if (!hitAirport && currentTooltip != null) {
                    currentTooltip.remove();
                    currentTooltip = null;
                    selectedAirport = null;
                }

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
            float currentRadius = airport.getRadius();
            float diameter = currentRadius * 2f;
            float drawX = airport.getX() - currentRadius;
            float drawY = airport.getY() - currentRadius;

            batch.draw(airportTexture, drawX, drawY, diameter, diameter);
        }

        batch.setColor(Color.WHITE);

        batch.end();

        if (currentTooltip != null && selectedAirport != null) {
            Vector3 screenPos = new Vector3(selectedAirport.getX(), selectedAirport.getY(), 0);
            camera.project(screenPos);
            currentTooltip.setPosition(screenPos.x + 20, screenPos.y + 20);
        }

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
    }
}
