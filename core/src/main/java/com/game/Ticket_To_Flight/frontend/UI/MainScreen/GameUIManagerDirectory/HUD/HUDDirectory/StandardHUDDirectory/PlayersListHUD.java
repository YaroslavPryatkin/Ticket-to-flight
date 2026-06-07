package com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.HUD.HUDDirectory.StandardHUDDirectory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.game.Ticket_To_Flight.Utilities.MapHolder;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Player;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PlaneType;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.frontend.LowLevelHandlerFront;
import com.game.Ticket_To_Flight.frontend.components.details.PlaneDetailsWidget;
import com.game.Ticket_To_Flight.frontend.components.subsidiary.ComponentHover;
import com.game.Ticket_To_Flight.frontend.components.background.SolidRectangleBackground;
import com.game.Ticket_To_Flight.frontend.components.tables.expandable.ExpandableListWidget;
import com.game.Ticket_To_Flight.frontend.components.texts.SingleLineText;
import com.game.Ticket_To_Flight.frontend.components.texts.Text;
import com.game.Ticket_To_Flight.frontend.components.texts.WrappedText;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PlayersListHUD extends Table {
    private static final float FIXED_WIDTH = 650f;
    private static final float INNER_WIDTH = FIXED_WIDTH - 30f;

    private static final float MAX_HEIGHT = 550f;

    private final GameData gameData;
    private final LowLevelHandlerFront llh;
    private final Skin skin;

    private final List<ExpandableListWidget> activeExpandLists = new ArrayList<>();

    private final Table contentTable;
    private final ScrollPane scrollPane;

    public PlayersListHUD(GameData gameData, LowLevelHandlerFront llh, Skin skin) {
        this.gameData = gameData;
        this.llh = llh;
        this.skin = skin;
        top().left();

        contentTable = new Table();
        contentTable.top().left();

        scrollPane = new ScrollPane(contentTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);

        scrollPane.setCancelTouchFocus(false);

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

        add(scrollPane).width(FIXED_WIDTH).maxHeight(MAX_HEIGHT).top().left();
    }

    public void updateData() {
        contentTable.clearChildren();
        activeExpandLists.clear();

        for (Player p : gameData.players) {
            if (p == null) continue;

            boolean isCurrentTurn = (p.getId() == gameData.currentPlayer);

            SolidRectangleBackground playerRow = new SolidRectangleBackground(0, 0, 0, 0,
                isCurrentTurn ? new Color(0.2f, 0.4f, 0.8f, 0.9f) : new Color(0.15f, 0.15f, 0.15f, 0.8f),
                new Color(0.25f, 0.45f, 0.85f, 0.9f),
                new Color(0.2f, 0.4f, 0.8f, 0.9f)
            );
            playerRow.left().pad(15);

            playerRow.setTouchable(Touchable.enabled);

            Table header = new Table();
            Label nameLabel = new SingleLineText(p.getName(), skin);
            if (p.getColor() != null) nameLabel.setColor(p.getColor());

            header.add(nameLabel).left().expandX();
            header.add(new SingleLineText("AP: " + p.getActionPoints(), skin)).right().top();
            playerRow.add(header).width(INNER_WIDTH).row();

            Table moneyRow = new Table();
            String incomeStr = p.getIncome() >= 0 ? "+$" + p.getIncome() : "-$" + Math.abs(p.getIncome());
            Label moneyLabel = new SingleLineText("$" + p.getMoney() + " (" + incomeStr + ")", skin);
            moneyLabel.setColor(Color.WHITE);
            moneyRow.add(moneyLabel).left().expandX();

            if (gameData.currentState == GameData.State.AUCTION) {
                boolean isPass = gameData.players.get(gameData.currentPlayer).hasPassed;

                String betText = isPass ? "Pass" : "Bet: $" + p.getAuctionBet();
                Label betLabel = new SingleLineText(betText, skin);

                betLabel.setColor(isPass ? Color.GRAY : Color.ORANGE);
                moneyRow.add(betLabel).right();
            }
            playerRow.add(moneyRow).width(INNER_WIDTH).padTop(8).row();

            String abilityStr = p.getAbility() != null ? p.getAbility().description : "None";
            Label abilityLabel = new WrappedText("Ability: " + abilityStr, skin, INNER_WIDTH);
            abilityLabel.setColor(Color.LIGHT_GRAY);

            playerRow.add(abilityLabel).width(INNER_WIDTH).left().padTop(5).row();

            ExpandableListWidget planesList = new ExpandableListWidget("Planes", skin);
            planesList.setPreferredWidth(INNER_WIDTH);
            activeExpandLists.add(planesList);
            Table innerContent = planesList.getContentTable();

            List<ExpandableListWidget> nestedPlaneLists = new ArrayList<>();
            Iterator<Map.Entry<PlaneType, Integer>> it = MapHolder.viewAsEntrySet(p.planes);
            boolean hasPlanes = false;

            while (it.hasNext()) {
                Map.Entry<PlaneType, Integer> entry = it.next();
                if (entry != null && entry.getValue() > 0) {
                    PlaneType pt = entry.getKey();
                    int amount = entry.getValue();

                    ExpandableListWidget singlePlaneExpand = new ExpandableListWidget(pt.description + " (x" + amount + ")", skin);
                    singlePlaneExpand.setPreferredWidth(INNER_WIDTH);
                    nestedPlaneLists.add(singlePlaneExpand);

                    Table ptContent = singlePlaneExpand.getContentTable();
                    PlaneDetailsWidget.fill(ptContent, skin, pt);

                    singlePlaneExpand.setCallbacks(
                        () -> {
                            for (ExpandableListWidget other : nestedPlaneLists) {
                                if (other != singlePlaneExpand) other.collapse();
                            }
                        },
                        () -> {
                            invalidateHierarchy();
                            contentTable.layout();
                            scrollPane.layout();
                        }
                    );

                    innerContent.add(singlePlaneExpand).width(INNER_WIDTH).padTop(5).row();
                    hasPlanes = true;
                }
            }

            if (!hasPlanes) {
                innerContent.add(new SingleLineText("No planes yet", skin)).left().padTop(4).row();
            }

            planesList.setCallbacks(
                () -> {
                    for (ExpandableListWidget other : activeExpandLists) {
                        if (other != planesList) other.collapse();
                    }
                },
                () -> {
                    invalidateHierarchy();
                    contentTable.layout();
                    scrollPane.layout();
                }
            );

            playerRow.add(planesList).width(INNER_WIDTH).padTop(10).row();

            contentTable.add(playerRow).width(FIXED_WIDTH).padBottom(10).left().row();
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (getStage() != null) {
            if (ComponentHover.isMouseOver(this)) {
                getStage().setScrollFocus(scrollPane);
            }
        }
    }
}
