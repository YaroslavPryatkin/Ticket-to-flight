package com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.Flights;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airline;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Player;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PassengerType;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PlaneType;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.commonFrontAndBack.Route;
import com.game.Ticket_To_Flight.frontend.LowLevelHandlerFront;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManager;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.Flights.FlightDirectory.PlaneSelectionWindow;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.HUD.HUDDirectory.FlightHUD;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;

public class MainFlightController {
    public enum Step {
        SELECT_PLANE,
        CHOOSE_AIRPORT_GROUP,
        CHOOSE_AIRLINE
    }

    public static class ChosenGroup {
        public final Airport airport; // Сделали public для FlightHUD
        public final PassengerType passengerType;

        public ChosenGroup(Airport airport, PassengerType passengerType) {
            this.airport = airport;
            this.passengerType = passengerType;
        }
    }

    private final Stage stage;
    private final Skin skin;
    private final GameData gameData;
    private final LowLevelHandlerFront llh;
    private final GameUIManager uiManager;
    private final FlightHUD flightHUD; // Наш новый HUD

    private final Deque<Runnable> undoStack = new ArrayDeque<>();
    private final List<ChosenGroup> chosenGroups = new ArrayList<>();

    private Step step = Step.SELECT_PLANE;
    private Route route;
    private PlaneType selectedPlane;
    private Table planeWindow;
    private boolean wasActive = false;

    // В конструктор добавили параметр FlightHUD
    public MainFlightController(Stage stage, Skin skin, GameData gameData, LowLevelHandlerFront llh, GameUIManager uiManager, FlightHUD flightHUD) {
        this.stage = stage;
        this.skin = skin;
        this.gameData = gameData;
        this.llh = llh;
        this.uiManager = uiManager;

        this.flightHUD = flightHUD;
        // Подключаем кнопки HUD'а к методам контроллера через лямбды
        this.flightHUD.setCallbacks(this::resetAll, this::goBack, this::finishRoute);
    }

    public void update() {
        if (!isActive()) {
            flightHUD.setVisible(false); // Прячем панель полетов
            clearUi();
            resetState();
            wasActive = false;
            return;
        }

        if (!wasActive) {
            resetState();
            showPlaneSelection();
            wasActive = true;
        }

        flightHUD.setVisible(true); // Показываем панель полетов
        // Передаем данные в HUD, чтобы он сам обновил тексты
        flightHUD.updateData(step, selectedPlane, route, chosenGroups);
        position();
    }

    public void position() {
        positionPlaneWindow();
        // Нам больше не нужно позиционировать HUD, он делает это сам через setFillParent(true)
    }

    private void clearUi() {
        removePlaneWindow();
        flightHUD.setVisible(false); // Просто скрываем HUD
    }

    // =========================================================
    // ВСЯ ОСТАЛЬНАЯ ЛОГИКА ИГРЫ ОСТАЕТСЯ БЕЗ ИЗМЕНЕНИЙ
    // =========================================================

    private void selectPlane(PlaneType plane) {
        selectedPlane = plane;
        step = Step.CHOOSE_AIRPORT_GROUP;
        removePlaneWindow();
        undoStack.push(this::resetState);
        uiManager.showSuccessWindow("Plane was selected successfully. Choose the first airport and the group.");
    }

    private void showPlaneSelection() {
        removePlaneWindow();
        Player player = gameData.players.get(llh.getMyId());
        planeWindow = new PlaneSelectionWindow(skin, player, this::selectPlane);
        stage.addActor(planeWindow);
        positionPlaneWindow();
    }

    public void handleAirportClick(Airport airport) {
        if (!isActive()) return;
        if (step != Step.CHOOSE_AIRPORT_GROUP) {
            uiManager.showSuccessWindow("Choose an airline first.");
            return;
        }
        if (route != null && !airport.equals(route.getCurrentAirport())) {
            uiManager.showSuccessWindow("Choose the current route airport: " + route.getCurrentAirport().getCityName());
            return;
        }
        uiManager.showAirportTooltipForFlight(airport, this::selectPassengerGroup);
    }

    public void handleAirlineClick(Airline airline) {
        if (!isActive()) return;
        if (step != Step.CHOOSE_AIRLINE || route == null) {
            uiManager.showSuccessWindow("Choose the airport and group first.");
            return;
        }
        if (airline.getPlayer() == null) {
            uiManager.showSuccessWindow("Choose an owned airline.");
            return;
        }

        Map<Integer, String> errors = route.makeFlight(airline);
        if (errors != null) {
            uiManager.showSuccessWindow(errors.values().iterator().next());
            return;
        }

        undoStack.push(() -> route.undoFlight());
        step = Step.CHOOSE_AIRPORT_GROUP;
        uiManager.removeTooltip();
        uiManager.showSuccessWindow("Airline selected. Choose the next airport and group.");
    }

    private boolean isActive() {
        return gameData.currentState == GameData.State.FLIGHTS && gameData.currentPlayer == llh.getMyId();
    }

    private void selectPassengerGroup(Airport airport, PassengerType passengerType) {
        if (selectedPlane == null) {
            uiManager.showSuccessWindow("Choose the plane first.");
            return;
        }

        final boolean createdRoute = route == null;
        if (createdRoute) {
            route = new Route(selectedPlane, gameData, airport);
        }

        String error = route.addPassenger(passengerType);
        if (error != null) {
            uiManager.showSuccessWindow(error);
            return;
        }

        chosenGroups.add(new ChosenGroup(airport, passengerType));
        undoStack.push(() -> {
            if (route != null) {
                route.removePassenger(route.getPassengers().size() - 1);
            }
            if (createdRoute) {
                route = null;
            }
            if (!chosenGroups.isEmpty()) {
                chosenGroups.remove(chosenGroups.size() - 1);
            }
        });
        step = Step.CHOOSE_AIRLINE;
        uiManager.removeTooltip();
        uiManager.showSuccessWindow("Group selected. Choose the airline.");
    }

    private void finishRoute() {
        if (route == null || !route.canFinishRoute()) {
            uiManager.showSuccessWindow("Route is not finished yet.");
            return;
        }
        llh.sendRouteResponse(route, true);
        uiManager.removeTooltip();
        uiManager.showSuccessWindow("Flight request was sent to server.");
        clearUi();
        resetState();
    }

    private void goBack() {
        if (undoStack.isEmpty()) return;
        undoStack.pop().run();
        if (route == null || selectedPlane == null) {
            step = Step.SELECT_PLANE;
            showPlaneSelection();
        } else if (route.getLines().isEmpty() && chosenGroups.isEmpty()) {
            step = Step.CHOOSE_AIRPORT_GROUP;
        } else if (chosenGroups.size() > route.getLines().size()) {
            step = Step.CHOOSE_AIRLINE;
        } else {
            step = Step.CHOOSE_AIRPORT_GROUP;
        }
        uiManager.removeTooltip();
    }

    private void resetAll() {
        clearUi();
        resetState();
        showPlaneSelection();
    }

    private void resetState() {
        undoStack.clear();
        chosenGroups.clear();
        route = null;
        selectedPlane = null;
        step = Step.SELECT_PLANE;
    }

    private void positionPlaneWindow() {
        if (planeWindow == null) return;
        planeWindow.pack();
        planeWindow.setPosition(
            (stage.getWidth() - planeWindow.getWidth()) / 2f,
            (stage.getHeight() - planeWindow.getHeight()) / 2f
        );
    }

    private void removePlaneWindow() {
        if (planeWindow != null) {
            planeWindow.remove();
            planeWindow = null;
        }
    }
}
