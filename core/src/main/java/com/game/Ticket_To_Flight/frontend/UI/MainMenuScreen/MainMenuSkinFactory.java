package com.game.Ticket_To_Flight.frontend.UI.MainMenuScreen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.game.Ticket_To_Flight.frontend.skins.BaseSkinFactory;

public class MainMenuSkinFactory extends BaseSkinFactory {

    public static Skin createMenuSkin() {
        Skin menuSkin = new Skin();
        menuSkin.add("default-font", generateCustomFont(54));

        Color windowBgColor = new Color(0.15f, 0.20f, 0.28f, 0.95f);
        Color buttonNormalColor = new Color(0.16f, 0.50f, 0.73f, 1f);
        Color buttonDownColor = new Color(0.10f, 0.35f, 0.53f, 1f);

        menuSkin.add("window-bg", createRoundedPatch(windowBgColor, 16));
        menuSkin.add("btn-up", createRoundedPatch(buttonNormalColor, 12));
        menuSkin.add("btn-down", createRoundedPatch(buttonDownColor, 12));

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = menuSkin.getFont("default-font");
        labelStyle.fontColor = Color.WHITE;
        menuSkin.add("default", labelStyle);

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = menuSkin.getFont("default-font");
        btnStyle.fontColor = Color.WHITE;
        btnStyle.downFontColor = Color.LIGHT_GRAY;
        btnStyle.up = menuSkin.getDrawable("btn-up");
        btnStyle.down = menuSkin.getDrawable("btn-down");
        menuSkin.add("default", btnStyle);

        Window.WindowStyle windowStyle = new Window.WindowStyle();
        windowStyle.titleFont = menuSkin.getFont("default-font");
        windowStyle.titleFontColor = Color.WHITE;
        windowStyle.background = menuSkin.getDrawable("window-bg");
        menuSkin.add("default", windowStyle);

        return menuSkin;
    }
}
