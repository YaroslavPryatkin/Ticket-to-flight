package com.game.Ticket_To_Flight.frontend.UI.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.game.Ticket_To_Flight.Utilities.MapHolder;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airline;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PassengerType;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.packages.PackageCreateWorldMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WorldMapRenderer extends ScreenAdapter {
   // private final GameData gameData;
    private final SpriteBatch batch;

    private final Texture mapTexture;
    private final Texture airportTexture;
    private final Texture airlineTexture;

    private List<Airport> airportsToDraw = new ArrayList<>();
    private List<Airline> airlinesToDraw = new ArrayList<>();

    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final float WORLD_WIDTH;
    private final float WORLD_HEIGHT;

    private final Vector3 lastMousePos = new Vector3();
    private final Vector3 screenPos = new Vector3();

    private Stage uiStage;
    private Skin skin;
    private Window currentTooltip;

    private Airport selectedAirport;
    private Airline selectedAirline;

    private boolean isBuyingPhase = true;
    private double currentPlayerMoney = 1000.0;
    private double currentPlayerIncome = 50.0;

    private Label moneyLabel;
    private Label incomeLabel;



    public WorldMapRenderer(PackageCreateWorldMap packet) {
        //this.gameData = gameData;
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

        Pixmap linePixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        linePixmap.setColor(Color.WHITE);
        linePixmap.fill();
        this.airlineTexture = new Texture(linePixmap);
        linePixmap.dispose();

        this.uiStage = new Stage(new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

        createBasicSkin();
        createHUD();
        setupInput();
    }

    private void createBasicSkin() {
        skin = new Skin();
        skin.add("default-font", new BitmapFont());

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.2f, 0.2f, 0.2f, 0.8f));
        pixmap.fill();
        skin.add("background", new Texture(pixmap));

        Pixmap btnPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        btnPixmap.setColor(new Color(0.4f, 0.4f, 0.4f, 1f));
        btnPixmap.fill();
        skin.add("btn-up", new Texture(btnPixmap));

        Pixmap btnDisabledPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        btnDisabledPixmap.setColor(new Color(0.1f, 0.1f, 0.1f, 0.9f));
        btnDisabledPixmap.fill();
        skin.add("btn-disabled", new Texture(btnDisabledPixmap));

        pixmap.dispose();
        btnPixmap.dispose();
        btnDisabledPixmap.dispose();

        Window.WindowStyle windowStyle = new Window.WindowStyle();
        windowStyle.titleFont = skin.getFont("default-font");
        windowStyle.background = skin.getDrawable("background");
        skin.add("default", windowStyle);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = skin.getFont("default-font");
        labelStyle.fontColor = Color.WHITE;
        skin.add("default", labelStyle);

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = skin.getFont("default-font");
        btnStyle.fontColor = Color.WHITE;
        btnStyle.up = skin.getDrawable("btn-up");
        btnStyle.disabled = skin.getDrawable("btn-disabled");
        skin.add("default", btnStyle);
    }

    private void createHUD() {
        Table hudContainer = new Table();
        hudContainer.setFillParent(true);
        hudContainer.top().right();
        hudContainer.pad(10);

        Table statsPanel = new Table();
        statsPanel.pad(5);

        moneyLabel = new Label("Money: $" + currentPlayerMoney, skin);
        incomeLabel = new Label("Income: +$" + currentPlayerIncome, skin);

        incomeLabel.setColor(Color.GREEN);

        statsPanel.add(moneyLabel).left().row();
        statsPanel.add(incomeLabel).left().padTop(5).row();

        hudContainer.add(statsPanel);
        uiStage.addActor(hudContainer);
    }

    public void updateAirportData(List<Airport> airports) {
        this.airportsToDraw = airports;
    }

    public void updateAirlinesData(List<Airline> airlines) {
        this.airlinesToDraw = airlines;
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

                for (Airport airport : airportsToDraw) {
                    float distance = Vector2.dst(airport.getX(), airport.getY(), worldClick.x, worldClick.y);

                    if (distance <= airport.getRadius()) {
                        showAirportTooltip(airport);
                        return true;
                    }
                }

                float clickTolerance = 10f;
                for (Airline airline : airlinesToDraw) {
                    float distanceToLine = distanceToSegment(
                        worldClick.x, worldClick.y,
                        airline.getPortA().getX(), airline.getPortA().getY(),
                        airline.getPortB().getX(), airline.getPortB().getY()
                    );

                    if (distanceToLine <= clickTolerance) {
                        showAirlineTooltip(airline, worldClick.x, worldClick.y);
                        return true;
                    }
                }

                if (currentTooltip != null) {
                    currentTooltip.remove();
                    currentTooltip = null;
                    selectedAirport = null;
                    selectedAirline = null;
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

    private float distanceToSegment(float px, float py, float x1, float y1, float x2, float y2) {
        float A = px - x1;
        float B = py - y1;
        float C = x2 - x1;
        float D = y2 - y1;

        float dot = A * C + B * D;
        float len_sq = C * C + D * D;
        float param = -1;
        if (len_sq != 0)
            param = dot / len_sq;

        float xx, yy;

        if (param < 0) {
            xx = x1;
            yy = y1;
        } else if (param > 1) {
            xx = x2;
            yy = y2;
        } else {
            xx = x1 + param * C;
            yy = y1 + param * D;
        }

        float dx = px - xx;
        float dy = py - yy;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    private void showAirportTooltip(Airport airport) {
        if (currentTooltip != null) currentTooltip.remove();

        selectedAirport = airport;

        currentTooltip = new Window(airport.getCityName(), skin);
        currentTooltip.pad(20);

        Table table = new Table();
        table.add(new Label("Route", skin)).padRight(10);
        table.add(new Label("Group", skin));
        table.row();

        var guestsMap = airport.getGuests();

        if (guestsMap != null) {
            for (PassengerType type : guestsMap.getKeys()) {
                Integer groupCount = guestsMap.get(type);
                if (groupCount == null || groupCount == 0) continue;

                String passengerInfo = type.description;
                String countText = groupCount + " гр. (по " + type.size + " чел.)";

                table.add(new Label(passengerInfo, skin)).padRight(10).left();
                table.add(new Label(countText, skin)).right();
                table.row();
            }
        }
        else {
            table.add(new Label("No guests", skin)).colspan(2);
        }

        currentTooltip.add(table);
        currentTooltip.pack();
        uiStage.addActor(currentTooltip);
    }

    private void showAirlineTooltip(Airline airline, float clickX, float clickY) {
        if (currentTooltip != null) currentTooltip.remove();

        selectedAirport = null;
        selectedAirline = airline;

        currentTooltip = new Window("Route Details", skin);
        currentTooltip.pad(20);
        Table table = new Table();

        if (airline.getPlayer() != null) {
            table.add(new Label("Owned by: " + airline.getPlayer().getName(), skin));
        } else {
            TextButton buyButton = new TextButton("Buy for $" + airline.getPrice(), skin);

            if (!isBuyingPhase) {
                buyButton.setDisabled(true);
            }

            if (currentPlayerMoney < airline.getPrice()) {
                buyButton.getLabel().setColor(Color.RED);
            } else {
                buyButton.getLabel().setColor(Color.WHITE);
            }

            buyButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (buyButton.isDisabled() || currentPlayerMoney < airline.getPrice()) return;
                    System.out.println("Buying route!");
                }
            });

            table.add(buyButton).width(150).height(40);
        }

        currentTooltip.add(table);
        currentTooltip.pack();
        uiStage.addActor(currentTooltip);

        screenPos.set(clickX, clickY, 0);
        camera.project(screenPos);
        currentTooltip.setPosition(screenPos.x + 10, screenPos.y + 10);
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

        float lineThickness = 6f;
        for (Airline airline : airlinesToDraw) {
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

        if (moneyLabel != null && incomeLabel != null) {
            moneyLabel.setText("Money: $" + currentPlayerMoney);
            incomeLabel.setText("Income: +$" + currentPlayerIncome);
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
