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
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.MapInput.MapSelectionState;

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
        public final Airport airport;
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
    private final FlightHUD flightHUD;
    private final MapSelectionState selectionState;

    private final Deque<Runnable> undoStack = new ArrayDeque<>();
    private final List<ChosenGroup> chosenGroups = new ArrayList<>();

    private Step step = Step.SELECT_PLANE;
    private Route route;
    private PlaneType selectedPlane;
    private Airport activeFlightAirport;
    private Airport firstFlightAirport;
    private Table planeWindow;
    private boolean wasActive = false;

    public MainFlightController(
        Stage stage,
        Skin skin,
        GameData gameData,
        LowLevelHandlerFront llh,
        GameUIManager uiManager,
        FlightHUD flightHUD,
        MapSelectionState selectionState
    ) {
        this.stage = stage;
        this.skin = skin;
        this.gameData = gameData;
        this.llh = llh;
        this.uiManager = uiManager;
        this.selectionState = selectionState;

        this.flightHUD = flightHUD;
        this.flightHUD.setCallbacks(this::resetAll, this::goBack, this::finishRoute);
        this.flightHUD.setPassengerCallbacks(passengerType -> {
            Airport airport = route != null ? route.getCurrentAirport() : activeFlightAirport;
            if (airport != null) {
                selectPassengerGroup(airport, passengerType);
            }
        });
    }

    public void update() {
        if (!isActive()) {
            flightHUD.setVisible(false);
            clearUi();
            resetState();
            wasActive = false;
            Player player = gameData.players.get(gameData.currentPlayer);
            /*if (player != null && player.actionPoints == 0) {
                uiManager.showSuccessWindow("You have not got enough AP");
            }*/
            return;
        }

        if (!wasActive) {
            resetState();
            showPlaneSelection();
            wasActive = true;
        }

        flightHUD.setVisible(true);
        flightHUD.updateData(step, selectedPlane, route, chosenGroups);
        position();
    }

    public void position() {
        positionPlaneWindow();
    }

    private void clearUi() {
        removePlaneWindow();
        flightHUD.setVisible(false);
    }

    public Route getRoute() {
        return route;
    }

    private void selectPlane(PlaneType plane) {
        selectedPlane = plane;
        step = Step.CHOOSE_AIRPORT_GROUP;
        removePlaneWindow();

        undoStack.push(() -> {
            selectedPlane = null;
            route = null;
            selectionState.setCurrentRoute(null);
            chosenGroups.clear();
            setActiveFlightAirport(null);
            setFirstFlightAirport(null);
        });

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

        if (chosenGroups.isEmpty() && step == Step.CHOOSE_AIRPORT_GROUP) {
            setActiveFlightAirport(airport);
            setFirstFlightAirport(airport);

            if (this.selectedPlane != null) {
                this.route = new Route(this.selectedPlane, this.gameData, airport);
                selectionState.setCurrentRoute(this.route);
            }
        }

        uiManager.showAirportTooltip(airport);
    }

    public void handleAirlineClick(Airline airline) {
        if (!isActive()) return;

        if (route == null) {
            uiManager.showSuccessWindow("Choose the starting airport and group first.");
            return;
        }

        if (activeFlightAirport == null || airline.getAnotherEnd(activeFlightAirport) == null) {
            uiManager.showSuccessWindow("Choose an airline connected to the current airport.");
            return;
        }

        Map<Integer, String> errors = route.makeFlight(airline);
        if (errors != null) {
            uiManager.showSuccessWindow(errors.values().iterator().next());
            return;
        }

        undoStack.push(() -> {
            route.undoFlight();
            setActiveFlightAirport(route.getCurrentAirport());

            if (chosenGroups.size() > route.getLines().size()) {
                step = Step.CHOOSE_AIRLINE;
            } else {
                step = Step.CHOOSE_AIRPORT_GROUP;
            }

            uiManager.showAirportTooltip(route.getCurrentAirport());
        });

        setActiveFlightAirport(route.getCurrentAirport());
        step = Step.CHOOSE_AIRPORT_GROUP;

        uiManager.showAirportTooltip(route.getCurrentAirport());
    }

    public void handleEmptyMapClick() {
        if (!isActive()) return;

        if (route != null && !route.getLines().isEmpty() && activeFlightAirport != null) {
            uiManager.showAirportTooltip(activeFlightAirport);
        } else {
            uiManager.removeTooltip();
        }
    }

    public boolean canSelectPassengerGroups(Airport airport) {
        return isActive() &&
            step == Step.CHOOSE_AIRPORT_GROUP &&
            activeFlightAirport != null &&
            airport != null &&
            airport.equals(activeFlightAirport);
    }

    private boolean isActive() {
        return gameData.currentState == GameData.State.FLIGHTS && gameData.currentPlayer == llh.getMyId()
            && gameData.players.get(gameData.currentPlayer).actionPoints > 0;
    }

    private void selectPassengerGroup(Airport airport, PassengerType passengerType) {
        if (!canSelectPassengerGroups(airport)) {
            uiManager.showSuccessWindow("You can choose groups only in current airport.");
            return;
        }

        if (route == null) {
            route = new Route(selectedPlane, gameData, airport);
            selectionState.setCurrentRoute(route);
        }

        String error = route.addPassenger(passengerType);
        if (error != null) {
            uiManager.showSuccessWindow(error);
            return;
        }

        chosenGroups.add(new ChosenGroup(airport, passengerType));

        undoStack.push(() -> {
            if (route != null && !route.getPassengers().isEmpty()) {
                route.removePassenger(route.getPassengers().size() - 1);
            }
            if (!chosenGroups.isEmpty()) {
                chosenGroups.remove(chosenGroups.size() - 1);
            }

            if (chosenGroups.isEmpty()) {
                route = null;
                setActiveFlightAirport(null);
                setFirstFlightAirport(null);
            }
        });

        step = Step.CHOOSE_AIRLINE;
        selectionState.clearFlightSelection();
        uiManager.removeTooltip();
        uiManager.showSuccessWindow("Group selected. Choose the airline.");
    }

    private void finishRoute() {
        if (route != null && !route.canFinishRoute()) {
            uiManager.showSuccessWindow("Route is not finished yet. Deliver all passengers or reset!");
            return;
        }
        llh.sendRoutePass();
        uiManager.removeTooltip();

        if (route == null) {
            uiManager.showSuccessWindow("Flight stage skipped.");
        } else {
            uiManager.showSuccessWindow("Flight request was sent to server.");
        }

        clearUi();
        resetState();
    }

    private void goBack() {
        if (undoStack.isEmpty()) return;

        undoStack.pop().run();

        uiManager.removeTooltip();

        if (selectedPlane == null) {
            step = Step.SELECT_PLANE;
            showPlaneSelection();
        } else {
            int linesCount = (route != null) ? route.getLines().size() : 0;

            if (chosenGroups.size() > linesCount) {
                step = Step.CHOOSE_AIRLINE;
            } else {
                step = Step.CHOOSE_AIRPORT_GROUP;
            }
        }
    }

    private void resetAll() {
        clearUi();
        uiManager.removeTooltip();
        selectionState.clearFlightSelection();
        resetState();
        showPlaneSelection();
    }

    private void resetState() {
        undoStack.clear();
        chosenGroups.clear();

        route = null;
        selectionState.setCurrentRoute(null);
        selectedPlane = null;

        setActiveFlightAirport(null);
        setFirstFlightAirport(null);

        step = Step.SELECT_PLANE;
    }

    private void setActiveFlightAirport(Airport airport) {
        activeFlightAirport = airport;
        selectionState.setActiveFlightAirport(airport);
    }

    private void setFirstFlightAirport(Airport airport) {
        firstFlightAirport = airport;
        selectionState.setFirstFlightAirport(airport);
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
