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

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MainFlightController {
    public enum Step { SELECT_PLANE, CHOOSING_STARTING_AIRPORT, IN_FLIGHT}

    private final Stage stage;
    private final Skin skin;
    private final GameData gameData;
    private final LowLevelHandlerFront llh;
    private final GameUIManager uiManager;
    private final FlightHUD flightHUD;
    private final MapSelectionState selectionState;

    private Step step = Step.SELECT_PLANE;
    private Route route;
    private PlaneType selectedPlane;
    private Airport activeFlightAirport;
    private Airport firstFlightAirport;
    private Table planeWindow;

    public MainFlightController(
        Stage stage, Skin skin, GameData gameData, LowLevelHandlerFront llh,
        GameUIManager uiManager, FlightHUD flightHUD, MapSelectionState selectionState
    ) {
        this.route = null;
        this.stage = stage;
        this.skin = skin;
        this.gameData = gameData;
        this.llh = llh;
        this.uiManager = uiManager;
        this.selectionState = selectionState;

        this.flightHUD = flightHUD;
        if (this.flightHUD != null) {
            this.flightHUD.setCallbacks(this::resetAll, this::goBack, this::passFlight, this::finishRoute);
            this.flightHUD.setPassengerCallbacks(
                (passenger) -> selectPassengerGroup(activeFlightAirport, passenger),
                this::removePassengerGroup
            );
        }
    }

    public void update() {
        if (!isActive()) {
            clearUi();
            resetState();
            return;
        }

        if (selectedPlane == null && planeWindow == null) {
            resetState();
            showPlaneSelection();
        }

        flightHUD.setVisible(true);
        flightHUD.updateData(step, selectedPlane, route);

        position();
    }

    private List<Route.BoarderPassenger> getRoutePassengers() {
        if (route == null) {
            return Collections.emptyList();
        }
        return route.getPassengers();
    }

    public void position() { positionPlaneWindow(); }
    private void clearUi() { removePlaneWindow(); flightHUD.setVisible(false); }
    public Route getRoute() { return route; }

    private void selectPlane(PlaneType plane) {
        selectedPlane = plane; step = Step.CHOOSING_STARTING_AIRPORT; removePlaneWindow();
    }

    private void showPlaneSelection() {
        removePlaneWindow();
        Player player = gameData.players.get(llh.getMyId());
        planeWindow = new PlaneSelectionWindow(skin, player, this::selectPlane);
        stage.addActor(planeWindow); positionPlaneWindow();
    }

    public void handleAirportClick(Airport airport) {
        if (!isActive()) return;

        boolean noPassengers = route == null || route.getPassengers().isEmpty();
        boolean noLines = route == null || route.getLines().isEmpty();
        if (noPassengers && noLines && step == Step.CHOOSING_STARTING_AIRPORT) {
            setActiveFlightAirport(airport); setFirstFlightAirport(airport);
            if (this.selectedPlane != null) {
                this.route = new Route(this.selectedPlane, this.gameData, airport);
                step = Step.IN_FLIGHT;
                selectionState.setCurrentRoute(this.route);
            }
        }
        uiManager.showAirportTooltip(airport);
    }

    public void handleAirlineClick(Airline airline) {
        if (!isActive()) return;
        if (route == null) { uiManager.showSuccessWindow("Choose the starting airport and group first."); return; }
        if (activeFlightAirport == null || airline.getAnotherEnd(activeFlightAirport) == null) {
            uiManager.showSuccessWindow("Choose an airline connected to the current airport."); return;
        }
        Map<Integer, String> errors = route.makeFlight(airline);
        if (errors != null) { uiManager.showSuccessWindow(errors.values().iterator().next()); return; }
        setActiveFlightAirport(route.getCurrentAirport());
        flightHUD.updateData(step, selectedPlane, route);
        uiManager.showAirportTooltip(route.getCurrentAirport());
    }

    public void handleEmptyMapClick() {
        if (!isActive()) return;
        if (route != null && !route.getLines().isEmpty() && activeFlightAirport != null) uiManager.showAirportTooltip(activeFlightAirport);
        else uiManager.removeTooltip();
    }


    private boolean isActive() {
        return gameData.currentState == GameData.State.FLIGHTS && gameData.currentPlayer == llh.getMyId() && gameData.players.get(gameData.currentPlayer).actionPoints > 0;
    }

    public void selectPassengerGroup(Airport airport, PassengerType passengerType) {
        if(!isActive())return;

        if (route == null) {
            route = new Route(selectedPlane, gameData, airport);
            step = Step.IN_FLIGHT;
            selectionState.setCurrentRoute(route);
        }

        String error = route.addPassenger(passengerType);
        if (error != null) {
            uiManager.showSuccessWindow(error);
            return;
        }

        flightHUD.updateData(step, selectedPlane, route);
    }

    public void removePassengerGroup(PassengerType passengerType) {
        if (route == null || passengerType == null) return;

        List<Route.BoarderPassenger> passengers = route.getPassengers();
        for (int i = passengers.size() - 1; i >= 0; i--) {
            Route.BoarderPassenger passenger = passengers.get(i);
            if (passenger.getType() == passengerType && passenger.canBeRemoved()) {
                if (route.removePassenger(i)) {
                    flightHUD.updateData(step, selectedPlane, route);
                }
                break;
            }
        }
    }

    private void passFlight() {
        llh.sendRoutePass();
        resetAll();
        uiManager.removeTooltip();
        uiManager.showSuccessWindow("Flight stage skipped.");
        clearUi();
        resetState();
    }

    private void finishRoute(boolean finishStage) {
        if (route != null && !route.canFinishRoute()) {
            uiManager.showSuccessWindow("Route is not finished yet. Deliver all passengers or reset!");
            return;
        }
        if (route == null) return;

        llh.sendRouteResponse(route, finishStage);
        resetAll();
        uiManager.removeTooltip();
        uiManager.showSuccessWindow("Flight request was sent to server.");
        clearUi();
        resetState();
    }

    private void goBack() {
        if (route != null) {
            boolean flightUndone = route.undoFlight();
            if (flightUndone) {
                setActiveFlightAirport(route.getCurrentAirport());
                if (route.getCurrentAirport() == firstFlightAirport) {
                    step = Step.CHOOSING_STARTING_AIRPORT;
                }
            } else resetAll();
        } else resetAll();
        uiManager.removeTooltip();
    }

    private void resetAll() {
        route = null;
        step = Step.SELECT_PLANE;
        clearUi(); uiManager.removeTooltip(); selectionState.clearRouteHighlights(); resetState(); showPlaneSelection();
    }

    private void resetState() {
        route = null; selectionState.setCurrentRoute(null); selectedPlane = null;
        setActiveFlightAirport(null); setFirstFlightAirport(null); step = Step.SELECT_PLANE;
    }

    private void setActiveFlightAirport(Airport airport) { activeFlightAirport = airport; selectionState.setActiveFlightAirport(airport); }
    private void setFirstFlightAirport(Airport airport) { firstFlightAirport = airport; selectionState.setFirstFlightAirport(airport); }
    private void positionPlaneWindow() { if (planeWindow == null) return; planeWindow.pack(); planeWindow.setPosition((stage.getWidth() - planeWindow.getWidth()) / 2f, (stage.getHeight() - planeWindow.getHeight()) / 2f); }
    private void removePlaneWindow() { if (planeWindow != null) { planeWindow.remove(); planeWindow = null; } }
}
