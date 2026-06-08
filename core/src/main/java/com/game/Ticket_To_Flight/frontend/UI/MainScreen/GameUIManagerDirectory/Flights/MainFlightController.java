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

import java.util.Map;

public class MainFlightController {
    public enum Step { SELECT_PLANE, CHOOSING_STARTING_AIRPORT, IN_FLIGHT}

    private final Stage stage;
    private final Skin skin;
    private final GameData gameData;
    private final LowLevelHandlerFront llh;
    private final GameUIManager uiManager;
    private final FlightHUD flightHUD;

    private Step step = Step.SELECT_PLANE;
    private Route route;
    private Table planeWindow;

    private boolean active = false;

    private PlaneType selectedPlane = null;

    public MainFlightController(
        Stage stage, Skin skin, GameData gameData, LowLevelHandlerFront llh,
        GameUIManager uiManager, FlightHUD flightHUD
    ) {
        this.route = null;
        this.stage = stage;
        this.skin = skin;
        this.gameData = gameData;
        this.llh = llh;
        this.uiManager = uiManager;

        this.flightHUD = flightHUD;
        if (this.flightHUD != null) {
            this.flightHUD.setCallbacks(this::returnToPlaneChoice, this::goBack, this::passFlight, this::finishRoute);
            this.flightHUD.setPassengerCallbacks(
                this::selectPassengerGroup,
                this::removePassengerGroup
            );
        }
    }

    public void setActive(){
        active = true;
    }

    public void update() {
        if(active) {
            flightHUD.setVisible(true);
            flightHUD.updateData(selectedPlane, gameData.currentPlayer, step, route);
            if (step == Step.SELECT_PLANE) {
                if (planeWindow == null) {
                    showPlaneSelection();
                }
                positionPlaneWindow();
            }
        }
    }

    public Route getRoute() { return route; }

    private void selectPlane(PlaneType plane) {
        selectedPlane = plane;
        step = Step.CHOOSING_STARTING_AIRPORT;
        removePlaneWindow();
    }



    public void handleAirportClick(Airport airport) {
        if (step == Step.CHOOSING_STARTING_AIRPORT) {
            if (this.selectedPlane != null) {
                this.route = new Route(this.selectedPlane, this.gameData, airport);
                step = Step.IN_FLIGHT;
            }
        }
    }

    public void handleAirlineClick(Airline airline) {
        if(step == Step.IN_FLIGHT) {
            if (route == null) {
                uiManager.showNotificationWindow("Choose the starting airport and group first.");
                return;
            }
            Map<Integer, String> errors = route.makeFlight(airline);
            if (errors != null) {
                uiManager.showNotificationWindow(errors.values().iterator().next());
                return;
            }
            uiManager.handleAirportClick(route.getCurrentAirport());
        }
    }

    public void selectPassengerGroup(PassengerType passengerType) {
        if(step == Step.IN_FLIGHT) {
            String error = route.addPassenger(passengerType);
            if (error != null) {
                uiManager.showNotificationWindow(error);
            }
        }
    }

    public void removePassengerGroup(Integer index) {
        if(step == Step.IN_FLIGHT) {
            route.removePassenger(index);
        }
    }

    private void passFlight() {
        setInactive();
        llh.sendRoutePass();
        uiManager.showNotificationWindow("Flight stage skipped.");
    }

    private void finishRoute(boolean finishStage) {
        if (route != null && !route.canFinishRoute()) {
            uiManager.showNotificationWindow("Route is not finished yet.\nDeliver all passengers or press reset and then pass!");
            return;
        }
        if (route == null) return;
        setInactive();
        llh.sendRouteResponse(route, finishStage);
        uiManager.showNotificationWindow("Flight request was sent to server.");
    }

    private void goBack() {
        if(step == Step.IN_FLIGHT) {
            if(route.isInStartingAirport()){
                returnToStartingAirportChoice();
            }
            else {
                boolean flightUndone = route.undoFlight();
                if (flightUndone) {
                    uiManager.handleAirportClick(route.getCurrentAirport());
                }
            }
        }
        else if(step == Step.CHOOSING_STARTING_AIRPORT){
            returnToPlaneChoice();
        }
    }

    private void returnToPlaneChoice(){
        route = null;
        step = Step.SELECT_PLANE;
        selectedPlane = null;
        removePlaneWindow();
    }

    private void returnToStartingAirportChoice(){
        route = null;
        step = Step.CHOOSING_STARTING_AIRPORT;
        removePlaneWindow();
    }

    public void setInactive(){
        if(active) {
            route = null;
            step = Step.SELECT_PLANE;
            selectedPlane = null;
            removePlaneWindow();
            flightHUD.setVisible(false);
            active = false;
        }
    }


    public void positionPlaneWindow() {
        if (planeWindow == null) return;
        planeWindow.pack();
        planeWindow.setPosition(
            (stage.getWidth() - planeWindow.getWidth()) / 2f,
            (stage.getHeight() - planeWindow.getHeight()) / 2f);
    }


    private void removePlaneWindow() {
        if (planeWindow != null) {
            planeWindow.remove();
            planeWindow = null;
        }
    }

    private void showPlaneSelection() {
        removePlaneWindow();
        Player player = gameData.players.get(llh.getMyId());
        planeWindow = new PlaneSelectionWindow(skin, player, this::selectPlane);
        stage.addActor(planeWindow);
        positionPlaneWindow();
    }


    public boolean isFirstAirport(Airport port){
        if(route == null) return false;
        return route.getStartingPort().equals(port);
    }

    public boolean isCurrentAirport(Airport port){
        if(route == null) return false;
        return route.getCurrentAirport().equals(port);
    }
    public boolean isAirlineInRoute(Airline line){
        if(route == null) return false;
        return route.getLines().contains(line);
    }
}
