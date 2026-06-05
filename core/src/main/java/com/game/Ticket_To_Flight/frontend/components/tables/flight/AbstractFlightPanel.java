package com.game.Ticket_To_Flight.frontend.components.tables.flight;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.game.Ticket_To_Flight.frontend.components.buttons.RoundedButton;
import com.game.Ticket_To_Flight.frontend.components.texts.SingleLineText;

public abstract class AbstractFlightPanel extends Table {
    protected final Skin skin;

    protected float screenWidth = 0;
    protected float screenHeight = 0;

    protected boolean isCollapsed = false;
    protected boolean isInitialized = false;
    protected Runnable onToggle;

    public AbstractFlightPanel(Skin skin) {
        this.skin = skin;
        top().right();
        pad(20);
        setBackground(skin.getDrawable("flight-panel-bg"));
    }

    public void setOnToggle(Runnable onToggle) {
        this.onToggle = onToggle;
    }

    protected Table buildHeader(String title) {
        Table header = new Table();
        header.add(new SingleLineText(title, skin)).expandX().left();

        TextButton toggleBtn = new RoundedButton(isCollapsed ? "Expand" : "Collapse", skin);
        toggleBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                isCollapsed = !isCollapsed;
                renderContent();
                if (onToggle != null) onToggle.run();
            }
        });

        header.add(toggleBtn).right().width(200).height(70);
        return header;
    }

    protected abstract void renderContent();
    protected abstract void recalculatePosition();
}
