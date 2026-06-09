package com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.HUD.HUDDirectory;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.game.Ticket_To_Flight.Utilities.MapHolder;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PlaneType;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManager;
import com.game.Ticket_To_Flight.frontend.components.texts.SingleLineText;
import com.game.Ticket_To_Flight.frontend.components.windows.BaseGameWindow;

import java.util.Iterator;
import java.util.Map;

public class AllPlanesListPanel extends BaseGameWindow {
    private final GameData gameData;
    private final GameUIManager gameUIManager;
    private final Skin skin;
    private final Table planesTable;

    public AllPlanesListPanel(Skin skin, GameData gameData, final GameUIManager gameUIManager) {
        super("Planes on the market for this round.", skin);
        this.gameData = gameData;
        this.gameUIManager = gameUIManager;
        this.skin = skin;

        this.planesTable = new Table();

        ScrollPane scrollPane = new ScrollPane(planesTable, skin);
        scrollPane.setScrollingDisabled(false, true);
        scrollPane.setFadeScrollBars(false);
        registerScrollFocus(scrollPane);

        this.add(scrollPane).expandX().fillX().padBottom(10);

        this.setTopPadding(20f);

        updateData();
    }

    public void updateData() {
        planesTable.clearChildren();

        Iterator<Map.Entry<PlaneType, Integer>> it = MapHolder.viewAsEntrySet(gameData.availablePlanes);
        boolean hasPlanes = false;
        while (it.hasNext()) {
            hasPlanes = true;
            Map.Entry<PlaneType, Integer> e = it.next();
            if (e == null) continue;

            final PlaneType plane = e.getKey();
            Integer amount = e.getValue();

            if (amount <= 0) continue;

            TextButton nameButton = new TextButton(plane.description + " (x" + amount + ")", skin);

            TextButton.TextButtonStyle infoStyle = new TextButton.TextButtonStyle(nameButton.getStyle());
            infoStyle.down = infoStyle.up;
            infoStyle.over = infoStyle.up;
            infoStyle.checked = infoStyle.up;
            infoStyle.focused = infoStyle.up;
            infoStyle.downFontColor = infoStyle.fontColor;
            infoStyle.checkedFontColor = infoStyle.fontColor;
            infoStyle.overFontColor = infoStyle.fontColor;
            nameButton.setStyle(infoStyle);

            nameButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    gameUIManager.handlePlaneClick(plane);
                }
            });

            planesTable.add(nameButton).pad(10);
        }
        if (!hasPlanes) {
            planesTable.add(new SingleLineText("No planes yet", skin)).left().padTop(20).row();
        }

        this.pack();

        this.setWidth(1300);

        applyPosition();
    }
}
