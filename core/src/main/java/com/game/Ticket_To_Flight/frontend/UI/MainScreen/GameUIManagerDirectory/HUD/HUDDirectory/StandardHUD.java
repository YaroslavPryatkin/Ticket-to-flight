package com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.HUD.HUDDirectory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Player;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.frontend.LowLevelHandlerFront;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.HUD.HUDDirectory.StandardHUDDirectory.PlayersListHUD;
import com.game.Ticket_To_Flight.frontend.components.texts.SingleLineText;

public class StandardHUD extends Table {
    private final GameData gameData;
    private final LowLevelHandlerFront llh;

    private final Label roundLabel, stageLabel, timeLabel;
    private final PlayersListHUD playersListHUD;

    public StandardHUD(Skin skin, GameData gameData, LowLevelHandlerFront llh) {
        this.gameData = gameData;
        this.llh = llh;

        this.setFillParent(true);
        this.top().left();
        this.pad(20, 20, 20, 20);

        this.setTouchable(Touchable.childrenOnly);

        roundLabel = new SingleLineText("Round: ", skin);
        stageLabel = new SingleLineText("Stage: ", skin);
        timeLabel = new SingleLineText("Time: ", skin);
        timeLabel.setColor(Color.ORANGE);

        playersListHUD = new PlayersListHUD(gameData, llh, skin);

        Table leftStats = new Table();
        leftStats.top().left();

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0, 0, 0, 0.7f)); // Черный цвет с 70% прозрачности
        pixmap.fill();
        TextureRegionDrawable bg = new TextureRegionDrawable(new Texture(pixmap));
        pixmap.dispose();

        leftStats.setBackground(bg);
        leftStats.pad(20);
        leftStats.setTouchable(Touchable.childrenOnly);

        leftStats.add(roundLabel).left().row();
        leftStats.add(stageLabel).left().padTop(15).row();
        leftStats.add(timeLabel).left().padTop(15).row();

        leftStats.add(playersListHUD).left().padTop(30).row();

        this.add(leftStats).left().top().expandY().fillY();
    }

    public void updateHUD(Player chosenPlayer) {
        String currentStage = gameData.currentState.toString();
        Player activePlayer = chosenPlayer == null ? gameData.players.get(llh.getMyId()) : chosenPlayer;

        updateGlobalGameStats(currentStage);

        playersListHUD.updateData();
    }

    private void updateGlobalGameStats(String stage) {
        roundLabel.setText("Round: " + gameData.roundNumber);
        stageLabel.setText("Stage: " + stage);
        timeLabel.setText("Time: 120s");
    }
}
