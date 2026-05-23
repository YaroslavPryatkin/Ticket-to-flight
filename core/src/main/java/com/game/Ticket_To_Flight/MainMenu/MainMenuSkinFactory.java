package com.game.Ticket_To_Flight.MainMenu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

public class MainMenuSkinFactory {

    public static Skin createMenuSkin() {
        Skin menuSkin = new Skin();
        menuSkin.add("default-font", new BitmapFont());

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = menuSkin.getFont("default-font");
        labelStyle.fontColor = Color.WHITE;
        menuSkin.add("default", labelStyle);

        Pixmap btnUp = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        btnUp.setColor(new Color(0.15f, 0.15f, 0.15f, 1f));
        btnUp.fill();
        menuSkin.add("btn-up", new Texture(btnUp));
        btnUp.dispose();

        Pixmap btnDown = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        btnDown.setColor(new Color(0.25f, 0.25f, 0.25f, 1f));
        btnDown.fill();
        menuSkin.add("btn-down", new Texture(btnDown));
        btnDown.dispose();

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = menuSkin.getFont("default-font");
        btnStyle.fontColor = Color.WHITE;
        btnStyle.up = menuSkin.getDrawable("btn-up");
        btnStyle.down = menuSkin.getDrawable("btn-down");
        menuSkin.add("default", btnStyle);

        Pixmap winBg = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        winBg.setColor(new Color(0.2f, 0.2f, 0.2f, 0.95f));
        winBg.fill();
        menuSkin.add("window-bg", new Texture(winBg));
        winBg.dispose();

        Window.WindowStyle windowStyle = new Window.WindowStyle();
        windowStyle.titleFont = menuSkin.getFont("default-font");
        windowStyle.titleFontColor = com.badlogic.gdx.graphics.Color.WHITE;
        windowStyle.background = menuSkin.getDrawable("window-bg");
        menuSkin.add("default", windowStyle);

        return menuSkin;
    }
}
