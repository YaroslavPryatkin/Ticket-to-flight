package com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManagerDirectory.GameStageWindows.SubWindows;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.AbilityType;
import com.game.Ticket_To_Flight.commonFrontAndBack.GameData;
import com.game.Ticket_To_Flight.frontend.LowLevelHandlerFront;
import com.game.Ticket_To_Flight.frontend.UI.MainScreen.GameUIManager;
import com.game.Ticket_To_Flight.frontend.components.windows.BaseGameWindow;
import com.game.Ticket_To_Flight.frontend.components.buttons.RoundedButton;

public class AbilitiesWindow extends BaseGameWindow {

    private final ButtonGroup<TextButton> buttonGroup;

    public AbilitiesWindow(Skin skin, final GameUIManager uiManager, final LowLevelHandlerFront llh, GameData gameData) {
        super("Choose your ability for this round", skin, 1300, 850);

        TextButton.TextButtonStyle abilityBtnStyle = new TextButton.TextButtonStyle();
        abilityBtnStyle.font = skin.getFont("default-font");
        abilityBtnStyle.up = skin.getDrawable("dark-bg");
        abilityBtnStyle.checked = skin.getDrawable("blue-bg");
        abilityBtnStyle.fontColor = Color.WHITE;
        abilityBtnStyle.checkedFontColor = Color.CYAN;
        abilityBtnStyle.disabledFontColor = Color.GRAY;

        buttonGroup = new ButtonGroup<>();
        buttonGroup.setMaxCheckCount(1);
        buttonGroup.setMinCheckCount(0);

        Table innerTable = new Table();

        for (AbilityType ability : gameData.availableAbilities) {
            if (ability.getId() == 0)
                continue;
            String buttonText = ability.description;
            TextButton abilityBtn = new RoundedButton(buttonText, skin);
            abilityBtn.setStyle(abilityBtnStyle);

            abilityBtn.setUserObject(ability.getId());

            buttonGroup.add(abilityBtn);
            innerTable.add(abilityBtn).width(1100).height(80).padBottom(20).row();
        }

        ScrollPane scrollPane = new ScrollPane(innerTable, skin);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFadeScrollBars(false);
        registerScrollFocus(scrollPane);

        this.add(scrollPane).expandY().fillY().padTop(30).padBottom(40).row();

        TextButton submitBtn = new RoundedButton("Submit", skin, "default");
        submitBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                TextButton selected = buttonGroup.getChecked();
                if (selected == null) return;
                int selectedAbilityId = (int) selected.getUserObject();

                llh.sendAbilityResponse(selectedAbilityId);

                remove();
                uiManager.showSuccessWindow("Ability selected successfully!");
            }
        });

        this.add(submitBtn).width(300).height(80).padBottom(40);
    }
}
