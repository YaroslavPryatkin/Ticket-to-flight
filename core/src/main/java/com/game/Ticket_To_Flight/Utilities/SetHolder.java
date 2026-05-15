package com.game.Ticket_To_Flight.Utilities;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.*;
import java.util.function.*;


/**
 * A Set implementation that uses an internal Map for O(1) access by ID.
 */
public class SetHolder<T extends Identifiable> implements Set<T> {

    private final Int2ObjectOpenHashMap<T> storage = new Int2ObjectOpenHashMap<>();

    /**
     * Default constructor.
     */
    public SetHolder() {}

    /**
     * Copy constructor.
     * Creates a new Holder containing all elements from the provided collection.
     */
    public SetHolder(Collection<? extends T> collection) {
        if (collection != null) {
            this.addAll(collection);
        }
    }

    // --- Specialized Lookup Methods ---

    public T get(Integer id) {
        return storage.get(id);
    }
    public T remove(Integer id) {return storage.remove(id);}

    public T get(T item) {
        return (item == null) ? null : storage.get(item.getId());
    }

    /**
     * Checks if the storage contains the exact same instance (by reference)
     * as the provided object.
     */
    public boolean containsExact(Object o){
        if(!(o instanceof Identifiable)) return false;
        Identifiable i = (Identifiable) o;
        return storage.containsKey(i.getId()) && storage.get(i.getId()) == i;
    }

    public boolean addOnlyNew(T t){
        if(t==null ) return false;
        if(storage.containsKey(t.getId())) return false;
        storage.put(t.getId(), t);
        return true;
    }

    /**
     * Returns an unmodifiable internal map
     */
    public Map<Integer, T> getMap(){
        return Collections.unmodifiableMap(storage);
    }

    // --- Struct Modifier Methods ---

    /**
     * Applies a function to all pairs (this[key].field, other[key]) for every same key in this and other
     * @param getField getter
     * @param other parameter
     * @param function function to apply
     * @return this
     */
    public <V> SetHolder<T> changeAsStructInteger(
        Function<T, V> getField,
        Map<Integer, V> other,
        BiConsumer<V, V> function
    ) {
        if(getField == null || other == null || function == null) return this;
        for (Map.Entry<Integer, V> entry : other.entrySet()) {
            T realItem = this.get(entry.getKey());
            if (realItem != null) {
                V currentValue = getField.apply(realItem);

                // Mutate the inner structure of the field
                function.accept(currentValue, entry.getValue());
            }
        }
        return this;
    }

    /**
     * Applies a function to all pairs (this[key].field, other[key]) for every same key in this and other
     * @param getField getter
     * @param other parameter
     * @param function function to apply
     * @return this
     */
    public <V> SetHolder<T> changeAsStructWithSetterInteger(
        BiConsumer<T,V> setField,
        Function<T, V> getField,
        Map<Integer, V> other,
        BiFunction<V, V, V> function
    ) {
        if(setField == null || getField == null || other == null || function == null) return this;
        for (Map.Entry<Integer, V> entry : other.entrySet()) {
            T realItem = this.get(entry.getKey());
            if (realItem != null) {
                V currentValue = getField.apply(realItem);

                // Calculate new value and assign it using the setter
                V newVal = function.apply(currentValue, entry.getValue());
                setField.accept(realItem, newVal);
            }
        }
        return this;
    }

    /**
     * Validates the proposed structural changes.
     * Returns false if any affected object is missing from storage,
     * or if the checkFunction fails for any object.
     * Note: no checking is done to ensure this[key].getField() != null
     */
    public <V> boolean checkChangeAsStructInteger(
        Function<T, V> getField,
        Map<Integer, V> other,
        BiPredicate<V, V> checkFunction
    ) {
        if(other == null) return true;
        if(checkFunction == null || getField == null) return false;
        for (Map.Entry<Integer, V> entry : other.entrySet()) {
            T realItem = this.get(entry.getKey());
            // 1. All objects affected by params must exist in the current storage
            if (realItem == null) {
                return false;
            }

            V currentValue = getField.apply(realItem);
            // 2. Validate the specific logic via the provided predicate
            if (!checkFunction.test(currentValue, entry.getValue())) {
                return false;
            }
        }

        return true;
    }


