package com.game.Ticket_To_Flight.frontend.UI.screens.MainScreen.GameUIManagerDirectory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.*;

public class StyleFactory {

    public Skin createBasicWindow() {
        Skin skin = new Skin();

        skin.add("default-font", generateCustomFont(42));

        Color windowBgColor = new Color(0.18f, 0.18f, 0.20f, 0.95f);
        Color buttonNormalColor = new Color(0.35f, 0.35f, 0.38f, 1f);
        Color buttonDownColor = new Color(0.25f, 0.25f, 0.28f, 1f);
        Color disabledColor = new Color(0.15f, 0.15f, 0.18f, 0.8f);

        skin.add("background", createRoundedPatch(windowBgColor, 16));
        skin.add("btn-up", createRoundedPatch(buttonNormalColor, 12));
        skin.add("btn-down", createRoundedPatch(buttonDownColor, 12));
        skin.add("btn-disabled", createRoundedPatch(disabledColor, 12));

        Window.WindowStyle windowStyle = new Window.WindowStyle();
        windowStyle.titleFont = skin.getFont("default-font");
        windowStyle.titleFontColor = Color.WHITE;
        windowStyle.background = skin.getDrawable("background");
        skin.add("default", windowStyle);

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
        btnStyle.checked = skin.getDrawable("btn-down");
        btnStyle.disabled = skin.getDrawable("btn-disabled");
        skin.add("default", btnStyle);

        return skin;
    }

    private BitmapFont generateCustomFont(int size) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("ui/Rubik-Medium.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        parameter.color = Color.WHITE;
        parameter.minFilter = Texture.TextureFilter.Linear;
        parameter.magFilter = Texture.TextureFilter.Linear;
        parameter.shadowColor = new Color(0, 0, 0, 0.5f);
        parameter.shadowOffsetX = 2;
        parameter.shadowOffsetY = 2;

        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();
        return font;
    }

    private NinePatch createRoundedPatch(Color color, int radius) {
        int size = radius * 2 + 2;
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        pixmap.setBlending(Pixmap.Blending.None);
        pixmap.setColor(color);

        pixmap.fillCircle(radius, radius, radius);
        pixmap.fillCircle(size - radius - 1, radius, radius);
        pixmap.fillCircle(radius, size - radius - 1, radius);
        pixmap.fillCircle(size - radius - 1, size - radius - 1, radius);

        pixmap.fillRectangle(radius, 0, size - 2 * radius, size);
        pixmap.fillRectangle(0, radius, size, size - 2 * radius);

        Texture texture = new Texture(pixmap);
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        pixmap.dispose();

        return new NinePatch(texture, radius, radius, radius, radius);
    }

    private Texture createCircleTexture(Color color, int radius) {
        int size = radius * 2;
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        pixmap.setBlending(Pixmap.Blending.None);
        pixmap.setColor(color);
        pixmap.fillCircle(radius, radius, radius);

        Texture texture = new Texture(pixmap);
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        pixmap.dispose();

        return texture;
    }

    public Skin createInvestWindow() {
        Skin skin = new Skin();

        skin.add("default-font", generateCustomFont(42));

        Color windowBgColor = new Color(0.15f, 0.20f, 0.28f, 0.95f);
        Color buttonNormalColor = new Color(0.16f, 0.50f, 0.73f, 1f);
        Color buttonCheckedColor = new Color(0.10f, 0.35f, 0.53f, 1f);
        Color buttonRedColor = new Color(0.90f, 0.30f, 0.26f, 1f);
        Color disabledColor = new Color(0.3f, 0.3f, 0.3f, 0.8f);

        skin.add("blue-bg", createRoundedPatch(windowBgColor, 16));
        skin.add("dark-bg", createRoundedPatch(buttonNormalColor, 12));
        skin.add("blue-bg-checked", createRoundedPatch(buttonCheckedColor, 12));
        skin.add("red-bg", createRoundedPatch(buttonRedColor, 12));
        skin.add("btn-disabled", createRoundedPatch(disabledColor, 12));

        skin.add("slider-track", createRoundedPatch(new Color(0.1f, 0.1f, 0.1f, 1f), 6));
        skin.add("slider-knob", createCircleTexture(Color.CYAN, 12));

        skin.add("scroll-track", createRoundedPatch(new Color(0.05f, 0.05f, 0.05f, 0.5f), 5));
        skin.add("scroll-knob", createRoundedPatch(Color.LIGHT_GRAY, 5));

        Window.WindowStyle windowStyle = new Window.WindowStyle();
        windowStyle.titleFont = skin.getFont("default-font");
        windowStyle.titleFontColor = Color.WHITE;
        windowStyle.background = skin.getDrawable("blue-bg");
        skin.add("default", windowStyle);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = skin.getFont("default-font");
        labelStyle.fontColor = Color.WHITE;
        skin.add("default", labelStyle);

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = skin.getFont("default-font");
        btnStyle.fontColor = Color.WHITE;
        btnStyle.downFontColor = Color.LIGHT_GRAY;
        btnStyle.up = skin.getDrawable("dark-bg");
        btnStyle.down = skin.getDrawable("blue-bg-checked");
        btnStyle.checked = skin.getDrawable("blue-bg-checked");
        btnStyle.disabled = skin.getDrawable("btn-disabled");
        skin.add("default", btnStyle);

        TextButton.TextButtonStyle btnStyleRed = new TextButton.TextButtonStyle();
        btnStyleRed.font = skin.getFont("default-font");
        btnStyleRed.fontColor = Color.WHITE;
        btnStyleRed.downFontColor = Color.LIGHT_GRAY;
        btnStyleRed.up = skin.getDrawable("red-bg");
        btnStyleRed.down = skin.getDrawable("blue-bg-checked");
        btnStyleRed.disabled = skin.getDrawable("btn-disabled");
        skin.add("red", btnStyleRed);

        Slider.SliderStyle sliderStyle = new Slider.SliderStyle();
        sliderStyle.background = skin.getDrawable("slider-track");
        sliderStyle.knob = skin.getDrawable("slider-knob");
        skin.add("default-horizontal", sliderStyle);

        ScrollPane.ScrollPaneStyle scrollStyle = new ScrollPane.ScrollPaneStyle();
        scrollStyle.vScrollKnob = skin.getDrawable("scroll-knob");
        scrollStyle.vScroll = skin.getDrawable("scroll-track");
        scrollStyle.hScrollKnob = skin.getDrawable("scroll-knob");
        scrollStyle.hScroll = skin.getDrawable("scroll-track");
        skin.add("default", scrollStyle);

        return skin;
    }
}
