package com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.GameUIManagerDirectory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.*;

public class StyleFactory {

    public Skin createBasicWindow() {
        Skin skin = new Skin();
        skin.add("default-font", new BitmapFont());

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.2f, 0.2f, 0.2f, 0.8f));
        pixmap.fill();
        skin.add("background", new Texture(pixmap));

        Pixmap btnPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        btnPixmap.setColor(new Color(0.4f, 0.4f, 0.4f, 1f));
        btnPixmap.fill();
        skin.add("btn-up", new Texture(btnPixmap));

        Pixmap btnDisabledPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        btnDisabledPixmap.setColor(new Color(0.1f, 0.1f, 0.1f, 0.9f));
        btnDisabledPixmap.fill();
        skin.add("btn-disabled", new Texture(btnDisabledPixmap));

        pixmap.dispose();
        btnPixmap.dispose();
        btnDisabledPixmap.dispose();

        Window.WindowStyle windowStyle = new Window.WindowStyle();
        windowStyle.titleFont = skin.getFont("default-font");
        windowStyle.background = skin.getDrawable("background");
        skin.add("default", windowStyle);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = skin.getFont("default-font");
        labelStyle.fontColor = Color.WHITE;
        skin.add("default", labelStyle);

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = skin.getFont("default-font");
        btnStyle.fontColor = Color.WHITE;
        btnStyle.up = skin.getDrawable("btn-up");
        btnStyle.disabled = skin.getDrawable("btn-disabled");
        skin.add("default", btnStyle);
        return skin;
    }

    public Skin createInvestWindow() {
        Skin skin = new Skin();
        skin.add("default-font", new BitmapFont());

        Pixmap bluePix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        bluePix.setColor(new Color(0.1f, 0.2f, 0.5f, 0.85f));
        bluePix.fill();
        skin.add("blue-bg", new Texture(bluePix));
        bluePix.dispose();

        Pixmap sliderKnob = new Pixmap(20, 20, Pixmap.Format.RGBA8888);
        sliderKnob.setColor(Color.CYAN);
        sliderKnob.fillCircle(10, 10, 10);
        skin.add("slider-knob", new Texture(sliderKnob));
        sliderKnob.dispose();

        Pixmap darkPix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        darkPix.setColor(new Color(0.1f, 0.1f, 0.1f, 0.9f));
        darkPix.fill();
        skin.add("dark-bg", new Texture(darkPix));
        darkPix.dispose();

        Pixmap sliderTrackPix = new Pixmap(100, 10, Pixmap.Format.RGBA8888);
        sliderTrackPix.setColor(new Color(0.2f, 0.2f, 0.2f, 1f));
        sliderTrackPix.fill();
        skin.add("slider-track", new Texture(sliderTrackPix));
        sliderTrackPix.dispose();

        Slider.SliderStyle sliderStyle = new Slider.SliderStyle();
        sliderStyle.background = skin.getDrawable("slider-track");
        sliderStyle.knob = skin.getDrawable("slider-knob");
        skin.add("default-horizontal", sliderStyle);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = skin.getFont("default-font");
        labelStyle.fontColor = Color.WHITE;
        skin.add("default", labelStyle);

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = skin.getFont("default-font");
        btnStyle.fontColor = Color.WHITE;
        btnStyle.up = skin.getDrawable("dark-bg"); // Будет темная кнопка
        skin.add("default", btnStyle);

        Pixmap redPix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        redPix.setColor(new Color(0.8f, 0.2f, 0.2f, 0.9f)); // Красный цвет
        redPix.fill();
        skin.add("red-bg", new Texture(redPix));
        redPix.dispose();

        TextButton.TextButtonStyle btnStyleRed = new TextButton.TextButtonStyle();
        btnStyleRed.font = skin.getFont("default-font");
        btnStyleRed.fontColor = Color.WHITE;
        btnStyleRed.up = skin.getDrawable("red-bg");

        skin.add("red", btnStyleRed);
        return skin;
    }
}
