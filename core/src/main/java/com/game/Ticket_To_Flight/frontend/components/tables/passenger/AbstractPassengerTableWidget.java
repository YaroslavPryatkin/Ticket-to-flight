package com.game.Ticket_To_Flight.frontend.components.tables.passenger;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.game.Ticket_To_Flight.frontend.components.texts.SingleLineText;

import java.util.List;

public abstract class AbstractPassengerTableWidget extends Table {
    protected final Skin skin;
    private final boolean showChoiceColumn;
    private final boolean alignTextRight;

    public AbstractPassengerTableWidget(Skin skin, boolean showChoiceColumn) {
        this(skin, showChoiceColumn, false);
    }

    public AbstractPassengerTableWidget(Skin skin, boolean showChoiceColumn, boolean alignTextRight) {
        this.skin = skin;
        this.showChoiceColumn = showChoiceColumn;
        this.alignTextRight = alignTextRight;
        top().right();
    }

    public void setRows(List<PassengerTableWidget> rows) {
        clearChildren();
        addHeader();

        if (rows == null || rows.isEmpty()) {
            add(new SingleLineText("No passengers", skin)).colspan(showChoiceColumn ? 5 : 4).center().pad(30);
            return;
        }

        for (PassengerTableWidget row : rows) {
            addText(row.cityTo(), 30, 12, true);
            addText(row.persons(), 30, 12, false);
            addText(row.reward(), 30, 12, false);
            addText(row.passengerClass(), 30, 12, false);

            if (showChoiceColumn) {
                Actor choiceActor = createChoiceActor(row);
                add(choiceActor).padLeft(10).padBottom(12).center();
            }
            row();
        }
    }

    protected Actor createChoiceActor(PassengerTableWidget row) {
        return new SingleLineText("", skin);
    }

    private void addHeader() {
        addText("CityTo", 30, 20, true);
        addText("Persons", 30, 20, false);
        addText("Reward", 30, 20, false);
        addText("Class", 30, 20, false);
        if (showChoiceColumn) {
            add(new SingleLineText("Choice", skin)).padLeft(10).padBottom(20).center();
        }
        row();
    }

    private void addText(String text, int padRight, int padBottom, boolean left) {
        if (alignTextRight) {
            add(new SingleLineText(text, skin)).padRight(padRight).padBottom(padBottom).right();
        } else if (left) {
            add(new SingleLineText(text, skin)).padRight(padRight).padBottom(padBottom).left();
        } else {
            add(new SingleLineText(text, skin)).padRight(padRight).padBottom(padBottom).center();
        }
    }
}
