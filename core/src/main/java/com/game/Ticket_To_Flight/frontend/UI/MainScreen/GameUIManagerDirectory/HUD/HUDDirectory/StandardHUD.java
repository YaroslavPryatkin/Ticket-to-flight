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
    private final Table leftStats;

    private final Label roundLabel, stageLabel, timeLabel;
    private final PlayersListHUD playersListHUD;

    public StandardHUD(Skin skin, GameData gameData, LowLevelHandlerFront llh) {
        this.gameData = gameData;
        this.llh = llh;

        this.setFillParent(true);
        this.top().left();
        //this.pad(20, 20, 20, 20);

        this.setTouchable(Touchable.childrenOnly);

        roundLabel = new SingleLineText("Round: ", skin);
        stageLabel = new SingleLineText("Stage: ", skin);
        timeLabel = new SingleLineText("Current bet: ", skin);

        playersListHUD = new PlayersListHUD(gameData, llh, skin);

        leftStats = new Table();
        leftStats.top().left();

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0, 0, 0, 0.7f)); // Черный цвет с 70% прозрачности
        pixmap.fill();
        TextureRegionDrawable bg = new TextureRegionDrawable(new Texture(pixmap));
        pixmap.dispose();

        leftStats.setBackground(bg);
        leftStats.pad(20);
        leftStats.setTouchable(Touchable.childrenOnly);

        addLeftStatsChildren();

        this.add(leftStats).left().top().expandY().fillY();
    }

    public void updateHUD() {
        updateGlobalGameStats();
        playersListHUD.updateData();
    }

    private GameData.State lastState = null;

    private void addLeftStatsChildren(){
        leftStats.add(roundLabel).left().row();
        leftStats.add(stageLabel).left().padTop(15).row();
        if(gameData.currentState == GameData.State.AUCTION)
            leftStats.add(timeLabel).left().padTop(15).row();
        leftStats.add(playersListHUD).left().padTop(30).row();
        lastState = gameData.currentState;
    }

    private void updateGlobalGameStats() {
        roundLabel.setText("Round: " + gameData.roundNumber);
        stageLabel.setText("Stage: " + gameData.currentState.toString());
        timeLabel.setText("Current bet: " + gameData.currentBet);
        if(lastState!=gameData.currentState){
            leftStats.clearChildren();
            addLeftStatsChildren();
        }
    }
}
