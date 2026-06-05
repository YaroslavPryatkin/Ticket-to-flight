package com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.HUD.HUDDirectory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.game.Ticket_To_Flight.Utilities.MapHolder;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Player;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PlaneType;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.frontend.LowLevelHandlerFront;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.HUD.HUDDirectory.StandardHUDDirectory.PlayersListHUD;
import com.game.Ticket_To_Flight.frontend.components.texts.SingleLineText;

import java.util.Iterator;
import java.util.Map;

public class StandardHUD extends Table {
    private final GameData gameData;
    private final LowLevelHandlerFront llh;

    private final Label roundLabel, stageLabel, timeLabel;
    private final Label moneyLabel, incomeLabel, currentBetLabel, planesLabel;

    private final PlayersListHUD playersListHUD;

    public StandardHUD(Skin skin, GameData gameData, LowLevelHandlerFront llh) {
        this.gameData = gameData;
        this.llh = llh;

        this.setFillParent(true);
        this.top();
        this.pad(20, 20, 20, 20);

        roundLabel = new SingleLineText("Round: ", skin);
        stageLabel = new SingleLineText("Stage: ", skin);
        timeLabel = new SingleLineText("Time: ", skin);
        timeLabel.setColor(Color.ORANGE);

        moneyLabel = new SingleLineText("Money: ", skin);
        incomeLabel = new SingleLineText("Income: ", skin);
        incomeLabel.setColor(Color.GREEN);
        currentBetLabel = new SingleLineText("Current bet: ", skin);
        currentBetLabel.setColor(Color.ORANGE);
        planesLabel = new SingleLineText("Planes: None", skin);
        planesLabel.setColor(Color.CYAN);

        playersListHUD = new PlayersListHUD(gameData, llh, skin);

        Table leftStats = new Table();
        leftStats.add(roundLabel).left().row();
        leftStats.add(stageLabel).left().padTop(15).row();
        leftStats.add(timeLabel).left().padTop(15).row();

        leftStats.add(playersListHUD).left().padTop(30).expandY().fillY().row();

        Table rightStats = new Table();
        rightStats.add(moneyLabel).right().row();
        rightStats.add(incomeLabel).right().padTop(15).row();
        rightStats.add(planesLabel).right().padTop(15).row();
        rightStats.add(currentBetLabel).right().padTop(15).row();

        this.add(leftStats).expandY().fillY().left().top();
        this.add(rightStats).expandX().right().top();
    }

    public void updateHUD(Player chosenPlayer) {
        String currentStage = gameData.currentState.toString();
        Player activePlayer = chosenPlayer == null ? gameData.players.get(llh.getMyId()) : chosenPlayer;

        updateGlobalGameStats(currentStage);
        updatePlayerStats(activePlayer, currentStage);

        playersListHUD.updateData();
    }

    private void updateGlobalGameStats(String stage) {
        roundLabel.setText("Round: " + gameData.roundNumber);
        stageLabel.setText("Stage: " + stage);
        timeLabel.setText("Time: 120s");
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
                planesText.append(e.getKey().description).append(" (").append(e.getValue()).append("), ");
                hasPlanes = true;
            }
        }

        if (hasPlanes) planesText.setLength(planesText.length() - 2);
        else planesText.append("None");
        return planesText.toString();
    }

    private String formatIncome(Integer income) {
        if (income == null) return "$0";
        return income >= 0 ? "+$" + income : "-$" + Math.abs(income);
    }
}
