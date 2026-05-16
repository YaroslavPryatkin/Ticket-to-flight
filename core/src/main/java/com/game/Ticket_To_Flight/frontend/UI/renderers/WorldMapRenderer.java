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
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.game.Ticket_To_Flight.PresetPaths;
import com.game.Ticket_To_Flight.Utilities.MapHolder;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airline;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PassengerType;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.frontend.MainClient;
import com.game.Ticket_To_Flight.packages.PackageCreateWorldMap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WorldMapRenderer extends ScreenAdapter {
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

    private Skin skin_default_window;
    private Skin skin_invest_window;
    private Window currentTooltip;

    private Airport selectedAirport;
    private Airline selectedAirline;

    private boolean isBuyingPhase = true;
    private double currentPlayerMoney = 1000.0;
    private double currentPlayerIncome = 50.0;

    private int currentRound = 1;
    private int currentStage = 1;
    private int timeRemaining = 120;

    private Label roundLabel;
    private Label stageLabel;
    private Label timeLabel;

    private Label moneyLabel;
    private Label incomeLabel;

    private boolean isOverlayActive = false;

    private MainClient client;
    private final GameData gameData;

    public WorldMapRenderer(MainClient client) {
        this.client = client;
        this.gameData = client.getGameData();
        this.batch = new SpriteBatch();

        this.WORLD_WIDTH = Gdx.graphics.getWidth();
        this.WORLD_HEIGHT = Gdx.graphics.getHeight();
        this.mapTexture = new Texture(Gdx.files.internal(PresetPaths.presetPaths.get(1)));

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

        createBasicWindow();
        createInvestWindow();
        setupInput();
        //showInvestWindow();
        showPlaneWindow();
        createHUD();
    }

    private void createBasicWindow() {
        skin_default_window = new Skin();
        skin_default_window.add("default-font", new BitmapFont());

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.2f, 0.2f, 0.2f, 0.8f));
        pixmap.fill();
        skin_default_window.add("background", new Texture(pixmap));

        Pixmap btnPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        btnPixmap.setColor(new Color(0.4f, 0.4f, 0.4f, 1f));
        btnPixmap.fill();
        skin_default_window.add("btn-up", new Texture(btnPixmap));

        Pixmap btnDisabledPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        btnDisabledPixmap.setColor(new Color(0.1f, 0.1f, 0.1f, 0.9f));
        btnDisabledPixmap.fill();
        skin_default_window.add("btn-disabled", new Texture(btnDisabledPixmap));

        pixmap.dispose();
        btnPixmap.dispose();
        btnDisabledPixmap.dispose();

        Window.WindowStyle windowStyle = new Window.WindowStyle();
        windowStyle.titleFont = skin_default_window.getFont("default-font");
        windowStyle.background = skin_default_window.getDrawable("background");
        skin_default_window.add("default", windowStyle);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = skin_default_window.getFont("default-font");
        labelStyle.fontColor = Color.WHITE;
        skin_default_window.add("default", labelStyle);

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = skin_default_window.getFont("default-font");
        btnStyle.fontColor = Color.WHITE;
        btnStyle.up = skin_default_window.getDrawable("btn-up");
        btnStyle.disabled = skin_default_window.getDrawable("btn-disabled");
        skin_default_window.add("default", btnStyle);
    }

    private void createInvestWindow() {
        skin_invest_window = new Skin();
        skin_invest_window.add("default-font", new BitmapFont());

        Pixmap bluePix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        bluePix.setColor(new Color(0.1f, 0.2f, 0.5f, 0.85f));
        bluePix.fill();
        skin_invest_window.add("blue-bg", new Texture(bluePix));
        bluePix.dispose();

        Pixmap sliderKnob = new Pixmap(20, 20, Pixmap.Format.RGBA8888);
        sliderKnob.setColor(Color.CYAN);
        sliderKnob.fillCircle(10, 10, 10);
        skin_invest_window.add("slider-knob", new Texture(sliderKnob));
        sliderKnob.dispose();

        Pixmap darkPix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        darkPix.setColor(new Color(0.1f, 0.1f, 0.1f, 0.9f));
        darkPix.fill();
        skin_invest_window.add("dark-bg", new Texture(darkPix));
        darkPix.dispose();

        Pixmap sliderTrackPix = new Pixmap(100, 10, Pixmap.Format.RGBA8888);
        sliderTrackPix.setColor(new Color(0.2f, 0.2f, 0.2f, 1f));
        sliderTrackPix.fill();
        skin_invest_window.add("slider-track", new Texture(sliderTrackPix));
        sliderTrackPix.dispose();

        Slider.SliderStyle sliderStyle = new Slider.SliderStyle();
        sliderStyle.background = skin_invest_window.getDrawable("slider-track");
        sliderStyle.knob = skin_invest_window.getDrawable("slider-knob");
        skin_invest_window.add("default-horizontal", sliderStyle);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = skin_invest_window.getFont("default-font");
        labelStyle.fontColor = Color.WHITE;
        skin_invest_window.add("default", labelStyle);

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = skin_invest_window.getFont("default-font");
        btnStyle.fontColor = Color.WHITE;
        btnStyle.up = skin_invest_window.getDrawable("dark-bg"); // Будет темная кнопка
        skin_invest_window.add("default", btnStyle);
    }

    private void createHUD() {
        Table topBar = new Table();
        topBar.setFillParent(true);
        topBar.top();
        topBar.pad(15);

        Table leftStats = new Table();
        leftStats.add(showRound()).padRight(30);
        leftStats.add(showStage()).padRight(30);
        leftStats.add(showTime());

        Table rightStats = showMoneyAndIncome();

        topBar.add(leftStats).expandX().left();
        topBar.add(rightStats).expandX().right();

        uiStage.addActor(topBar);
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

        InputAdapter mapInputProcessor = new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (isOverlayActive) return true;

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
                if (isOverlayActive) return true;
                float deltaX = lastMousePos.x - screenX;
                float deltaY = screenY - lastMousePos.y;

                camera.translate(deltaX * camera.zoom, deltaY * camera.zoom);
                lastMousePos.set(screenX, screenY, 0);
                return true;
            }

            @Override
            public boolean scrolled(float amountX, float amountY) {
                if (isOverlayActive) return true;
                camera.zoom += amountY * 0.1f;
                return true;
            }
        };

        multiplexer.addProcessor(mapInputProcessor);
        Gdx.input.setInputProcessor(multiplexer);
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

    private Table showMoneyAndIncome() {
        Table table = new Table();
        moneyLabel = new Label("Money: $" + currentPlayerMoney, skin_default_window);
        incomeLabel = new Label("Income: +$" + currentPlayerIncome, skin_default_window);
        incomeLabel.setColor(Color.GREEN);

        table.add(moneyLabel).left().row();
        table.add(incomeLabel).left().padTop(5).row();
        return table;
    }

    private Label showRound() {
        roundLabel = new Label("Round: " + currentRound, skin_default_window);
        return roundLabel;
    }

    private Label showStage() {
        stageLabel = new Label("Stage: " + currentStage, skin_default_window);
        return stageLabel;
    }

    private Label showTime() {
        timeLabel = new Label("Time: " + timeRemaining + "s", skin_default_window);
        timeLabel.setColor(Color.ORANGE); // Сделаем время оранжевым для красоты
        return timeLabel;
    }

    private void showAirportTooltip(Airport airport) {
        if (currentTooltip != null) currentTooltip.remove();

        selectedAirport = airport;

        currentTooltip = new Window(airport.getCityName(), skin_default_window);
        currentTooltip.pad(20);

        Table table = new Table();
        table.add(new Label("Route", skin_default_window)).padRight(10);
        table.add(new Label("Group", skin_default_window));
        table.row();

        var guestsMap = airport.getGuests();

        if (guestsMap != null) {
            Iterator<PassengerType> it = MapHolder.viewAsListIterator(guestsMap);
            PassengerType type;
            while ((type = it.next()) != null) {
                Integer groupCount = guestsMap.get(type);
                if (groupCount == null || groupCount == 0) continue;

                String passengerInfo = type.description;
                String countText = groupCount + " гр. (по " + type.size + " чел.)";

                table.add(new Label(passengerInfo, skin_default_window)).padRight(10).left();
                table.add(new Label(countText, skin_default_window)).right();
                table.row();
            }
        }
        else {
            table.add(new Label("No guests", skin_default_window)).colspan(2);
        }

        currentTooltip.add(table);
        currentTooltip.pack();
        uiStage.addActor(currentTooltip);
    }

    private void showAirlineTooltip(Airline airline, float clickX, float clickY) {
        if (currentTooltip != null) currentTooltip.remove();

        selectedAirport = null;
        selectedAirline = airline;

        currentTooltip = new Window("Route Details", skin_default_window);
        currentTooltip.pad(20);
        Table table = new Table();

        if (airline.getPlayer() != null) {
            table.add(new Label("Owned by: " + airline.getPlayer().getName(), skin_default_window));
        } else {
            TextButton buyButton = new TextButton("Buy for $" + airline.getPrice(), skin_default_window);

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

    public void showInvestWindow() {
        isOverlayActive = true;

        final Table overlayWindow = new Table();
        overlayWindow.setFillParent(true);
        overlayWindow.setBackground(skin_invest_window.getDrawable("blue-bg"));
        overlayWindow.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.enabled);

        Label titleLabel = new Label("Investing", skin_invest_window);
        titleLabel.setFontScale(1.5f);

        Label subtitleLabel = new Label("invest your incomes to money", skin_invest_window);

        final Slider slider = new Slider(1, 20, 1, false, skin_invest_window);
        final Label amountLabel = new Label("1", skin_invest_window);

        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                amountLabel.setText(String.valueOf((int) slider.getValue()));
            }
        });

        TextButton submitBtn = new TextButton("Submit", skin_invest_window);
        submitBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int investedAmount = (int) slider.getValue();
                overlayWindow.remove();
                showSuccessWindow("Income was invested successfully!");
            }
        });

        overlayWindow.add(titleLabel).padBottom(15).row();
        overlayWindow.add(subtitleLabel).padBottom(40).row();
        overlayWindow.add(slider).width(300).padBottom(10).row();
        overlayWindow.add(amountLabel).padBottom(40).row();
        overlayWindow.add(submitBtn).width(150).height(50);

        uiStage.addActor(overlayWindow);
    }

    public void showSuccessWindow(String message) {
        isOverlayActive = true;

        final Window successWindow = new Window(" Success", skin_default_window);
        successWindow.pad(30);
        successWindow.padTop(50);
        successWindow.setModal(true);
        successWindow.setMovable(false);

        Label messageLabel = new Label(message, skin_default_window);

        TextButton closeBtn = new TextButton("Close", skin_default_window);
        closeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                successWindow.remove();
                isOverlayActive = false;
            }
        });

        successWindow.add(messageLabel).padBottom(30).row();
        successWindow.add(closeBtn).width(120).height(40);

        successWindow.pack();
        float centerX = (uiStage.getWidth() - successWindow.getWidth()) / 2f;
        float centerY = (uiStage.getHeight() - successWindow.getHeight()) / 2f;
        successWindow.setPosition(centerX, centerY);

        uiStage.addActor(successWindow);
    }

    public void showPlaneWindow() {
        isOverlayActive = true;

        final Table overlayWindow = new Table();
        overlayWindow.setFillParent(true);
        overlayWindow.setBackground(skin_invest_window.getDrawable("blue-bg"));
        overlayWindow.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.enabled);

        Label titleLabel = new Label("Purchase planes", skin_invest_window);
        titleLabel.setFontScale(1.5f);
        Label subtitleLabel = new Label("Buy new planes", skin_invest_window);

        Table planesTable = new Table();

        planesTable.add(new Label("Regional Jet", skin_invest_window)).pad(20);
        planesTable.add(new Label("Business Plane", skin_invest_window)).pad(20);
        planesTable.add(new Label("Usual Jet", skin_invest_window)).pad(20);
        planesTable.row();

        planesTable.add(new Label("$500", skin_invest_window)).padBottom(20);
        planesTable.add(new Label("$1200", skin_invest_window)).padBottom(20);
        planesTable.add(new Label("$2500", skin_invest_window)).padBottom(20);
        planesTable.row();

        TextButton buyRegBtn = new TextButton("BUY", skin_invest_window);
        TextButton buyBusBtn = new TextButton("BUY", skin_invest_window);
        TextButton buyUsuBtn = new TextButton("BUY", skin_invest_window);

        buyRegBtn.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                System.out.println("Куплен Regional Jet");
                overlayWindow.remove();
                showSuccessWindow("Regional Jet purchased successfully!");
            }
        });

        buyBusBtn.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                System.out.println("Куплен Business Plane");
                overlayWindow.remove();
                showSuccessWindow("Business Plane purchased successfully!");
            }
        });

        buyUsuBtn.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                System.out.println("Куплен Usual Jet");
                overlayWindow.remove();
                showSuccessWindow("Usual Jet purchased successfully!");
            }
        });

        planesTable.add(buyRegBtn).width(140).height(45);
        planesTable.add(buyBusBtn).width(140).height(45);
        planesTable.add(buyUsuBtn).width(140).height(45);

        TextButton closeBtn = new TextButton("Cancel", skin_invest_window);
        closeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                overlayWindow.remove();
            }
        });

        overlayWindow.add(titleLabel).padBottom(10).row();
        overlayWindow.add(subtitleLabel).padBottom(50).row();
        overlayWindow.add(planesTable).padBottom(50).row(); // Вставляем всю таблицу с самолетами целиком
        overlayWindow.add(closeBtn).width(150).height(50);

        uiStage.addActor(overlayWindow);
    }

    @Override
    public void render(float delta) {
        client.mainCycleWithUpdate();
        renderNoLogic(delta);
        gameData.releaseReadLock();
    }

    private void renderNoLogic(float delta){
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

        if (moneyLabel != null) {
            moneyLabel.setText("Money: $" + currentPlayerMoney);
        }
        if (incomeLabel != null) {
            incomeLabel.setText("Income: +$" + currentPlayerIncome);
        }
        if (roundLabel != null) {
            roundLabel.setText("Round: " + currentRound);
        }
        if (stageLabel != null) {
            stageLabel.setText("Stage: " + currentStage);
        }
        if (timeLabel != null) {
            timeLabel.setText("Time: " + timeRemaining + "s");
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
