package com.game.Ticket_To_Flight.frontend.components;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

import java.util.ArrayList;
import java.util.List;

public abstract class ScrollableBoxList<T extends BoxContainer> extends Group {
    private final List<T> items = new ArrayList<>();
    private final float itemGap;
    private final float scrollSpeed;
    private float scrollOffset = 0;

    public ScrollableBoxList(float x, float y, float width, float height, float itemGap, float scrollSpeed) {
        this.itemGap = itemGap;
        this.scrollSpeed = scrollSpeed;
        setPosition(x, y);
        setSize(width, height);
        addListener(new InputListener() {
            @Override
            public boolean scrolled(InputEvent event, float x, float y, float amountX, float amountY) {
                scrollBy(amountY * scrollSpeed);
                return true;
            }
        });
    }

    public void addItem(T item) {
        items.add(item);
        addActor(item);
        layoutItems();
    }

    public void clearItems() {
        items.clear();
        clearChildren();
        scrollOffset = 0;
    }

    public List<T> getItems() {
        return items;
    }

    protected abstract void configureItem(T item, int index);

    private void scrollBy(float delta) {
        scrollOffset = Math.max(0, scrollOffset + delta);
        layoutItems();
    }

    private void layoutItems() {
        float nextY = getHeight() - scrollOffset;
        for (int i = 0; i < items.size(); i++) {
            T item = items.get(i);
            configureItem(item, i);
            nextY -= item.getHeight();
            item.setPosition(0, nextY);
            nextY -= itemGap;
        }
    }
}
