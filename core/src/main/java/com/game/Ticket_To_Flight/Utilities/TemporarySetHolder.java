package com.game.Ticket_To_Flight.Utilities;

import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

public class TemporarySetHolder<C extends Identifiable, T extends Identifiable> extends SetHolder<T>{
    private final SetHolder<T> cur;
    private final SetHolder<C> toAdd;
    private final Set<Integer> toRemove;
    private final Function<C,T> restoreDTO;

    private TemporarySetHolder(SetHolder<T> cur, Set<C> toAdd, Set<Integer> toRemove, Function<C,T> restoreDTO) {
        this.cur = cur;
        this.toAdd = new SetHolder<>(toAdd);
        this.toRemove = toRemove == null ? Collections.emptySet() : toRemove;
        this.restoreDTO = restoreDTO;
    }

    public static <C extends Identifiable, T extends Identifiable> SetHolder<T> generateTemporarySetHolder(
        SetHolder<T> cur, Set<C> toAdd, Set<Integer> toRemove, Function<C,T> restoreDTO) {
        if ((toAdd == null || toAdd.isEmpty()) && (toRemove == null || toRemove.isEmpty())) {
            return cur;
        }
        return new TemporarySetHolder<>(cur, toAdd, toRemove, restoreDTO);
    }

    @Override
    public T get(Integer id) {
        T inCur = cur.get(id);
        C inAdd = toAdd.get(id);
        boolean inRem = toRemove.contains(id);
        if(inCur!=null && inAdd!=null){
            if(inRem) return restoreDTO.apply(inAdd);
            else return inCur;
        }
        if(!inRem){
            if(inCur!=null) return inCur;
            else if(inAdd!=null) return restoreDTO.apply(inAdd);
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
