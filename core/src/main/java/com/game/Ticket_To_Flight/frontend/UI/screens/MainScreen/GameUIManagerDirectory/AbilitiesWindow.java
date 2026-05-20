package com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.GameUIManagerDirectory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.game.Ticket_To_Flight.frontend.LowLevelHandlerFront;
import com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.GameUIManager;

public class AbilitiesWindow extends Table {

    private final ButtonGroup<TextButton> buttonGroup;

    public AbilitiesWindow(Skin skin, final GameUIManager uiManager, final LowLevelHandlerFront llh) {
        uiManager.setOverlayActive(true);

        this.setFillParent(true);
        this.setBackground(skin.getDrawable("blue-bg"));
        this.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.enabled);

        Label titleLabel = new Label("Choose your ability for this round", skin);
        titleLabel.setFontScale(1.4f);
        this.add(titleLabel).pad(20).row();

        TextButton.TextButtonStyle abilityBtnStyle = new TextButton.TextButtonStyle();
        abilityBtnStyle.font = skin.getFont("default-font");
        abilityBtnStyle.up = skin.getDrawable("dark-bg");
        abilityBtnStyle.fontColor = Color.WHITE;
        abilityBtnStyle.checkedFontColor = Color.CYAN;
        abilityBtnStyle.disabledFontColor = Color.GRAY;

        buttonGroup = new ButtonGroup<>();
        buttonGroup.setMaxCheckCount(1);
        buttonGroup.setMinCheckCount(0);

        String[] abilitiesText = {
            "1. Be first to buy planes",
            "2. Be first to rent airlines",
            "3. Be first to perform flights",
            "4. +2 action points",
            "5. Look what will happen at stage 1 in the next turn",
            "6. Increase the income from flights by 1.2",
            "7. Pass one time in the next turn auction without losing it"
        };

        Table innerTable = new Table();

        for (int i = 0; i < abilitiesText.length; i++) {
            TextButton abilityBtn = new TextButton(abilitiesText[i], abilityBtnStyle);
            buttonGroup.add(abilityBtn);
            innerTable.add(abilityBtn).width(550).height(45).padBottom(12).row();
        }

        ScrollPane scrollPane = new ScrollPane(innerTable, skin);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFadeScrollBars(false);
        this.add(scrollPane).expandY().fillY().padBottom(20).row();

        TextButton submitBtn = new TextButton("Submit", skin, "default");
        submitBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                TextButton selected = buttonGroup.getChecked();
                if (selected == null) return;

                int selectedIndex = buttonGroup.getButtons().indexOf(selected, true);

                System.out.println("Selected ability index: " + selectedIndex);

                remove();
                uiManager.setOverlayActive(false);
                uiManager.showSuccessWindow("Ability selected successfully!");
            }
        });

        this.add(submitBtn).width(160).height(50).padBottom(25);
    }
}
