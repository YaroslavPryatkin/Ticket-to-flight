package com.game.Ticket_To_Flight.frontend.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Align;

public abstract class BaseGameWindow extends Window {
    private ScrollPane scrollFocusPane;

    public BaseGameWindow(String title, Skin skin, float width, float height) {
        super(title, skin);
        applyDefaultStyles();
        this.setSize(width, height);
    }

    public BaseGameWindow(String title, Skin skin) {
        super(title, skin);
        applyDefaultStyles();
    }

    private void applyDefaultStyles() {
        this.getColor().a = 0.8f;
        this.setMovable(false);
        this.getTitleLabel().setAlignment(Align.center);

        this.padTop(90);

        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public boolean scrolled(InputEvent event, float x, float y, float amountX, float amountY) {
                return true;
            }
        });
    }

    protected void registerScrollFocus(final ScrollPane scrollPane) {
        this.scrollFocusPane = scrollPane;

        addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (event.getStage() != null) {
                    event.getStage().setScrollFocus(scrollPane);
                }
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                Stage stage = event.getStage();
                boolean movedInsideWindow = toActor != null && isAscendantOf(toActor);
                if (stage != null && !movedInsideWindow && stage.getScrollFocus() == scrollPane) {
                    stage.setScrollFocus(null);
                }
            }
        });

        updateScrollFocusUnderMouse();
    }

    public void updateScrollFocusUnderMouse() {
        Stage stage = getStage();
        if (stage == null || scrollFocusPane == null) return;

        Vector2 stageCoords = stage.screenToStageCoordinates(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
        Actor hitActor = stage.hit(stageCoords.x, stageCoords.y, true);
        if (hitActor == this || (hitActor != null && isAscendantOf(hitActor))) {
            stage.setScrollFocus(scrollFocusPane);
        }
    }
}
