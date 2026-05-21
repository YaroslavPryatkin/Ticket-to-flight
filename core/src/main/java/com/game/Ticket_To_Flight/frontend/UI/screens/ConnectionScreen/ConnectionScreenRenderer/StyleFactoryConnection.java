package com.game.Ticket_To_Flight.frontend.UI.screens.ConnectionScreen.ConnectionScreenRenderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

public class StyleFactoryConnection extends Skin {
    public Skin createConnectionSkin() {
        Skin s = new Skin();
        s.add("default-font", new BitmapFont());

        Pixmap darkPix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        darkPix.setColor(new Color(0.2f, 0.2f, 0.2f, 1f));
        darkPix.fill();
        s.add("bg-dark", new Texture(darkPix));

        Pixmap cursorPix = new Pixmap(2, 20, Pixmap.Format.RGBA8888);
        cursorPix.setColor(Color.WHITE);
        cursorPix.fill();
        s.add("cursor", new Texture(cursorPix));

        Pixmap selectionPix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        selectionPix.setColor(new Color(0.3f, 0.5f, 0.8f, 0.8f));
        selectionPix.fill();
        s.add("selection", new Texture(selectionPix));

        Pixmap loadPix = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
        loadPix.setColor(Color.WHITE);
        loadPix.fillCircle(16, 16, 14);
        loadPix.setColor(Color.BLACK);
        loadPix.fillCircle(16, 16, 10);
        loadPix.fillRectangle(16, 0, 16, 16);
        s.add("loading-icon", new Texture(loadPix));

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = s.getFont("default-font");
        labelStyle.fontColor = Color.WHITE;
        s.add("default", labelStyle);

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = s.getFont("default-font");
        btnStyle.fontColor = Color.WHITE;
        btnStyle.up = s.getDrawable("bg-dark");
        s.add("default", btnStyle);

        TextField.TextFieldStyle tfStyle = new TextField.TextFieldStyle();
        tfStyle.font = s.getFont("default-font");
        tfStyle.fontColor = Color.WHITE;
        tfStyle.background = s.getDrawable("bg-dark");
        tfStyle.cursor = s.getDrawable("cursor");
        tfStyle.selection = s.getDrawable("selection");
        s.add("default", tfStyle);

        darkPix.dispose();
        cursorPix.dispose();
        selectionPix.dispose();
        loadPix.dispose();

        return s;
    }
}
