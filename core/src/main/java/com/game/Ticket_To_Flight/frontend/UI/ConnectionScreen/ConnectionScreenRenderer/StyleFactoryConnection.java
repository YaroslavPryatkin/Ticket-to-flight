package com.game.Ticket_To_Flight.frontend.UI.ConnectionScreen.ConnectionScreenRenderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.game.Ticket_To_Flight.frontend.skins.BaseSkinFactory;

public class StyleFactoryConnection extends BaseSkinFactory {
    public static Skin createConnectionSkin() {
        Skin skin = new Skin();
        skin.add("default-font", generateCustomFont(42));

        Color windowBgColor = new Color(0.18f, 0.18f, 0.20f, 0.95f);
        skin.add("background", createRoundedPatch(windowBgColor, 16));

        Window.WindowStyle windowStyle = new Window.WindowStyle();
        windowStyle.titleFont = skin.getFont("default-font");
        windowStyle.titleFontColor = com.badlogic.gdx.graphics.Color.WHITE;

        windowStyle.background = skin.getDrawable("background");

        skin.add("default", windowStyle);

        Color bgDarkColor = new Color(0.15f, 0.20f, 0.28f, 0.95f);
        Color selectionColor = new Color(0.3f, 0.5f, 0.8f, 0.8f);
        Color buttonNormalColor = new Color(0.16f, 0.50f, 0.73f, 1f);
        Color buttonDownColor = new Color(0.10f, 0.35f, 0.53f, 1f);

        skin.add("bg-dark", createRoundedPatch(bgDarkColor, 12));
        skin.add("btn-up", createRoundedPatch(buttonNormalColor, 12));
        skin.add("btn-down", createRoundedPatch(buttonDownColor, 12));

        skin.add("cursor", createRectangleTexture(Color.WHITE, 3, 40));
        skin.add("selection", createRectangleTexture(selectionColor, 1, 1));

        Pixmap loadPix = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
        loadPix.setColor(Color.WHITE);
        loadPix.fillCircle(16, 16, 14);
        loadPix.setColor(Color.BLACK);
        loadPix.fillCircle(16, 16, 10);
        loadPix.fillRectangle(16, 0, 16, 16);
        skin.add("loading-icon", new Texture(loadPix));
        loadPix.dispose();

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = skin.getFont("default-font");
        labelStyle.fontColor = Color.WHITE;
        skin.add("default", labelStyle);

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = skin.getFont("default-font");
        btnStyle.fontColor = Color.WHITE;
        btnStyle.downFontColor = Color.LIGHT_GRAY;
        btnStyle.up = skin.getDrawable("btn-up");
        btnStyle.down = skin.getDrawable("btn-down");
        skin.add("default", btnStyle);

        TextField.TextFieldStyle tfStyle = new TextField.TextFieldStyle();
        tfStyle.font = skin.getFont("default-font");
        tfStyle.fontColor = Color.WHITE;
        tfStyle.background = skin.getDrawable("bg-dark");
        tfStyle.cursor = skin.getDrawable("cursor");
        tfStyle.selection = skin.getDrawable("selection");
        skin.add("default", tfStyle);

        return skin;
    }
}
