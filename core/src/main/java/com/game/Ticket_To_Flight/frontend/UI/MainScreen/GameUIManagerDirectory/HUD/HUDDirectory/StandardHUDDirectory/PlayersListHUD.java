package com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.HUD.HUDDirectory.StandardHUDDirectory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.game.Ticket_To_Flight.Utilities.MapHolder;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Player;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PlaneType;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.frontend.LowLevelHandlerFront;
import com.game.Ticket_To_Flight.frontend.components.background.SolidRectangleBackground;
import com.game.Ticket_To_Flight.frontend.components.ExpandableListWidget;
import com.game.Ticket_To_Flight.frontend.components.texts.SingleLineText;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PlayersListHUD extends Table {
    private static final float MIN_PLAYER_ROW_WIDTH = 480f;
    private static final float PLAYER_ROW_HORIZONTAL_PADDING = 30f;
    private static final float PLAYER_ROW_EXTRA_WIDTH = 20f;
    private static final float HEADER_NAME_RIGHT_PADDING = 100f;
    private static final float PLANE_STAT_LEFT_PADDING = 15f;

    private final GameData gameData;
    private final LowLevelHandlerFront llh;
    private final Skin skin;
    private final GlyphLayout glyphLayout = new GlyphLayout();

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

        add(scrollPane).expand().fill().top().left();
    }

    public void updateData() {
        contentTable.clearChildren();
        activeExpandLists.clear();

        for (Player p : gameData.players) {
            if (p == null) continue;

            boolean isCurrentTurn = (p.getId() == gameData.currentPlayer);
            float rowWidth = calculatePlayerRowWidth(p);
            float innerListWidth = rowWidth - PLAYER_ROW_HORIZONTAL_PADDING;

            SolidRectangleBackground playerRow = new SolidRectangleBackground(0, 0, 0, 0,
                isCurrentTurn ? new Color(0.2f, 0.4f, 0.8f, 0.9f) : new Color(0.15f, 0.15f, 0.15f, 0.8f),
                new Color(0.25f, 0.45f, 0.85f, 0.9f),
                new Color(0.2f, 0.4f, 0.8f, 0.9f)
            );
            playerRow.left().pad(15);

            Table header = new Table();
            Label nameLabel = new SingleLineText(p.getName(), skin);
            if (p.getColor() != null) nameLabel.setColor(p.getColor());

            header.add(nameLabel).left().expandX().padRight(HEADER_NAME_RIGHT_PADDING);
            header.add(new SingleLineText("AP: " + p.getActionPoints(), skin)).right();
            playerRow.add(header).fillX().expandX().row();

            String incomeStr = p.getIncome() >= 0 ? "+$" + p.getIncome() : "-$" + Math.abs(p.getIncome());
            Label moneyLabel = new SingleLineText("$" + p.getMoney() + " (" + incomeStr + ")", skin);
            moneyLabel.setColor(Color.WHITE);
            playerRow.add(moneyLabel).left().padTop(8).row();

            String abilityStr = p.getAbility() != null ? p.getAbility().description : "None";
            Label abilityLabel = new SingleLineText("Ability: " + abilityStr, skin);
            abilityLabel.setColor(Color.LIGHT_GRAY);

            playerRow.add(abilityLabel).expandX().fillX().left().padTop(5).row();

            ExpandableListWidget planesList = new ExpandableListWidget("Planes", skin);
            planesList.setPreferredWidth(innerListWidth);
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
                    singlePlaneExpand.setPreferredWidth(innerListWidth);
                    nestedPlaneLists.add(singlePlaneExpand);

                    Table ptContent = singlePlaneExpand.getContentTable();
                    addPlaneStat(ptContent, "Fuel", String.valueOf(pt.fuel));
                    addPlaneStat(ptContent, "Stations", String.valueOf(pt.stations));
                    addPlaneStat(ptContent, "Luxury", String.valueOf(pt.luxury));
                    addPlaneStat(ptContent, "Capacity", String.valueOf(pt.capacity));
                    addPlaneStat(ptContent, "Gate Range", formatInterval(pt.gateRange.getFrom(), pt.gateRange.getTo()));
                    addPlaneStat(ptContent, "Dist Range", formatInterval(pt.distRange.getFrom(), pt.distRange.getTo()));

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

                    innerContent.add(singlePlaneExpand).fillX().expandX().padTop(5).row();
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

            playerRow.add(planesList).fillX().expandX().padTop(10).row();

            contentTable.add(playerRow).width(rowWidth).padBottom(10).left().row();
        }
    }

    private void addPlaneStat(Table table, String label, String value) {
        Label statLabel = new SingleLineText(label + ": " + value, skin);
        statLabel.setColor(Color.LIGHT_GRAY);
        table.add(statLabel).left().padLeft(PLANE_STAT_LEFT_PADDING).padBottom(3).row();
    }

    private float calculatePlayerRowWidth(Player player) {
        float contentWidth = expandableHeaderWidth("Planes");
        contentWidth = Math.max(contentWidth,
            measureText(player.getName()) + HEADER_NAME_RIGHT_PADDING + measureText("AP: " + player.getActionPoints()));

        String incomeStr = player.getIncome() >= 0 ? "+$" + player.getIncome() : "-$" + Math.abs(player.getIncome());
        contentWidth = Math.max(contentWidth, measureText("$" + player.getMoney() + " (" + incomeStr + ")"));

        String abilityStr = player.getAbility() != null ? player.getAbility().description : "None";
        contentWidth = Math.max(contentWidth, measureText("Ability: " + abilityStr));

        Iterator<Map.Entry<PlaneType, Integer>> it = MapHolder.viewAsEntrySet(player.planes);
        boolean hasPlanes = false;
        while (it.hasNext()) {
            Map.Entry<PlaneType, Integer> entry = it.next();
            if (entry != null && entry.getValue() > 0) {
                hasPlanes = true;
                PlaneType pt = entry.getKey();
                int amount = entry.getValue();

                contentWidth = Math.max(contentWidth, expandableHeaderWidth(pt.description + " (x" + amount + ")"));
                contentWidth = Math.max(contentWidth, planeStatWidth("Fuel", String.valueOf(pt.fuel)));
                contentWidth = Math.max(contentWidth, planeStatWidth("Stations", String.valueOf(pt.stations)));
                contentWidth = Math.max(contentWidth, planeStatWidth("Luxury", String.valueOf(pt.luxury)));
                contentWidth = Math.max(contentWidth, planeStatWidth("Capacity", String.valueOf(pt.capacity)));
                contentWidth = Math.max(contentWidth, planeStatWidth("Gate Range", formatInterval(pt.gateRange.getFrom(), pt.gateRange.getTo())));
                contentWidth = Math.max(contentWidth, planeStatWidth("Dist Range", formatInterval(pt.distRange.getFrom(), pt.distRange.getTo())));
            }
        }

        if (!hasPlanes) {
            contentWidth = Math.max(contentWidth, measureText("No planes yet"));
        }

        return Math.max(MIN_PLAYER_ROW_WIDTH, contentWidth + PLAYER_ROW_HORIZONTAL_PADDING + PLAYER_ROW_EXTRA_WIDTH);
    }

    private float expandableHeaderWidth(String title) {
        return ExpandableListWidget.TOGGLE_BUTTON_WIDTH
            + ExpandableListWidget.TOGGLE_BUTTON_RIGHT_PADDING
            + measureText(title);
    }

    private float planeStatWidth(String label, String value) {
        return PLANE_STAT_LEFT_PADDING + measureText(label + ": " + value);
    }

    private float measureText(String text) {
        Label.LabelStyle labelStyle = skin.get(Label.LabelStyle.class);
        glyphLayout.setText(labelStyle.font, text);
        return glyphLayout.width;
    }

    private <T> String formatInterval(T from, T to) {
        String left = from == null ? "-inf" : from.toString();
        String right = to == null ? "+inf" : to.toString();
        return "[" + left + ", " + right + "]";
    }
}
