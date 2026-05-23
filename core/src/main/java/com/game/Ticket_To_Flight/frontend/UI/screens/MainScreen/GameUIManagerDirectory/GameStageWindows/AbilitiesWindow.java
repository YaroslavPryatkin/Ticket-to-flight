package com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.GameUIManagerDirectory.GameStageWindows;

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
import com.game.Ticket_To_Flight.commonFrontAndBack.StaticGameData; // <-- Убедись, что импорт правильный
import com.game.Ticket_To_Flight.frontend.LowLevelHandlerFront;
import com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.GameUIManager;
import com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.GameUIManagerDirectory.BaseGameWindow;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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

        List<AbilityType> sortedAbilities = new ArrayList<>();
        for (AbilityType ability : StaticGameData.abilityTypes) {
            sortedAbilities.add(ability);
        }

        sortedAbilities.sort(new Comparator<AbilityType>() {
            @Override
            public int compare(AbilityType a1, AbilityType a2) {
                return Integer.compare(a1.getId(), a2.getId());
            }
        });

        buttonGroup = new ButtonGroup<>();
        buttonGroup.setMaxCheckCount(1);
        buttonGroup.setMinCheckCount(0);

        Table innerTable = new Table();

        int i = 1;
        for (AbilityType ability : sortedAbilities) {
            String buttonText = i + ". " + ability.description;
            TextButton abilityBtn = new TextButton(buttonText, abilityBtnStyle);

            abilityBtn.setUserObject(ability.getId());

            buttonGroup.add(abilityBtn);
            innerTable.add(abilityBtn).width(1100).height(80).padBottom(20).row();
            i++;
        }

        ScrollPane scrollPane = new ScrollPane(innerTable, skin);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFadeScrollBars(false);

        this.add(scrollPane).expandY().fillY().padTop(30).padBottom(40).row();

        TextButton submitBtn = new TextButton("Submit", skin, "default");
        submitBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                TextButton selected = buttonGroup.getChecked();
                if (selected == null) return;
                int selectedAbilityId = (int) selected.getUserObject();

                System.out.println("Selected ability ID: " + selectedAbilityId);

                llh.sendAbilityResponse(selectedAbilityId);

                remove();
                uiManager.showSuccessWindow("Ability selected successfully!");
            }
        });

        this.add(submitBtn).width(300).height(80).padBottom(40);
    }
}
