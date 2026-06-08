package com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.HUD.HUDDirectory.FlightHUDDirectory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Airport;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PassengerType;
import com.game.Ticket_To_Flight.commonFrontAndBack.Route;
import com.game.Ticket_To_Flight.frontend.components.details.PassengerDetailsWidget;
import com.game.Ticket_To_Flight.frontend.components.buttons.SelectButton;
import com.game.Ticket_To_Flight.frontend.components.tables.expandable.ExpandableListWidget;
import com.game.Ticket_To_Flight.frontend.components.tables.passenger.PassengerTableWidget;
import com.game.Ticket_To_Flight.frontend.components.tables.flight.AbstractFlightPanel;
import com.game.Ticket_To_Flight.frontend.components.texts.SingleLineText;

import java.util.*;
import java.util.function.Consumer;

public class GroupHUD extends AbstractFlightPanel {
    private static final float CONTENT_WIDTH = 760f;
    private static final float CONTENT_HEIGHT = 260f;

    private final ScrollPane scrollPane;
    private final Table contentTable;

    private float currentTopY = 0;
    private Route currentRoute;
    private Consumer<Integer> onPassengerRemoved;

    public GroupHUD(Skin skin) {
        super(skin);

        contentTable = new Table();
        contentTable.top().left();

        scrollPane = new ScrollPane(contentTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);

        scrollPane.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (event.getStage() != null) event.getStage().setScrollFocus(scrollPane);
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (event.getStage() != null) event.getStage().setScrollFocus(null);
            }
        });
    }

    public void setCallbacks(Consumer<Integer> onPassengerRemoved) {
        this.onPassengerRemoved = onPassengerRemoved;
    }

    public void updateData( Route route) {
        this.currentRoute = route;
        renderContent();
    }

    public void forceUpdate() {
        renderContent();
    }

    @Override
    protected void renderContent() {
        clearChildren();
        contentTable.clearChildren();

        add(buildHeader("Passengers on Board")).fillX().expandX().row();

        if (!isCollapsed) {
            if (currentRoute == null || currentRoute.getPassengers().isEmpty()) {
                contentTable.add(new SingleLineText("Plane is empty", skin)).left().pad(15).row();
            }
            else {
                List<Route.BoarderPassenger> routePassengers = currentRoute.getPassengers();
                List<ExpandableListWidget> activeLists = new ArrayList<>();

                for (int i = 0; i < routePassengers.size(); ++i) {
                    Route.BoarderPassenger bp = routePassengers.get(i);
                    if (bp.isFinished()) continue;

                    PassengerType pt = bp.getType();
                    PassengerTableWidget ptWidget = new PassengerTableWidget(pt);
                    ExpandableListWidget listWidget = new ExpandableListWidget(ptWidget.passengerClass(), skin);
                    listWidget.setPreferredWidth(CONTENT_WIDTH);
                    activeLists.add(listWidget);

                    SelectButton deleteBtn = createDeleteButton(i, bp.canBeRemoved());
                    listWidget.addHeaderActor(deleteBtn, 160f, 55f);

                    Table passengerContent = listWidget.getContentTable();
                    PassengerDetailsWidget.fill(passengerContent, skin, pt);

                    listWidget.setCallbacks(
                        () -> {
                            for (ExpandableListWidget other : activeLists) if (other != listWidget) other.collapse();
                        },
                        () -> {
                            invalidateHierarchy();
                            contentTable.layout();
                            scrollPane.layout();
                        }
                    );

                    contentTable.add(listWidget).width(CONTENT_WIDTH).fillX().expandX().padTop(8).row();
                }
            }
            add(scrollPane).width(CONTENT_WIDTH).height(CONTENT_HEIGHT).padTop(10).row();
        }

        if (screenWidth > 0 && currentTopY > 0) recalculatePosition();
    }

    private SelectButton createDeleteButton(int passenger, boolean canRemove) {
        SelectButton button = new SelectButton("Delete", skin, () -> {
            if (onPassengerRemoved != null) onPassengerRemoved.accept(passenger);
        });

        button.setDisabled(!canRemove);
        button.getLabel().setColor(canRemove ? new Color(1f, 0.4f, 0.4f, 1f) : Color.DARK_GRAY);
        return button;
    }

    public void layoutFor(float width, float height, float topY) {
        this.screenWidth = width;
        this.screenHeight = height;
        this.currentTopY = topY;
        recalculatePosition();
    }

    @Override
    protected void recalculatePosition() {
        pack();
        setWidth(Math.max(getWidth(), 820));
        setPosition(screenWidth - getWidth() - 20, currentTopY - getHeight() - 14);
    }
}