    /**
     * Applies a modifying function to a specific field of the objects.
     * Useful for mutable field objects (like internal collections or wrappers).
     * @return this
     */
    public <V> SetHolder<T> changeAsStructType(
        Function<T, V> getField,
        List<Map<T, V>> params,
        BiConsumer<V, List<V>> function
    ) {
        Set<T> allKeys = collectAllKeysType(params);

        for (T key : allKeys) {
            // Get the actual stored instance using the ID of the key
            T realItem = this.get(key);
            if (realItem != null) {
                V currentValue = getField.apply(realItem);
                List<V> paramValues = collectParamValuesType(key, params);

                // Mutate the inner structure of the field
                function.accept(currentValue, paramValues);
            }
        }
        return this;
    }

    /**
     * Applies a modifying function to a specific field of the objects.
     * Useful for mutable field objects (like internal collections or wrappers).
     * @return this
     */
    public <C, V> SetHolder<T> changeAsStructInteger(
        Function<T, C> getField,
        List<Map<Integer, V>> params,
        BiConsumer<C, List<V>> function
    ) {
        Set<Integer> allKeys = collectAllKeysInteger(params);

        for (Integer key : allKeys) {
            // Get the actual stored instance using the ID of the key
            T realItem = this.get(key);
            if (realItem != null) {
                C currentValue = getField.apply(realItem);
                List<V> paramValues = collectParamValuesInteger(key, params);

                // Mutate the inner structure of the field
                function.accept(currentValue, paramValues);
            }
        }
        return this;
    }



    /**
     * Applies a function to calculate a new value for a field,
     * then uses the setter to assign it back to the object.
     * Useful for immutable fields (like Integer, String, Enum).
     * @return this
     */
    public <V> SetHolder<T> changeAsStructWithSetterType(
        BiConsumer<T, V> setter, // Takes the target object and the new value
        Function<T, V> getField,
        List<Map<T, V>> params,
        BiFunction<V, List<V>, V> function
    ) {
        Set<T> allKeys = collectAllKeysType(params);

        for (T key : allKeys) {
            T realItem = this.get(key);
            if (realItem != null) {
                V currentValue = getField.apply(realItem);
                List<V> paramValues = collectParamValuesType(key, params);

                // Calculate new value and assign it using the setter
                V newValue = function.apply(currentValue, paramValues);
                setter.accept(realItem, newValue);
            }
        }
        return this;
    }

    /**
     * Validates the proposed structural changes.
     * Returns false if any affected object is missing from storage,
     * or if the checkFunction fails for any object.
     */
    public <V> boolean checkChangeAsStructType(
        Function<T, V> getField,
        List<Map<T, V>> params,
        BiPredicate<V, List<V>> checkFunction
    ) {
        Set<T> allKeys = collectAllKeysType(params);

        for (T key : allKeys) {
            T realItem = this.get(key);
            // 1. All objects affected by params must exist in the current storage
            if (realItem == null) {
                return false;
            }

            V currentValue = getField.apply(realItem);
            List<V> paramValues = collectParamValuesType(key, params);

            // 2. Validate the specific logic via the provided predicate
            if (!checkFunction.test(currentValue, paramValues)) {
                return false;
            }
        }

        return true;
    }


    /**
     * Validates the proposed structural changes.
     * Returns false if any affected object is missing from storage,
     * or if the checkFunction fails for any object.
     * Note: does not check that this[key].getField() != null
     */
    public <C, V> boolean checkChangeAsStructInteger(
        Function<T, C> getField,
        List<Map<Integer, V>> params,
        BiPredicate<C, List<V>> checkFunction
    ) {
        Set<Integer> allKeys = collectAllKeysInteger(params);

        for (Integer key : allKeys) {
            T realItem = this.get(key);
            // 1. All objects affected by params must exist in the current storage
            if (realItem == null) {
                return false;
            }

            C currentValue = getField.apply(realItem);
            List<V> paramValues = collectParamValuesInteger(key, params);

            // 2. Validate the specific logic via the provided predicate
            if (!checkFunction.test(currentValue, paramValues)) {
                return false;
            }
        }

        return true;
    }

    // --- Private Helpers for Struct Methods ---

    /**
     * Gathers all unique keys present across all parameter maps.
     */
    private <V> Set<T> collectAllKeysType(List<Map<T, V>> params) {
        Set<T> allKeys = new HashSet<>();
        if (params != null) {
            for (Map<T, V> paramMap : params) {
                if (paramMap != null) {
                    allKeys.addAll(paramMap.keySet());
                }
            }
        }
        return allKeys;
    }

