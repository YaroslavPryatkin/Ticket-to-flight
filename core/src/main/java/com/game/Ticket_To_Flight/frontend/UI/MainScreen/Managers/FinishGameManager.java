package com.game.Ticket_To_Flight.frontend.UI.MainScreen.Managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Player;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.commonFrontAndBack.RatingRecord;
import com.game.Ticket_To_Flight.frontend.LowLevelHandlerFront;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManager;
import com.game.Ticket_To_Flight.frontend.components.buttons.RoundedButton;
import com.game.Ticket_To_Flight.frontend.components.texts.SingleLineText;
import com.game.Ticket_To_Flight.frontend.components.windows.BaseGameWindow;

import java.util.Comparator;
import java.util.List;

public class FinishGameManager extends BaseGameWindow {

    public FinishGameManager(Skin skin, GameData gameData, GameUIManager uiManager, LowLevelHandlerFront llh, Stage uiStageWindow) {
        super("Game Results", skin, 700, 600);

        this.setColor(new Color(0.2f, 0.6f, 0.95f, 0.95f));

        this.padLeft(40).padRight(40).padBottom(30);

        Table playersTable = new Table();
        playersTable.top();

        SingleLineText rankHeader = new SingleLineText("Rank", skin);
        SingleLineText nameHeader = new SingleLineText("Player", skin);
        SingleLineText scoreHeader = new SingleLineText("Score", skin);

        rankHeader.setColor(Color.LIGHT_GRAY);
        nameHeader.setColor(Color.LIGHT_GRAY);
        scoreHeader.setColor(Color.LIGHT_GRAY);

        playersTable.add(rankHeader).padRight(20).padBottom(15);
        playersTable.add(nameHeader).expandX().left().padBottom(15);
        playersTable.add(scoreHeader).padLeft(20).padBottom(15).row();

        if (llh.getGameFinishRating() != null) {
            List<RatingRecord> sortedRatingRecord = llh.getGameFinishRating();
            for (int place = 0; place < sortedRatingRecord.size(); ++place) {
                RatingRecord record = sortedRatingRecord.get(place);
                Player player = record.getPlayer(gameData);
                String playerName = (player != null) ? player.getName() : "Unknown Player";

                SingleLineText rankText = new SingleLineText("#" + place, skin);
                SingleLineText nameText = new SingleLineText(playerName, skin);
                SingleLineText scoreText = new SingleLineText(String.valueOf(record.getRating()), skin);

                if (player != null && player.getColor() != null) {
                    nameText.setColor(player.getColor());
                }

                playersTable.add(rankText).padRight(20).padBottom(10);
                playersTable.add(nameText).expandX().left().padBottom(10);
                playersTable.add(scoreText).padLeft(20).padBottom(10).row();
            }
        }

        ScrollPane scrollPane = new ScrollPane(playersTable, skin);
        scrollPane.setFadeScrollBars(false);
        registerScrollFocus(scrollPane);

        TextButton okButton = new RoundedButton("OK", skin);
        okButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        this.add(scrollPane).width(600).height(350).padBottom(30).row();
        this.add(okButton).width(200).height(70);

        this.pack();

        float x = (uiStageWindow.getWidth() - this.getWidth())/2;

        float y = (uiStageWindow.getHeight() - this.getHeight())/2;

        uiStageWindow.addActor(this);
        this.setPosition(x,y);
    }
}
