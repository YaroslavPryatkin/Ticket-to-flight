package com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.GameUIManagerDirectory.HUD;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.game.Ticket_To_Flight.Utilities.MapHolder;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Player;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PlaneType;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.frontend.LowLevelHandlerFront;

import java.util.Iterator;
import java.util.Map;

public class HUDOverlay extends Table {
    private final GameData gameData;
    private final LowLevelHandlerFront llh;
    private final Skin skin;

    private final Label roundLabel;
    private final Label stageLabel;
    private final Label timeLabel;

    private final Label moneyLabel;
    private final Label incomeLabel;
    private final Label currentBetLabel;
    private final Label planesLabel;

    private final Table playersTable;

    public HUDOverlay(Stage uiStageHUD, Skin skin, GameData gameData, LowLevelHandlerFront llh) {
        this.gameData = gameData;
        this.llh = llh;
        this.skin = skin;

        this.setFillParent(true);
        this.top();
        this.pad(20);

        roundLabel = new Label("Round: ", skin);
        stageLabel = new Label("Stage: ", skin);
        timeLabel = new Label("Time: ", skin);
        timeLabel.setColor(Color.ORANGE);

        moneyLabel = new Label("Money: ", skin);
        incomeLabel = new Label("Income: ", skin);
        incomeLabel.setColor(Color.GREEN);
        currentBetLabel = new Label("Current bet: ", skin);
        currentBetLabel.setColor(Color.ORANGE);
        planesLabel = new Label("Planes: None", skin);
        planesLabel.setColor(Color.CYAN);

        playersTable = new Table();

        Table leftStats = new Table();
        leftStats.add(roundLabel).left().row();
        leftStats.add(stageLabel).left().padTop(15).row();
        leftStats.add(timeLabel).left().padTop(15).row();
        leftStats.add(playersTable).left().padTop(30).expandX().fillX().row();

        Table rightStats = new Table();
        rightStats.add(moneyLabel).right().row();
        rightStats.add(incomeLabel).right().padTop(15).row();
        rightStats.add(planesLabel).right().padTop(15).row();
        rightStats.add(currentBetLabel).right().padTop(15).row();

        this.add(leftStats).expandX().left().top();
        this.add(rightStats).expandX().right().top();

        uiStageHUD.addActor(this);
    }

    public void updateHUD(Player chosenPlayer) {
        String currentStage = gameData.currentState.toString();
        Player activePlayer = chosenPlayer == null ? gameData.players.get(llh.getMyId()) : chosenPlayer;

        updateGlobalGameStats(currentStage);
        updatePlayerStats(activePlayer, currentStage);
        updatePlayersListTable();
    }

    private void updateGlobalGameStats(String stage) {
        int round = gameData.roundNumber;
        int time = 120;

        roundLabel.setText("Round: " + round);
        stageLabel.setText("Stage: " + stage);
        timeLabel.setText("Time: " + time + "s");
    }

    private void updatePlayerStats(Player player, String stage) {
        moneyLabel.setText("Money: $" + player.getMoney());
        incomeLabel.setText("Income: " + formatIncome(player.getIncome()));

        if (stage.equalsIgnoreCase("Auction")) {
            currentBetLabel.setVisible(true);
            currentBetLabel.setText("Current bet: $" + player.getAuctionBet());
        } else {
            currentBetLabel.setVisible(false);
        }

        planesLabel.setText(getPlanesFormattedText(player));
    }

    private String getPlanesFormattedText(Player player) {
        StringBuilder planesText = new StringBuilder("Planes: ");
        boolean hasPlanes = false;

        Iterator<Map.Entry<PlaneType, Integer>> it = MapHolder.viewAsEntrySet(player.planes);
        while (it.hasNext()) {
            Map.Entry<PlaneType, Integer> e = it.next();
            if (e != null && e.getValue() > 0) {
                planesText.append(e.getKey().description)
                    .append(" (").append(e.getValue()).append("), ");
                hasPlanes = true;
            }
        }

        if (hasPlanes) {
            planesText.setLength(planesText.length() - 2);
        } else {
            planesText.append("None");
        }

        return planesText.toString();
    }

    private void updatePlayersListTable() {
        playersTable.clearChildren();

        for (Player p : gameData.players) {
            if (p == null) continue;

            Table playerRow = createPlayerRow(p);
            playersTable.add(playerRow).width(400).padBottom(5).left().row();
        }
    }

    private Table createPlayerRow(Player p) {
        Table playerRow = new Table();
        playerRow.left().pad(8, 15, 8, 15);

        boolean isCurrentTurn = (p.getId() == gameData.currentPlayer);
        String bgStyle = isCurrentTurn ? "blue-bg" : "dark-bg";
        playerRow.setBackground(skin.getDrawable(bgStyle));

        Label nameLabel = new Label(p.getName(), skin);
        if (p.getColor() != null) {
            nameLabel.setColor(p.getColor());
        }

        Label statsLabel = new Label("  $" + p.getMoney() + " (" + formatIncome(p.getIncome()) + ")", skin);
        statsLabel.setColor(Color.WHITE);

        playerRow.add(nameLabel).left().row();
        playerRow.add(statsLabel).left();

        return playerRow;
    }

    private String formatIncome(Integer income) {
        if (income == null) return "$0";
        return income >= 0 ? "+$" + income : "-$" + Math.abs(income);
    }

    public void resize() {
        this.invalidateHierarchy();
    }
}