    private <V> Set<Integer> collectAllKeysInteger(List<Map<Integer, V>> params) {
        Set<Integer> allKeys = new HashSet<>();
        if (params != null) {
            for (Map<Integer, V> paramMap : params) {
                if (paramMap != null) {
                    allKeys.addAll(paramMap.keySet());
                }
            }
        }
        return allKeys;
    }

    /**
     * Extracts values for a specific key from all parameter maps.
     * Maintains the same index order as the params list.
     */
    private <V> List<V> collectParamValuesType(T key, List<Map<T, V>> params) {
        List<V> paramValues = new ArrayList<>();
        if (params != null) {
            for (Map<T, V> paramMap : params) {
                paramValues.add(paramMap != null ? paramMap.get(key) : null);
            }
        }
        return paramValues;
    }

    private <V> List<V> collectParamValuesInteger(Integer key, List<Map<Integer, V>> params) {
        List<V> paramValues = new ArrayList<>();
        if (params != null) {
            for (Map<Integer, V> paramMap : params) {
                paramValues.add(paramMap != null ? paramMap.get(key) : null);
            }
        }
        return paramValues;
    }

    // --- Change Elements Methods ---

    /**
     * For all elements that exist in both this and other replaces the element in this with replaceFunction(old, otherElem)
     * @param other - source
     * @param replaceFunction - function to replace an existing element
     * @return this
     */
    public SetHolder<T> changeElements(Set<T> other, BiFunction<T, T, T> replaceFunction){
        if (other == null) return this;
        for (T otherItem : other) {
            if (otherItem == null) continue;
            int id = otherItem.getId();
            T thisItem = this.get(id);
            if (thisItem != null) {
                T newItem = replaceFunction.apply(thisItem, otherItem);
                if (newItem == null) {
                    storage.remove(id);
                } else {
                    this.add(newItem);
                }
            }
        }
        return this;
    }

    /**
     * For every element in the union of extraList a List params is created
     * For all elements that exists in both this and the other replaces the element in this with replaceFunction(old, params)
     * @param other - source
     * @param replaceFunction - function to replace an existing element
     * @return this
     */
    public SetHolder<T> changeElements(List<SetHolder<T>> other, BiFunction<T, List<T>, T> replaceFunction) {
        if (other == null) return this;

        Set<Integer> otherUnionIds = new HashSet<>();
        for (SetHolder<T> holder : other) {
            if (holder != null) {
                for (T item : holder) {
                    if (item != null) otherUnionIds.add(item.getId());
                }
            }
        }

        for (Integer id : otherUnionIds) {
            T thisItem = this.get(id);
            if (thisItem != null) {
                List<T> params = new ArrayList<>();
                for (SetHolder<T> holder : other) {
                    params.add(holder != null ? holder.get(id) : null);
                }

                T newItem = replaceFunction.apply(thisItem, params);
                if (newItem == null) {
                    storage.remove(id);
                } else {
                    this.add(newItem);
                }
            }
        }
        return this;
    }

    // --- Merge Methods ---

    /**
     * Adds all elements that this does not contain <br>
     * Those elements that already exist in this are not added
     * @param extra - source
     * @return this
     */
    public SetHolder<T> merge(Set<T> extra) {
        if (extra == null) return this;
        for (T item : extra) {
            if (item != null && !this.contains(item)) {
                this.add(item);
            }
        }
        return this;
    }

    public static <T> Set<T> merge(Set<T> cur, Set<T> extra){
        if(cur == null) return extra;
        if(extra==null) return cur;
        for(T t : extra){
            if(t!=null && !cur.contains(t))
                cur.add(t);
        }
        return cur;
    }

    /**
     * Consecutive execution of merge(Set<T> extra)
     * @param extraList - source list
     * @return this
     */
    public SetHolder<T> merge(List<? extends Set<T>> extraList){
        if (extraList != null) {
            for (Set<T> extra : extraList) {
                this.merge(extra);
            }
        }
        return this;
    }

    /**
     * Adds addFunction(otherElem) for all elements not in this <br>
     * If an element is already contained in this, then replaces it with the result of replaceFunction(old, params)
     * @param extra - source
     * @param addFunction - function to get a new element
     * @param replaceFunction - function to replace an existing element
     * @return this
     */
    public SetHolder<T> merge(Set<T> extra, Function<T, T> addFunction, BiFunction<T, T, T> replaceFunction){
        if (extra == null) return this;
        for (T otherItem : extra) {
            if (otherItem == null) continue;
            int id = otherItem.getId();
            T thisItem = this.get(id);
            if (thisItem == null) {
                T newItem = addFunction.apply(otherItem);
                if (newItem != null) this.add(newItem);
            } else {
                T newItem = replaceFunction.apply(thisItem, otherItem);
                if (newItem == null) {
                    storage.remove(id);
                } else {
                    this.add(newItem);
                }
            }
        }
        return this;
    }

