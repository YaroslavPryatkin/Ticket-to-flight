package com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.MapInput;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airline;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.frontend.LowLevelHandlerFront;
import com.game.Ticket_To_Flight.frontend.MainClient;
import com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.GameUIManager;
import com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.MapStrategies.DefaultInteractionStrategy;
import com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.MapStrategies.FlightInteractionStrategy;
import com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.MapStrategies.MapInteractionStrategy;

public class MapInputController extends InputAdapter {
    private final OrthographicCamera camera;
    private final GameData gameData;
    private final Vector3 lastMousePos = new Vector3();
    private final float clickTolerance = 10f;
    private final MapInteractionStrategy defaultStrategy;
    private final MapInteractionStrategy flightStrategy;

    private MapInteractionStrategy currentStrategy;

    public MapInputController(OrthographicCamera camera, GameData gameData, GameUIManager uiManager, MainClient client, MapSelectionState selectionState) {
        this.camera = camera;
        this.gameData = gameData;
        LowLevelHandlerFront llh = client.getLlh();
        this.defaultStrategy = new DefaultInteractionStrategy(uiManager);
        this.flightStrategy = new FlightInteractionStrategy(gameData, uiManager, llh, selectionState);
        this.currentStrategy = defaultStrategy;
    }

    private float distanceToSegment(float px, float py, float x1, float y1, float x2, float y2) {
        float a = px - x1;
        float b = py - y1;
        float c = x2 - x1;
        float d = y2 - y1;

        float dot = a * c + b * d;
        float lenSq = c * c + d * d;
        float param = lenSq == 0 ? -1 : dot / lenSq;

        float xx;
        float yy;
        if (param < 0) {
            xx = x1;
            yy = y1;
        } else if (param > 1) {
            xx = x2;
            yy = y2;
        } else {
            xx = x1 + param * c;
            yy = y1 + param * d;
        }

        float dx = px - xx;
        float dy = py - yy;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    private Airport getClickedAirport(float worldX, float worldY) {
        for (Airport airport : gameData.airports) {
            if (Vector2.dst(airport.getX(), airport.getY(), worldX, worldY) <= airport.getRadius()) {
                return airport;
            }
        }
        return null;
    }

    private Airline getClickedAirline(float worldX, float worldY) {
        for (Airline airline : gameData.airlines) {
            float dist = distanceToSegment(worldX, worldY, airline.getPortA().getX(), airline.getPortA().getY(), airline.getPortB().getX(), airline.getPortB().getY());
            if (dist <= clickTolerance) return airline;
        }
        return null;
    }

    public void updateCurrentStrategy() {
        currentStrategy = gameData.currentState == GameData.State.FLIGHTS ? flightStrategy : defaultStrategy;
    }

    public void setCurrentStrategy(MapInteractionStrategy currentStrategy) {
        this.currentStrategy = currentStrategy;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector3 worldClick = new Vector3(screenX, screenY, 0);
        camera.unproject(worldClick);

        Airport clickedAirport = getClickedAirport(worldClick.x, worldClick.y);
        if (clickedAirport != null) {
            currentStrategy.onAirportClicked(clickedAirport);
            return true;
        }

        Airline clickedAirline = getClickedAirline(worldClick.x, worldClick.y);
        if (clickedAirline != null) {
            currentStrategy.onAirlineClicked(clickedAirline);
            return true;
        }

        currentStrategy.onEmptyMapClicked(worldClick.x, worldClick.y);
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
}
