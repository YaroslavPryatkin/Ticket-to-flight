package com.game.Ticket_To_Flight.frontend.components.windows;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.game.Ticket_To_Flight.frontend.components.texts.SingleLineText;

public abstract class BaseInvestWindow extends BaseGameWindow {
    private static final float WINDOW_WIDTH = 800f;
    private static final float WINDOW_HEIGHT = 600f;
    private static final float SLIDER_WIDTH = 500f;
    private static final float BOUND_LABEL_WIDTH = 80f;

    protected final Skin windowSkin;
    protected Slider slider;
    protected Label valueLabel;

    protected BaseInvestWindow(String title, Skin skin) {
        super(title, skin, WINDOW_WIDTH, WINDOW_HEIGHT);
        this.windowSkin = skin;
    }

    protected void buildSliderLayout(String subtitle, int leftBound, int rightBound, int initialValue) {
        add(new SingleLineText(subtitle, windowSkin)).padBottom(30).row();

        valueLabel = new SingleLineText(String.valueOf(initialValue), windowSkin);
        add(valueLabel).padBottom(20).row();

        slider = new Slider(leftBound, rightBound, 1, false, windowSkin);
        slider.setValue(initialValue);
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                valueLabel.setText(String.valueOf((int) slider.getValue()));
            }
        });

        Label leftLabel = new SingleLineText(String.valueOf(leftBound), windowSkin);
        Label rightLabel = new SingleLineText(String.valueOf(rightBound), windowSkin);

        Table sliderTable = new Table();
        sliderTable.add(leftLabel).width(BOUND_LABEL_WIDTH).right().padRight(20);
        sliderTable.add(slider).width(SLIDER_WIDTH);
        sliderTable.add(rightLabel).width(BOUND_LABEL_WIDTH).left().padLeft(20);
        add(sliderTable).padBottom(40).row();
    }

    protected int getSliderValue() {
        return slider == null ? 0 : (int) slider.getValue();
    }
}