    /**
     * For every element in the union of extraList a List params is created <br>
     * Adds addFunction(params) for all elements not in this <br>
     * Replaces with replaceFunction(old, params) for all elements already in this <br>
     * @param extraList - source
     * @param addFunction - function to get a new element
     * @param replaceFunction - function to replace an existing element
     * @return this
     */
    public SetHolder<T> merge(List<SetHolder<T>> extraList, Function<List<T>, T> addFunction,  BiFunction<T, List<T>, T> replaceFunction){
        if (extraList == null) return this;

        Set<Integer> extraUnionIds = new HashSet<>();
        for (SetHolder<T> holder : extraList) {
            if (holder != null) {
                for (T item : holder) {
                    if (item != null) extraUnionIds.add(item.getId());
                }
            }
        }

        for (Integer id : extraUnionIds) {
            T thisItem = this.get(id);
            List<T> params = new ArrayList<>();
            for (SetHolder<T> holder : extraList) {
                params.add(holder != null ? holder.get(id) : null);
            }

            if (thisItem == null) {
                T newItem = addFunction.apply(params);
                if (newItem != null) this.add(newItem);
            } else {
                T newItem = replaceFunction.apply(thisItem, params);
                if (newItem == null) {
                    storage.remove(id);
                } else {
                    this.add(newItem);
                }
            }
        }
        return this;
    }

    // -- Check functions ---

    /**
     * Iterates over 'other' and checks pairs (this.get(id), otherElem). <br>
     * Note: This implementation only checks elements present in 'other'.
     * @param other - source
     * @param checkFunction - predicate to validate pairs
     * @return true if all pairs pass the check, false otherwise
     */
    public boolean checkInBoth(Set<T> other, BiPredicate<T, T> checkFunction) {
        if (other == null) return true;

        for (T otherItem : other) {
            if (otherItem == null) continue;
            T thisItem = this.get(otherItem.getId());
            if (!checkFunction.test(thisItem, otherItem)) {
                return false;
            }
        }
        return true;
    }

    /**
     * For every element in the union of all holders in other (and this), a List params is created.
     * @param other - list of specialized holders
     * @param checkFunction - predicate to validate (thisItem, params)
     * @return true if all pairs pass the check, false otherwise
     */
    public boolean checkInBoth(List<SetHolder<T>> other, BiPredicate<T, List<T>> checkFunction) {
        if (other == null) return true;

        // Collect the union of IDs from 'this' and all holders in 'other'
        Set<Integer> allIds = new HashSet<>(this.storage.keySet());
        for (SetHolder<T> holder : other) {
            if (holder != null) {
                for (T item : holder) {
                    if (item != null) allIds.add(item.getId());
                }
            }
        }

        for (Integer id : allIds) {
            T thisItem = this.get(id);
            List<T> params = new ArrayList<>();
            for (SetHolder<T> holder : other) {
                params.add(holder != null ? holder.get(id) : null);
            }

            if (!checkFunction.test(thisItem, params)) {
                return false;
            }
        }
        return true;
    }


    // --- clear and add all from look up ---

    /**
     * Deletes existing data and adding all elements from lookUp with ids from toAdd
     * @param toAdd a set to add
     * @param lookUp a holder of actual objects from toAdd
     * @return this
     */
    public SetHolder<T> clearAndAddAllFromLookUp(Set<Integer> toAdd, SetHolder<T> lookUp) {
        this.clear();
        if (toAdd != null && lookUp != null) {
            for (Integer id : toAdd) {
                T item = lookUp.get(id);
                if (item != null) {
                    this.add(item);
                }
            }
        }
        return this;
    }

    // --- Change set Methods ---

