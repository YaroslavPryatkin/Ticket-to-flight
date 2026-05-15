package com.game.Ticket_To_Flight.Utilities;

import java.util.Collections;
import java.util.Set;

public class TemporarySetHolder<T extends Identifiable> extends SetHolder<T>{
    SetHolder<T> cur;
    SetHolder<T> toAdd;
    Set<Integer> toRemove;

    private TemporarySetHolder(SetHolder<T> cur, Set<T> toAdd, Set<Integer> toRemove) {
        this.cur = cur;
        this.toAdd = new SetHolder<>(toAdd);
        this.toRemove = toRemove == null ? Collections.emptySet() : toRemove;
    }

    public static <T extends Identifiable> SetHolder<T> generateTemporarySetHolder(
        SetHolder<T> cur, Set<T> toAdd, Set<Integer> toRemove) {
        if ((toAdd == null || toAdd.isEmpty()) && (toRemove == null || toRemove.isEmpty())) {
            return cur;
        }
        return new TemporarySetHolder<>(cur, toAdd, toRemove);
    }

    @Override
    public T get(Integer id) {
        T inCur = cur.get(id);
        T inAdd = toAdd.get(id);
        boolean inRem = toRemove.contains(id);
        if(inCur!=null && inAdd!=null){
            if(inRem) return inAdd;
            else return inCur;
        }
        if(!inRem){
            if(inCur!=null) return inCur;
            else if(inAdd!=null) return inAdd;
        }
        return null;
    }
    @Override
    public T get(T item) {
        return get(item.getId());
    }

    private boolean containCheck(Integer id){
        boolean inCur = cur.contains(id);
        boolean inAdd = toAdd.contains(id);
        boolean inRem = toRemove.contains(id);
        return !inRem || (inCur && inAdd);
    }

    @Override
    public boolean contains(Object o) {
        if(o instanceof Integer)
            return containCheck((Integer) o);
        if(o instanceof Identifiable)
            return containCheck(((Identifiable) o).getId());
        return false;
    }
}
