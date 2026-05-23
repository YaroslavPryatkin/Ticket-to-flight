package com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.GameUIManagerDirectory.GameStageWindows;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.GameUIManager;

public class PlaneWindow extends Table {
    private final GameUIManager uiManager;

    public PlaneWindow(Skin skin, GameUIManager uiManager) {
        this.uiManager = uiManager;

        final Table overlayWindow = new Table();
        overlayWindow.setFillParent(true);
        overlayWindow.setBackground(skin.getDrawable("blue-bg"));
        overlayWindow.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.enabled);

        Label titleLabel = new Label("Purchase planes", skin);
        titleLabel.setFontScale(1.5f);
        Label subtitleLabel = new Label("Buy new planes", skin);

        Table planesTable = new Table();

        planesTable.add(new Label("Regional Jet", skin)).pad(20);
        planesTable.add(new Label("Business Plane", skin)).pad(20);
        planesTable.add(new Label("Usual Jet", skin)).pad(20);
        planesTable.row();

        planesTable.add(new Label("$500", skin)).padBottom(20);
        planesTable.add(new Label("$1200", skin)).padBottom(20);
        planesTable.add(new Label("$2500", skin)).padBottom(20);
        planesTable.row();

        TextButton buyRegBtn = new TextButton("BUY", skin);
        TextButton buyBusBtn = new TextButton("BUY", skin);
        TextButton buyUsuBtn = new TextButton("BUY", skin);

        buyRegBtn.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                System.out.println("Куплен Regional Jet");
                overlayWindow.remove();
                uiManager.showSuccessWindow("Regional Jet purchased successfully!");
            }
        });

        buyBusBtn.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                System.out.println("Куплен Business Plane");
                overlayWindow.remove();
                uiManager.showSuccessWindow("Business Plane purchased successfully!");
            }
        });

        buyUsuBtn.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                System.out.println("Куплен Usual Jet");
                overlayWindow.remove();
                uiManager.showSuccessWindow("Usual Jet purchased successfully!");
            }
        });

        planesTable.add(buyRegBtn).width(140).height(45);
        planesTable.add(buyBusBtn).width(140).height(45);
        planesTable.add(buyUsuBtn).width(140).height(45);

        TextButton closeBtn = new TextButton("Cancel", skin);
        closeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                overlayWindow.remove();
            }
        });

        overlayWindow.add(titleLabel).padBottom(10).row();
        overlayWindow.add(subtitleLabel).padBottom(50).row();
        overlayWindow.add(planesTable).padBottom(50).row(); // Вставляем всю таблицу с самолетами целиком
        overlayWindow.add(closeBtn).width(150).height(50);
    }
}