    /**
     * toRemove and toAdd will not be changed
     * If element is contained in, then after changing in the set will remain the element from: <br>
     * this, toAdd, toRemove -> toAdd ----- delete old one, add new one <br>
     * this, toRemove -> null ------------- delete old one <br>
     * toAdd, toRemove -> null ------------ add new one and immediately delete <br>
     * this, toAdd -> this ---------------- trying to add a copy, dismiss <br>
     * this -> this ----------------------- nothing changed <br>
     * toAdd -> toAdd --------------------- add new one <br>
     * toRemove -> null ------------------- removing non-existing, dismiss
     * @param toAdd a set to add
     * @param toRemove a set to remove
     * @return this
     */
    public SetHolder<T> changeSetTT(Set<T> toAdd, Set<T> toRemove) {
        if (toRemove != null) {
            Set<Integer> toRemIds = new HashSet<>();
            for (T t : toRemove) {
                if (t != null) toRemIds.add(t.getId());
            }
            return changeSetTI(toAdd, toRemIds);
        }
        return changeSetTI(toAdd, null);
    }

    /**
     * toAdd will not be changed, toRemove might be <br>
     * If element is contained in, then after changing in the set will remain the element from: <br>
     * this, toAdd, toRemove -> toAdd ----- delete old one, add new one <br>
     * this, toRemove -> null ------------- delete old one <br>
     * toAdd, toRemove -> null ------------ add new one and immediately delete <br>
     * this, toAdd -> this ---------------- trying to add a copy, dismiss <br>
     * this -> this ----------------------- nothing changed <br>
     * toAdd -> toAdd --------------------- add new one <br>
     * toRemove -> null ------------------- removing non-existing, dismiss
     * @param toAdd a set to add
     * @param toRemove a set to remove
     * @return this
     */
    public SetHolder<T> changeSetTI(Set<T> toAdd, Set<Integer> toRemove){
        if(toAdd != null && toRemove != null){
            for(T t : toAdd){
                boolean inThis = this.contains(t);
                boolean inRem = toRemove.contains(t.getId());
                if(inRem){
                    if(inThis) { // this, toAdd, toRem
                        this.remove(t.getId());
                        this.add(t);
                    }
                    toRemove.remove(t.getId()); // toAdd, toRem AND this, toAdd, toRem
                }
                else if(!inThis) { // toAdd
                    this.add(t);
                }
            }
            for(Integer id : toRemove){
                boolean inThis = this.contains(id);
                if(inThis){ //this, toRem
                    this.remove(id);
                }
            }
        }
        else if(toAdd !=null){
            for(T t : toAdd){
                boolean inThis = this.contains(t);
                if(!inThis){
                    this.add(t);
                }
            }
        }
        else if(toRemove!=null){
            for(Integer id : toRemove){
                boolean inThis = this.contains(id);
                if(inThis){
                    this.remove(id);
                }
            }
        }
        return this;
    }

    /**
     * toAdd will not be changed, toRemove might be <br>
     * If element is contained in, then after changing in the set will remain the element from: <br>
     * this, toAdd, toRemove -> toAdd ----- delete old one, add new one <br>
     * this, toRemove -> null ------------- delete old one <br>
     * toAdd, toRemove -> null ------------ add new one and immediately delete <br>
     * this, toAdd -> this ---------------- trying to add a copy, dismiss <br>
     * this -> this ----------------------- nothing changed <br>
     * toAdd -> toAdd --------------------- add new one <br>
     * toRemove -> null ------------------- removing non-existing, dismiss
     * @param toAdd a set to add
     * @param toRemove a set to remove
     * @return this
     */
    public <C extends Identifiable> SetHolder<T> changeSetDTOI(Set<C> toAdd, Set<Integer> toRemove, Function<C, T> restoreDTO){
        if(toAdd != null && toRemove != null){
            for(C t : toAdd){
                boolean inThis = this.contains(t);
                boolean inRem = toRemove.contains(t.getId());
                if(inRem){
                    if(inThis) { // this, toAdd, toRem
                        this.remove(t.getId());
                        T newItem = restoreDTO.apply(t);
                        if(newItem!=null)
                            this.add(newItem);
                    }
                    toRemove.remove(t.getId()); // toAdd, toRem AND this, toAdd, toRem
                }
                else if(!inThis) { // toAdd
                    T newItem = restoreDTO.apply(t);
                    if(newItem!=null)
                        this.add(newItem);
                }
            }
            for(Integer id : toRemove){
                boolean inThis = this.contains(id);
                if(inThis){ //this, toRem
                    this.remove(id);
                }
            }
        }
        else if(toAdd !=null){
            for(C t : toAdd){
                boolean inThis = this.contains(t);
                if(!inThis){
                    T newItem = restoreDTO.apply(t);
                    if(newItem!=null)
                        this.add(newItem);
                }
            }
        }
        else if(toRemove!=null){
            for(Integer id : toRemove){
                boolean inThis = this.contains(id);
                if(inThis){
                    this.remove(id);
                }
            }
        }
        return this;
    }

