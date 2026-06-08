package com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.Flights;

import com.badlogic.gdx.graphics.OrthographicCamera;
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
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.Tooltips.ContextMenuWithButtons;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.MapInput.ContextMenuPositionManager;

import java.util.Map;

public class MainFlightController {
    public enum Step { SELECT_PLANE, CHOOSING_STARTING_AIRPORT, IN_FLIGHT}

    private final Stage uiStageHUD;
    private final Skin skin;
    private final GameData gameData;
    private final LowLevelHandlerFront llh;
    private final GameUIManager gameUIManager;
    private final OrthographicCamera camera;
    private final FlightHUD flightHUD;

    private Step step = Step.SELECT_PLANE;
    private Route route;
    private Table planeWindow;

    private boolean active = false;

    private PlaneType selectedPlane = null;
    private ContextMenuWithButtons currentTooltip=null;

    public MainFlightController(
        Stage uiStageHUD, Skin skin, GameData gameData, LowLevelHandlerFront llh,
        GameUIManager gameUIManager, FlightHUD flightHUD, OrthographicCamera camera
    ) {
        this.route = null;
        this.uiStageHUD = uiStageHUD;
        this.skin = skin;
        this.gameData = gameData;
        this.llh = llh;
        this.gameUIManager = gameUIManager;
        this.camera = camera;

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

    public void updateHud() {
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
        flightHUD.updateHudOnNextFrame();
        removePlaneWindow();
    }


    private void tryChoosingAirport(Airport airport){
        removeTooltip();
        if (step == Step.CHOOSING_STARTING_AIRPORT) {
            if (this.selectedPlane != null) {
                this.route = new Route(this.selectedPlane, this.gameData, airport);
                step = Step.IN_FLIGHT;
                flightHUD.updateHudOnNextFrame();
            }
        }
    }

    private void tryMakingFlight(Airline airline){
        removeTooltip();
        if(step == Step.IN_FLIGHT) {
            if (route == null) {
                gameUIManager.showNotificationWindow("Choose the starting airport and group first.");
                return;
            }
            Map<Integer, String> errors = route.makeFlight(airline);
            if (errors != null) {
                gameUIManager.showNotificationWindow(errors.values().iterator().next());
                return;
            }
            flightHUD.updateHudOnNextFrame();
            gameUIManager.handleAirportClick(route.getCurrentAirport());
        }
    }


    public void selectPassengerGroup(PassengerType passengerType) {
        if(step == Step.IN_FLIGHT) {
            String error = route.addPassenger(passengerType);
            if (error != null) {
                gameUIManager.showNotificationWindow(error);
            }
            else
                flightHUD.updateHudOnNextFrame();
        }
    }

    public void removePassengerGroup(Integer index) {
        if(step == Step.IN_FLIGHT) {
            if(route.removePassenger(index))
                flightHUD.updateHudOnNextFrame();
        }
    }

    private void passFlight() {
        llh.sendRoutePass();
        setInactive();
        gameUIManager.showNotificationWindow("Flight stage skipped.");
    }

    private void finishRoute(boolean finishStage) {
        if (route != null && !route.canFinishRoute()) {
            gameUIManager.showNotificationWindow("Route is not finished yet.\nDeliver all passengers or press reset and then pass!");
            return;
        }
        if (route == null) return;
        llh.sendRouteResponse(route, finishStage);
        setInactive();
        gameUIManager.showNotificationWindow("Flight request was sent to server.");
    }

    private void goBack() {
        if(step == Step.IN_FLIGHT) {
            if(route.isInStartingAirport()){
                returnToStartingAirportChoice();
            }
            else {
                boolean flightUndone = route.undoFlight();
                if (flightUndone) {
                    gameUIManager.handleAirportClick(route.getCurrentAirport());
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
        removeTooltip();
        removePlaneWindow();
        flightHUD.updateHudOnNextFrame();
    }

    private void returnToStartingAirportChoice(){
        route = null;
        step = Step.CHOOSING_STARTING_AIRPORT;
        removeTooltip();
        removePlaneWindow();
        flightHUD.updateHudOnNextFrame();
    }

    public void setInactive(){
        if(active) {
            route = null;
            step = Step.SELECT_PLANE;
            selectedPlane = null;
            removePlaneWindow();
            flightHUD.setVisible(false);
            removeTooltip();
            flightHUD.updateHudOnNextFrame();
            active = false;
        }
    }


    public void positionPlaneWindow() {
        if (planeWindow == null) return;
        planeWindow.pack();
        planeWindow.setPosition(
            (uiStageHUD.getWidth() - planeWindow.getWidth()) / 2f,
            (uiStageHUD.getHeight() - planeWindow.getHeight()) / 2f);
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
        uiStageHUD.addActor(planeWindow);
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





    public void showTooltip(Airline airline, Airport airport) {
        removeTooltip();
        if(active && step == Step.IN_FLIGHT && airline!=null) {
            currentTooltip = new ContextMenuWithButtons(skin, ()->tryMakingFlight(airline));
            uiStageHUD.addActor(currentTooltip.asWindow());
            updateTooltip(airline, airport);
        }
        else if(active && step == Step.CHOOSING_STARTING_AIRPORT && airport!=null){
            currentTooltip = new ContextMenuWithButtons(skin, ()->tryChoosingAirport(airport));
            uiStageHUD.addActor(currentTooltip.asWindow());
            updateTooltip(airline, airport);
        }
    }

    public void removeTooltip() {
        if (currentTooltip != null) {
            currentTooltip.asWindow().remove();
            currentTooltip = null;
        }
    }

    public void updateTooltip(Airline currentAirline, Airport currentAirport){
        if(active && currentTooltip != null){
            if(currentAirport!=null){
                ContextMenuPositionManager.updateTooltipPosition(currentTooltip, currentAirport, uiStageHUD, camera);
                ContextMenuPositionManager.clearTooltipScrollFocusWhenPointerLeaves(currentTooltip, uiStageHUD);
            }
            else if(currentAirline!=null){
                ContextMenuPositionManager.updateTooltipPosition(currentTooltip, currentAirline, uiStageHUD, camera);
                ContextMenuPositionManager.clearTooltipScrollFocusWhenPointerLeaves(currentTooltip, uiStageHUD);
            }
            else
                removeTooltip();
        }
        else
            removeTooltip();
    }

    public boolean isPointerOverTooltip() {
        return ContextMenuPositionManager.isPointerOverTooltip(currentTooltip);
    }
}
