package com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.Flights;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.game.Ticket_To_Flight.Utilities.MapHolder;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airline;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Player;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PassengerType;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PlaneType;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.commonFrontAndBack.Route;
import com.game.Ticket_To_Flight.frontend.LowLevelHandlerFront;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManager;
import com.game.Ticket_To_Flight.frontend.components.buttons.RoundedButton;
import com.game.Ticket_To_Flight.frontend.components.texts.SingleLineText;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainFlightController {
    private enum Step {
        SELECT_PLANE,
        CHOOSE_AIRPORT_GROUP,
        CHOOSE_AIRLINE
    }

    private static class ChosenGroup {
        private final Airport airport;
        private final PassengerType passengerType;

        private ChosenGroup(Airport airport, PassengerType passengerType) {
            this.airport = airport;
            this.passengerType = passengerType;
        }
    }

    private final Stage stage;
    private final Skin skin;
    private final GameData gameData;
    private final LowLevelHandlerFront llh;
    private final GameUIManager uiManager;
    private final Deque<Runnable> undoStack = new ArrayDeque<>();
    private final List<ChosenGroup> chosenGroups = new ArrayList<>();

    private Step step = Step.SELECT_PLANE;
    private Route route;
    private PlaneType selectedPlane;
    private Table planeWindow;
    private Table statusPanel;
    private TextButton resetButton;
    private TextButton backButton;
    private TextButton finishButton;
    private boolean wasActive = false;

    public MainFlightController(Stage stage, Skin skin, GameData gameData, LowLevelHandlerFront llh, GameUIManager uiManager) {
        this.stage = stage;
        this.skin = skin;
        this.gameData = gameData;
        this.llh = llh;
        this.uiManager = uiManager;
    }

    public void update() {
        if (!isActive()) {
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

        ensureControls();
        updateStatusPanel();
        position();
    }

    public void position() {
        positionPlaneWindow();
        positionStatusPanel();
        positionControlButtons();
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

    private void selectPlane(PlaneType plane) {
        selectedPlane = plane;
        step = Step.CHOOSE_AIRPORT_GROUP;
        removePlaneWindow();
        undoStack.push(this::resetState);
        uiManager.showSuccessWindow("Plane was selected successfully. Choose the first airport and the group.");
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

    private void showPlaneSelection() {
        removePlaneWindow();
        planeWindow = new Table();
        planeWindow.setBackground(skin.getDrawable("background"));
        planeWindow.pad(40);

        planeWindow.add(new SingleLineText("Choose the plane", skin)).padBottom(30).row();
        Table planesTable = new Table();

        Player player = gameData.players.get(llh.getMyId());
        Iterator<Map.Entry<PlaneType, Integer>> iterator = MapHolder.viewAsEntrySet(player.planes);
        Map.Entry<PlaneType, Integer> entry;
        boolean hasPlanes = false;
        while ((entry = iterator.next()) != null) {
            hasPlanes = true;
            PlaneType plane = entry.getKey();
            Integer count = entry.getValue();
            TextButton button = new RoundedButton(plane.description + " x" + count, skin);
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    selectPlane(plane);
                }
            });
            planesTable.add(button).width(900).height(80).padBottom(20).row();
        }
        if (!hasPlanes) {
            planesTable.add(new SingleLineText("No planes available", skin)).padBottom(20).row();
        }

        ScrollPane scrollPane = new ScrollPane(planesTable, skin);
        planeWindow.add(scrollPane).width(1000).height(500);
        stage.addActor(planeWindow);
        positionPlaneWindow();
    }

    private void ensureControls() {
        if (statusPanel == null) {
            statusPanel = new Table();
            statusPanel.setBackground(skin.getDrawable("background"));
            statusPanel.pad(25);
            stage.addActor(statusPanel);
        }
        if (resetButton == null) {
            resetButton = createControlButton("Reset", () -> resetAll());
            stage.addActor(resetButton);
        }
        if (backButton == null) {
            backButton = createControlButton("Back", () -> goBack());
            stage.addActor(backButton);
        }
        if (finishButton == null) {
            finishButton = createControlButton("Finish route", () -> finishRoute());
            stage.addActor(finishButton);
        }
    }

    private TextButton createControlButton(String text, Runnable action) {
        TextButton button = new RoundedButton(text, skin);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action.run();
            }
        });
        return button;
    }

    private void updateStatusPanel() {
        if (statusPanel == null) return;

        statusPanel.clearChildren();
        statusPanel.add(new SingleLineText("Flight status", skin)).left().padBottom(20).row();
        statusPanel.add(new SingleLineText("Step: " + stepText(), skin)).left().padBottom(15).row();

        String planeText = selectedPlane == null ? "None" : selectedPlane.description;
        statusPanel.add(new SingleLineText("Chosen Plane: " + planeText, skin)).left().padBottom(15).row();

        if (route != null) {
            statusPanel.add(new SingleLineText("Current airport: " + route.getCurrentAirport().getCityName(), skin)).left().padBottom(15).row();
            statusPanel.add(new SingleLineText("Airlines selected: " + route.getLinesCount(), skin)).left().padBottom(15).row();
        }

        for (ChosenGroup group : chosenGroups) {
            statusPanel.add(new SingleLineText("Group to: " + group.passengerType.typeTo.description, skin)).left().row();
            statusPanel.add(new SingleLineText("Composition: " + group.passengerType.size + " passengers", skin)).left().row();
            statusPanel.add(new SingleLineText("Type: " + group.passengerType.description, skin)).left().padBottom(15).row();
        }

        boolean canFinish = route != null && route.canFinishRoute();
        finishButton.setDisabled(!canFinish);
        finishButton.getLabel().setColor(canFinish ? Color.WHITE : Color.LIGHT_GRAY);
    }

    private String stepText() {
        if (step == Step.SELECT_PLANE) return "Choose plane";
        if (step == Step.CHOOSE_AIRPORT_GROUP) return "Choose airport and group";
        return "Choose airline";
    }

    private void positionPlaneWindow() {
        if (planeWindow == null) return;
        planeWindow.pack();
        planeWindow.setPosition(
            (stage.getWidth() - planeWindow.getWidth()) / 2f,
            (stage.getHeight() - planeWindow.getHeight()) / 2f
        );
    }

    private void positionStatusPanel() {
        if (statusPanel == null) return;
        statusPanel.setSize(760, 700);
        statusPanel.setPosition(stage.getWidth() - statusPanel.getWidth() - 40, stage.getHeight() - statusPanel.getHeight() - 40);
    }

    private void positionControlButtons() {
        float width = 260;
        float height = 80;
        float gap = 20;
        float y = 40;
        float x = stage.getWidth() - width - 40;

        if (resetButton != null) {
            resetButton.setSize(width, height);
            resetButton.setPosition(x, y);
        }
        if (backButton != null) {
            backButton.setSize(width, height);
            backButton.setPosition(x - width - gap, y);
        }
        if (finishButton != null) {
            finishButton.setSize(width + 120, height);
            finishButton.setPosition(x - (width + 120) * 2 - gap, y);
        }
    }

    private void clearUi() {
        removePlaneWindow();
        if (statusPanel != null) {
            statusPanel.remove();
            statusPanel = null;
        }
        if (resetButton != null) {
            resetButton.remove();
            resetButton = null;
        }
        if (backButton != null) {
            backButton.remove();
            backButton = null;
        }
        if (finishButton != null) {
            finishButton.remove();
            finishButton = null;
        }
    }

    private void removePlaneWindow() {
        if (planeWindow != null) {
            planeWindow.remove();
            planeWindow = null;
        }
    }
}