    /**
     * toAdd will not be changed, toRemove might be. <br>
     * If element is contained in, then after changing in the set will remain the element from: <br>
     * this, toAdd, toRemove -> toAdd ----- delete old one, add new one <br>
     * this, toRemove -> null ------------- delete old one <br>
     * toAdd, toRemove -> null ------------ add new one and immediately delete <br>
     * this, toAdd -> this ---------------- trying to add a copy, dismiss <br>
     * this -> this ----------------------- nothing changed <br>
     * toAdd -> toAdd --------------------- add new one <br>
     * toRemove -> null ------------------- removing non-existing, dismiss
     * If an element is not found in lookUp, then it is not added to this.
     * @param toAdd a set to add
     * @param toRemove a set to remove
     * @param lookUp a holder of actual objects from toAdd
     * @return this
     */
    public SetHolder<T> changeSetII(Set<Integer> toAdd, Set<Integer> toRemove, SetHolder<T> lookUp) {
        if (toAdd != null && toRemove != null) {
            for (Integer id : toAdd) {
                boolean inRem = toRemove.contains(id);
                boolean inThis = this.contains(id);

                if (inRem) {
                    if (inThis) {
                        this.remove(id);
                        T newItem = lookUp.get(id);
                        if (newItem != null) {
                            this.add(newItem);
                        }
                    }
                    toRemove.remove(id);
                } else if (!inThis) {
                    T newItem = lookUp.get(id);
                    if (newItem != null) {
                        this.add(newItem);
                    }
                }
            }
            for (Integer id : toRemove) {
                if (this.contains(id)) {
                    this.remove(id);
                }
            }
        } else if (toAdd != null) {
            for (Integer id : toAdd) {
                if (!this.contains(id)) {
                    T newItem = lookUp.get(id);
                    if (newItem != null) {
                        this.add(newItem);
                    }
                }
            }
        } else if (toRemove != null) {
            for (Integer id : toRemove) {
                if (this.contains(id)) {
                    this.remove(id);
                }
            }
        }
        return this;
    }

    /**
     * toAdd will not be changed, toRemove might be. <br>
     * If element is contained in, then after changing in the set will remain the element from: <br>
     * this, toAdd, toRemove -> toAdd ----- delete old one, add new one <br>
     * this, toRemove -> null ------------- delete old one <br>
     * toAdd, toRemove -> null ------------ add new one and immediately delete <br>
     * this, toAdd -> this ---------------- trying to add a copy, dismiss <br>
     * this -> this ----------------------- nothing changed <br>
     * toAdd -> toAdd --------------------- add new one <br>
     * toRemove -> null ------------------- removing non-existing, dismiss <br>
     * If an element is not found in lookUp, then it is not added to this.<br>
     * Also applies functions to removed and added elements
     * @param toAdd a set to add
     * @param toRemove a set to remove
     * @param lookUp a holder of actual objects from toAdd
     * @param modifyOldFunction modifies an element after removing
     * @param modifyNewFunction modifies an element before adding
     */
    public void changeSetII(Set<Integer> toAdd, Set<Integer> toRemove, SetHolder<T> lookUp,
                            Consumer<T> modifyOldFunction, Function<T,T> modifyNewFunction) {
        if(modifyOldFunction == null)  modifyOldFunction = (t)->{};
        if(modifyNewFunction == null) modifyNewFunction = (t)->t;

        if (toAdd != null && toRemove != null) {
            for (Integer id : toAdd) {
                boolean inRem = toRemove.contains(id);
                boolean inThis = this.contains(id);

                if (inRem) {
                    if (inThis) {
                        modifyOldFunction.accept(this.remove(id));
                        T newItem = lookUp.get(id);
                        if (newItem != null) {
                            this.add(modifyNewFunction.apply(newItem));
                        }
                    }
                    toRemove.remove(id);
                } else if (!inThis) {
                    T newItem = lookUp.get(id);
                    if (newItem != null) {
                        this.add(modifyNewFunction.apply(newItem));
                    }
                }
            }
            for (Integer id : toRemove) {
                if (this.contains(id)) {
                    modifyOldFunction.accept(this.remove(id));
                }
            }
        } else if (toAdd != null) {
            for (Integer id : toAdd) {
                if (!this.contains(id)) {
                    T newItem = lookUp.get(id);
                    if (newItem != null) {
                        this.add(modifyNewFunction.apply(newItem));
                    }
                }
            }
        } else if (toRemove != null) {
            for (Integer id : toRemove) {
                if (this.contains(id)) {
                    modifyOldFunction.accept(this.remove(id));
                }
            }
        }
    }

