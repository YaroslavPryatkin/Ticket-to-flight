package com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.GameUIManagerDirectory.GameStageWindows.SubWindows;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Player;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PlaneType;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.commonFrontAndBack.StaticGameData;
import com.game.Ticket_To_Flight.frontend.LowLevelHandlerFront;
import com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.GameUIManager;
import com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.GameUIManagerDirectory.GameStageWindows.BaseGameWindow;

public class PlaneWindow extends BaseGameWindow {

    private final ButtonGroup<TextButton> buttonGroup;

    public PlaneWindow(Skin skin, final GameUIManager uiManager, final LowLevelHandlerFront llh, GameData gameData) {
        super("Plane Purchase", skin, 1400, 750);

        Player pl = gameData.players.get(gameData.currentPlayer);
        int playerMoney = pl.money;

        TextButton.TextButtonStyle affordableStyle = new TextButton.TextButtonStyle();
        affordableStyle.font = skin.getFont("default-font");
        affordableStyle.up = skin.getDrawable("dark-bg");
        affordableStyle.checked = skin.getDrawable("blue-bg");
        affordableStyle.fontColor = Color.WHITE;
        affordableStyle.checkedFontColor = Color.CYAN;
        affordableStyle.disabledFontColor = Color.GRAY;

        TextButton.TextButtonStyle expensiveStyle = new TextButton.TextButtonStyle();
        expensiveStyle.font = skin.getFont("default-font");
        expensiveStyle.up = skin.getDrawable("dark-bg");
        expensiveStyle.fontColor = Color.RED;
        expensiveStyle.disabledFontColor = Color.RED;

        buttonGroup = new ButtonGroup<>();
        buttonGroup.setMaxCheckCount(1);
        buttonGroup.setMinCheckCount(0);

        Table planesTable = new Table();

        TextButton submitBtn = new TextButton("Choose the plane", skin, "default");

        for (PlaneType plane : StaticGameData.planeTypes) {
            Table singlePlaneTable = new Table();

            Label nameLabel = new Label(plane.description, skin);

            boolean canAfford = playerMoney >= plane.price;

            TextButton priceBtn = new TextButton("$" + plane.price, canAfford ? affordableStyle : expensiveStyle);
            priceBtn.setUserObject(plane.getId());

            if (!canAfford) {
                priceBtn.setDisabled(true);
            } else {
                buttonGroup.add(priceBtn);

                priceBtn.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        if (buttonGroup.getChecked() == null) {
                            submitBtn.setText("Choose the plane");
                        } else {
                            submitBtn.setText("Submit the purchase");
                        }
                    }
                });
            }

            singlePlaneTable.add(nameLabel).padBottom(20).row();
            singlePlaneTable.add(priceBtn).width(250).height(80);

            planesTable.add(singlePlaneTable).pad(30);
        }

        ScrollPane scrollPane = new ScrollPane(planesTable, skin);
        scrollPane.setScrollingDisabled(false, true);
        scrollPane.setFadeScrollBars(false);

        this.add(scrollPane).expandX().fillX().padTop(50).padBottom(50).row();

        Table bottomTable = new Table();

        submitBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                TextButton selected = buttonGroup.getChecked();
                if (selected == null) {
                    return;
                }

                int selectedPlaneId = (int) selected.getUserObject();

                llh.sendPlaneResponse(selectedPlaneId, false);

                buttonGroup.uncheckAll();
            }
        });

        TextButton exitBtn = new TextButton("Exit", skin, "red");
        exitBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                llh.sendPlanePass();
                remove();
                uiManager.showSuccessWindow("Plane purchase finished!");
            }
        });

        bottomTable.add(submitBtn).width(600).height(80).padBottom(20).row();
        bottomTable.add(exitBtn).width(300).height(80);

        this.add(bottomTable).padBottom(40);
    }
}
