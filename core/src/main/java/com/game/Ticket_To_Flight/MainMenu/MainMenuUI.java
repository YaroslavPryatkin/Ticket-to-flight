package com.game.Ticket_To_Flight.MainMenu;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class MainMenuUI {
    private final Table mainTable;
    private final MainMenuClient mainMenuClient;

    public MainMenuUI(Skin skin, MainMenuClient mainMenuClient) {
        this.mainMenuClient = mainMenuClient;
        this.mainTable = new Table();
        this.mainTable.setFillParent(true);
        this.mainTable.center();

        buildUI(skin);
    }

    private void buildUI(Skin skin) {
        Label titleLabel = new Label("Ticket to Flight", skin);
        titleLabel.setFontScale(2.5f);
        mainTable.add(titleLabel).padBottom(150).row();

        TextButton createServerBtn = new TextButton("Create server", skin);
        TextButton connectServerBtn = new TextButton("Connect to server", skin);

        createServerBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //System.out.println("Create server clicked");
                if (mainMenuClient.createMainLogic()) {
                    MainMenuMessageDialog dialog = new MainMenuMessageDialog("Server", "Server was created succesfully", skin);
                    dialog.show(mainTable.getStage());
                    createServerBtn.setText("Server is running");
                }
            }
        });

        connectServerBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //System.out.println("Connect to server clicked");
                mainMenuClient.createMainClient();
            }
        });

        mainTable.add(createServerBtn).width(500).height(150).padBottom(20).row();
        mainTable.add(connectServerBtn).width(500).height(150);
    }

    public Table getTable() {
        return mainTable;
    }
}