    /**
     * All allowed combinations of an element containing in this, toAdd, toRemove are: <br>
     * this, toAdd, toRemove -> toAdd ----- delete old one, add new one <br>
     * this, toRemove -> null ------------- delete old one <br>
     * toAdd, toRemove -> null ------------ add new one and immediately delete <br>
     * this -> this ----------------------- nothing changed <br>
     * toAdd -> toAdd --------------------- add new one <br>
     * Not allowed combinations are: <br>
     * this, toAdd -> this ---------------- trying to add a copy <br>
     * toRemove -> null ------------------- removing non-existing
     * @param toAdd a set to add
     * @param toRemove a set to remove
     * @return result of the check. If false, then not all changes will be applied
     */
    public boolean checkChangeSetTT(Set<T> toAdd, Set<T> toRemove) {
        if (toAdd != null && toRemove != null) {
            for (T item : toAdd) {
                if (this.contains(item) && !toRemove.contains(item)) return false;
            }
            for (T item : toRemove) {
                if (!this.contains(item) && !toAdd.contains(item)) return false;
            }
        } else if (toAdd != null) {
            for (T item : toAdd) {
                if (this.contains(item)) return false;
            }
        } else if (toRemove != null) {
            for (T item : toRemove) {
                if (!this.contains(item)) return false;
            }
        }
        return true;
    }

    /**
     * All allowed combinations of an element containing in this, toAdd, toRemove are: <br>
     * this, toAdd, toRemove -> toAdd ----- delete old one, add new one <br>
     * this, toRemove -> null ------------- delete old one <br>
     * toAdd, toRemove -> null ------------ add new one and immediately delete <br>
     * this -> this ----------------------- nothing changed <br>
     * toAdd -> toAdd --------------------- add new one <br>
     * Not allowed combinations are: <br>
     * this, toAdd -> this ---------------- trying to add a copy <br>
     * toRemove -> null ------------------- removing non-existing
     * @param toAdd a set to add
     * @param toRemove a set to remove
     * @return result of the check. If false, then not all changes will be applied
     */
    public <C extends Identifiable>boolean checkChangeSetTI(Set<C> toAdd, Set<Integer> toRemove) {
        if (toAdd != null && toRemove != null) {
            Set<Integer> addIds = new HashSet<>();
            for (C item : toAdd) {
                Integer id = item.getId();
                addIds.add(id);
                if (this.contains(id) && !toRemove.contains(id)) return false;
            }
            for (Integer id : toRemove) {
                if (!this.contains(id) && !addIds.contains(id)) return false;
            }
        } else if (toAdd != null) {
            for (C item : toAdd) {
                if (this.contains(item.getId())) return false;
            }
        } else if (toRemove != null) {
            for (Integer id : toRemove) {
                if (!this.contains(id)) return false;
            }
        }
        return true;
    }



    /**
     * All allowed combinations of an element containing in this, toAdd, toRemove are: <br>
     * this, toAdd, toRemove -> toAdd ----- delete old one, add new one <br>
     * this, toRemove -> null ------------- delete old one <br>
     * toAdd, toRemove -> null ------------ add new one and immediately delete <br>
     * this -> this ----------------------- nothing changed <br>
     * toAdd -> toAdd --------------------- add new one <br>
     * Not allowed combinations are: <br>
     * this, toAdd -> this ---------------- trying to add a copy <br>
     * toRemove -> null ------------------- removing non-existing
     * @param toAdd a set to add
     * @param toRemove a set to remove
     * @return result of the check. If false, then not all changes will be applied
     */
    public boolean checkChangeSetII(Set<Integer> toAdd, Set<Integer> toRemove) {
        if (toAdd != null && toRemove != null) {
            for (Integer id : toAdd) {
                if (this.contains(id) && !toRemove.contains(id)) return false;
            }
            for (Integer id : toRemove) {
                if (!this.contains(id) && !toAdd.contains(id)) return false;
            }
        } else if (toAdd != null) {
            for (Integer id : toAdd) {
                if (this.contains(id)) return false;
            }
        } else if (toRemove != null) {
            for (Integer id : toRemove) {
                if (!this.contains(id)) return false;
            }
        }
        return true;
    }

