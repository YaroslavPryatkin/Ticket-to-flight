package com.game.Ticket_To_Flight.frontend.components.tables.expandable;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.game.Ticket_To_Flight.backend.gameLogicEntities.templates.PassengerType;
import com.game.Ticket_To_Flight.frontend.components.buttons.RoundedButton;
import com.game.Ticket_To_Flight.frontend.components.texts.SingleLineText;

public class ExpandableListWidget extends Table {
    public static final float TOGGLE_BUTTON_WIDTH = 40f;
    public static final float TOGGLE_BUTTON_HEIGHT = 35f;
    public static final float TOGGLE_BUTTON_RIGHT_PADDING = 15f;

    private final Table header;
    private final Table contentTable;
    private final TextButton toggleButton;
    private boolean isExpanded = false;
    private float preferredWidth = 0f;

    private Runnable onExpandCallback;
    private Runnable onToggleCallback;

    public ExpandableListWidget(String title, Skin skin) {
        header = new Table();

        toggleButton = new RoundedButton("+", skin);
        toggleButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (isExpanded) {
                    collapse();
                } else {
                    expand();
                }
            }
        });

        header.add(toggleButton).left()
            .width(TOGGLE_BUTTON_WIDTH)
            .height(TOGGLE_BUTTON_HEIGHT)
            .padRight(TOGGLE_BUTTON_RIGHT_PADDING);
        SingleLineText name = new SingleLineText(title, skin);
        header.add(name).expandX().left();

        contentTable = new Table();

        add(header).expandX().fillX().left().row();
    }

    public ExpandableListWidget(String title, Skin skin, PassengerType pass) {
        header = new Table();

        toggleButton = new RoundedButton("+", skin);
        toggleButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (isExpanded) {
                    collapse();
                } else {
                    expand();
                }
            }
        });

        header.add(toggleButton).left()
            .width(TOGGLE_BUTTON_WIDTH)
            .height(TOGGLE_BUTTON_HEIGHT)
            .padRight(TOGGLE_BUTTON_RIGHT_PADDING);
        SingleLineText name = new SingleLineText(title, skin);
        name.setColor(pass.typeTo.getColor());
        header.add(name).expandX().left();

        contentTable = new Table();

        add(header).expandX().fillX().left().row();
    }

    public Table getContentTable() {
        return contentTable;
    }

    public void addHeaderActor(Actor actor, float width, float height) {
        header.add(actor).right().width(width).height(height).padLeft(12);
        invalidateHierarchy();
    }

    public void setPreferredWidth(float preferredWidth) {
        this.preferredWidth = Math.max(0f, preferredWidth);
        invalidateHierarchy();
    }

    @Override
    public float getPrefWidth() {
        return Math.max(super.getPrefWidth(), preferredWidth);
    }

    public void setCallbacks(Runnable onExpand, Runnable onToggle) {
        this.onExpandCallback = onExpand;
        this.onToggleCallback = onToggle;
    }

    public void expand() {
        if (!isExpanded) {
            isExpanded = true;
            toggleButton.setText("-");

            clearChildren();
            add(header).expandX().fillX().left().row();
            add(contentTable).expandX().fillX().padTop(5).row();

            if (onExpandCallback != null) onExpandCallback.run();
            if (onToggleCallback != null) onToggleCallback.run();
        }
    }

    public void collapse() {
        if (isExpanded) {
            isExpanded = false;
            toggleButton.setText("+");

            clearChildren();
            add(header).expandX().fillX().left().row();

            if (onToggleCallback != null) onToggleCallback.run();
        }
    }

    public boolean isExpanded() {
        return isExpanded;
    }
}
