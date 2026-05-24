package com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.GameUIManagerDirectory.GameStageWindows.SubWindows;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.game.Ticket_To_Flight.Utilities.MapHolder;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.Player;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PlaneType;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.frontend.LowLevelHandlerFront;
import com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.GameUIManager;
import com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.GameUIManagerDirectory.GameStageWindows.BaseGameWindow;

import java.util.Iterator;
import java.util.Map;

public class PlaneWindow extends BaseGameWindow {

    private final ButtonGroup<TextButton> buttonGroup;
    private final Runnable updateBottomUI;

    public PlaneWindow(Skin skin, final GameUIManager uiManager, final LowLevelHandlerFront llh, GameData gameData) {
        super("Plane Purchase", skin, 1400, 800);

        Player pl = gameData.players.get(llh.getMyId());
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

        Label titleLabel = new Label("Plane purchase", skin);
        titleLabel.setFontScale(1.2f);
        this.add(titleLabel).padBottom(10).row();

        final Label choosePlaneTopLabel = new Label("Choose the plane", skin);
        choosePlaneTopLabel.setColor(Color.ORANGE);
        final Cell<Label> choosePlaneCell = this.add(choosePlaneTopLabel).padBottom(20);
        choosePlaneCell.row();

        Table planesTable = new Table();
        Iterator<Map.Entry<PlaneType, Integer>> it = MapHolder.viewAsEntrySet(gameData.availablePlanes);

        while (it.hasNext()) {
            Map.Entry<PlaneType, Integer> e = it.next();
            if (e == null) continue;

            PlaneType plane = e.getKey();
            Integer num = e.getValue();
            int planeId = plane.getId();

            if (num <= 0) continue;

            Table singlePlaneTable = new Table();
            Label nameLabel = new Label(plane.description, skin);
            Label numLabel = new Label("Left: " + num, skin);

            boolean canAfford = playerMoney >= plane.price;

            TextButton priceBtn = new TextButton("$" + plane.price, canAfford ? affordableStyle : expensiveStyle);
            priceBtn.setUserObject(planeId);

            if (!canAfford) {
                priceBtn.setDisabled(true);
            } else {
                buttonGroup.add(priceBtn);
            }

            singlePlaneTable.add(nameLabel).padBottom(20).row();
            singlePlaneTable.add(numLabel).padBottom(20).row();
            singlePlaneTable.add(priceBtn).width(250).height(80);

            planesTable.add(singlePlaneTable).pad(30);
        }

        ScrollPane scrollPane = new ScrollPane(planesTable, skin);
        scrollPane.setScrollingDisabled(false, true);
        scrollPane.setFadeScrollBars(false);
        this.add(scrollPane).expandX().fillX().padBottom(40).row();

        final Table actionTable = new Table();
        final TextButton passBtn = new TextButton("Pass", skin, "red");
        final TextButton buyBtn = new TextButton("Buy the plane", skin, "default");
        final TextButton buyAndFinishBtn = new TextButton("Buy the plane and finish", skin, "red");

        updateBottomUI = new Runnable() {
            @Override
            public void run() {
                actionTable.clearChildren();

                if (buttonGroup.getChecked() == null) {
                    choosePlaneCell.setActor(choosePlaneTopLabel);
                    actionTable.add(passBtn).width(400).height(100);
                } else {
                    choosePlaneCell.setActor(null);
                    Table buyButtonsTable = new Table();
                    buyButtonsTable.add(buyBtn).width(500).height(80).padRight(30);
                    buyButtonsTable.add(buyAndFinishBtn).width(500).height(80);

                    actionTable.add(buyButtonsTable).padBottom(20).row();
                    actionTable.add(passBtn).width(400).height(80);
                }
                PlaneWindow.this.invalidateHierarchy();
            }
        };

        updateBottomUI.run();

        for (TextButton btn : buttonGroup.getButtons()) {
            btn.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    updateBottomUI.run();
                }
            });
        }

        buyBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (buttonGroup.getChecked() == null) return;
                int selectedPlaneId = (int) buttonGroup.getChecked().getUserObject();

                llh.sendPlaneResponse(selectedPlaneId, false);
                uiManager.showSuccessWindow("Plane successfully bought!");
            }
        });

        buyAndFinishBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (buttonGroup.getChecked() == null) return;
                int selectedPlaneId = (int) buttonGroup.getChecked().getUserObject();

                llh.sendPlaneResponse(selectedPlaneId, true);
                uiManager.showSuccessWindow("Plane bought and turn finished!");
            }
        });

        passBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                llh.sendPlanePass();
                uiManager.showSuccessWindow("Skipped plane purchase.");
            }
        });

        this.add(actionTable).padBottom(20);
    }
}