    /**
     * All allowed combinations of an element containing in this, toAdd, toRemove are: <br>
     * this, toAdd, toRemove -> toAdd ----- delete old one, add new one <br>
     * this, toRemove -> null ------------- delete old one <br>
     * toAdd, toRemove -> null ------------ add new one and immediately delete <br>
     * this -> this ----------------------- nothing changed <br>
     * toAdd -> toAdd --------------------- add new one <br>
     * Not allowed combinations are: <br>
     * this, toAdd -> this ---------------- trying to add a copy <br>
     * toRemove -> null ------------------- removing non-existing <br>
     * In case of adding, the id of added element must exist in lookUp
     * @param toAdd a set to add
     * @param toRemove a set to remove
     * @param lookUp a holder of actual objects from toAdd
     * @return result of the check. If false, then not all changes will be applied
     */
    public boolean checkChangeSetIILookUp(Set<Integer> toAdd, Set<Integer> toRemove, SetHolder<T> lookUp) {
        return SetHolder.checkChangeSetIILookUp(this, toAdd, toRemove, lookUp);
    }


    // --- Static change set methods ---


    /**
     * All allowed combinations of an element containing in this, toAdd, toRemove are: <br>
     * this, toAdd, toRemove -> toAdd ----- delete old one, add new one <br>
     * this, toRemove -> null ------------- delete old one <br>
     * toAdd, toRemove -> null ------------ add new one and immediately delete <br>
     * this -> this ----------------------- nothing changed <br>
     * toAdd -> toAdd --------------------- add new one <br>
     * Not allowed combinations are: <br>
     * this, toAdd -> this ---------------- trying to add a copy <br>
     * toRemove -> null ------------------- removing non-existing <br>
     * In case of adding, the id of added element must exist in lookUp
     * @param current "this" set
     * @param toAdd a set to add
     * @param toRemove a set to remove
     * @param lookUp a holder of actual objects from toAdd
     * @return result of the check. If false, then not all changes will be applied
     */
    public static <T extends Identifiable> boolean checkChangeSetIILookUp(Set<T> current, Set<Integer> toAdd, Set<Integer> toRemove, SetHolder<T> lookUp) {
        if(current == null) return false;
        if (toAdd != null && toRemove != null) {
            for (Integer id : toAdd) {
                boolean inThis = current.contains(id);
                boolean inRem = toRemove.contains(id);

                if (inThis && !inRem) return false;

                if (!inRem || inThis) {
                    if (lookUp.get(id) == null) return false;
                }
            }
            for (Integer id : toRemove) {
                if (!current.contains(id) && !toAdd.contains(id)) return false;
            }
        } else if (toAdd != null) {
            for (Integer id : toAdd) {
                if (current.contains(id)) return false;
                if (lookUp.get(id) == null) return false;
            }
        } else if (toRemove != null) {
            for (Integer id : toRemove) {
                if (!current.contains(id)) return false;
            }
        }
        return true;
    }

    // --- Set Interface Implementation (Delegation to Map) ---

    @Override
    public int size() { return storage.size(); }

    @Override
    public boolean isEmpty() { return storage.isEmpty(); }

    @Override
    public boolean contains(Object o) {
        if(o instanceof Integer)
            return storage.containsKey((Integer) o);
        if(o instanceof Identifiable)
            return storage.containsKey(((Identifiable) o).getId());
        return false;
    }



    @Override
    public Iterator<T> iterator() { return storage.values().iterator(); }

    @Override
    public Object[] toArray() { return storage.values().toArray(); }

    @Override
    public <E> E[] toArray(E[] a) { return storage.values().toArray(a); }

    @Override
    public boolean add(T t) {
        if (t == null) return false;
        return storage.put(t.getId(), t) == null;
    }

    @Override
    public boolean remove(Object o) {
        if (o instanceof Identifiable) {
            return storage.remove(((Identifiable) o).getId()) != null;
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        if(c==null) return true;
        for (Object e : c) if (!contains(e)) return false;
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        boolean modified = false;
        for (T e : c) if (add(e)) modified = true;
        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) { return storage.values().retainAll(c); }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean modified = false;
        for (Object e : c) if (remove(e)) modified = true;
        return modified;
    }

    @Override
    public void clear() { storage.clear(); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Set)) return false;
        Set<?> other = (Set<?>) o;
        return other.size() == size() && containsAll(other);
    }

    @Override
    public int hashCode() {
        int h = 0;
        for (T obj : storage.values()) {
            if (obj != null) h += obj.hashCode();
        }
        return h;
    }
}
