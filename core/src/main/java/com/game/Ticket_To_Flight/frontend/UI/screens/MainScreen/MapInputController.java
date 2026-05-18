package com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;

public class MapInputController extends InputAdapter {
    private final OrthographicCamera camera;
    private final GameData gameData;
    private final GameUIManager uiManager;
    private final Vector3 lastMousePos = new Vector3();

    public MapInputController(OrthographicCamera camera, GameData gameData, GameUIManager uiManager) {
        this.camera = camera;
        this.gameData = gameData;
        this.uiManager = uiManager;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (uiManager.isOverlayActive()) return true;

        Vector3 worldClick = new Vector3(screenX, screenY, 0);
        camera.unproject(worldClick);

        for (Airport airport : gameData.airports) {
            if (Vector2.dst(airport.getX(), airport.getY(), worldClick.x, worldClick.y) <= airport.getRadius()) {
                uiManager.showAirportTooltip(airport);
                return true;
            }
        }

        uiManager.removeTooltip();
        lastMousePos.set(screenX, screenY, 0);
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (uiManager.isOverlayActive()) return true;
        return true;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        if (uiManager.isOverlayActive()) return true;
        camera.zoom += amountY * 0.1f;
        return true;
    }
}
