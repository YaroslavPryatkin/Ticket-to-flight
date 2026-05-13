package com.game.Ticket_To_Flight.Utilities;

import java.util.HashMap;
import java.util.Map;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class MapHolder<K extends Identifiable, V> implements Map<K, V> {
    private final Map<K, V> storage = new HashMap<>();


    public MapHolder(){};

    public MapHolder(Map<K,V> other){
        if(other == null) return;
        this.putAll(other);
    }
    // --- Change elemets functions ---

    /**
     * Applies the replaceFunction to elements that exist in both storage and the provided params map.
     * If replaceFunction returns null, the element is removed from storage.
     */
    public MapHolder<K,V> changeElements(Map<K, V> params, BiFunction<V, V, V> replaceFunction) {
        if (params == null) return this;

        for (Map.Entry<K, V> entry : params.entrySet()) {
            K key = entry.getKey();
            if (storage.containsKey(key)) {
                V storageValue = storage.get(key);
                V paramValue = entry.getValue();

                V newValue = replaceFunction.apply(storageValue, paramValue);
                if (newValue == null) {
                    storage.remove(key);
                } else {
                    storage.put(key, newValue);
                }
            }
        }
        return this;
    }

    /**
     * Applies the replaceFunction to all keys found in the parameters, if they exist in storage.
     * Updates the storage with the newly returned value.
     *
     * @param params   List of parameter maps (e.g., [toAddMap, toRemoveMap]).
     * @param replaceFunction Function taking (Current Value, List of Param Values) and returning the New Value.
     */
    public MapHolder<K,V> changeElements(List<? extends Map<K, V>> params, BiFunction<V, List<V>, V> replaceFunction) {
        if (params == null) return this;
        Set<K> allKeys = collectAllKeys(params);

        for (K key : allKeys) {
            if (storage.containsKey(key)) {
                V storageValue = storage.get(key);
                List<V> paramValues = collectParamValues(key, params);

                // Apply the replaceFunction and explicitly update the map
                V newValue = replaceFunction.apply(storageValue, paramValues);
                if(newValue==null) storage.remove(key);
                else storage.put(key, newValue);
            }
        }
        return this;
    }


    // --- Merge functions

    /**
     * Adds addFunction(paramValue) for keys not in storage.
     * Replaces existing values with replaceFunction(oldValue, paramValue).
     * If replaceFunction returns null, the element is removed.
     */
    public MapHolder<K,V> merge(Map<K, V> extra, Function<V, V> addFunction, BiFunction<V, V, V> replaceFunction) {
        if (extra == null) return this;

        for (Map.Entry<K, V> entry : extra.entrySet()) {
            K key = entry.getKey();
            V extraValue = entry.getValue();

            if (!storage.containsKey(key)) {
                V newValue = addFunction.apply(extraValue);
                if (newValue != null) {
                    storage.put(key, newValue);
                }
            } else {
                V oldValue = storage.get(key);
                V newValue = replaceFunction.apply(oldValue, extraValue);
                if (newValue == null) {
                    storage.remove(key);
                } else {
                    storage.put(key, newValue);
                }
            }
        }
        return this;
    }

    /**
     * For every key in the union of extraList:
     * If key is not in storage, adds addFunction(params).
     * If key is in storage, replaces with replaceFunction(oldValue, params).
     * If replaceFunction returns null, the element is removed.
     */
    public MapHolder<K,V> merge(List<? extends Map<K, V>> extraList, Function<List<V>, V> addFunction, BiFunction<V, List<V>, V> replaceFunction) {
        if (extraList == null) return this;

        Set<K> allExtraKeys = collectAllKeys(extraList);

        for (K key : allExtraKeys) {
            List<V> paramValues = collectParamValues(key, extraList);

            if (!storage.containsKey(key)) {
                V newValue = addFunction.apply(paramValues);
                if (newValue != null) {
                    storage.put(key, newValue);
                }
            } else {
                V oldValue = storage.get(key);
                V newValue = replaceFunction.apply(oldValue, paramValues);
                if (newValue == null) {
                    storage.remove(key);
                } else {
                    storage.put(key, newValue);
                }
            }
        }
        return this;
    }

    /**
     * Adds all entries from the extra map whose keys are not already present in storage.
     * Entries with keys that already exist in storage are ignored.
     *
     * @param extra - source map
     * @return this (or void, depending on your MapHolder preference)
     */
    public MapHolder<K, V> merge(Map<K, V> extra) {
        if (extra == null) return this;

        for (Map.Entry<K, V> entry : extra.entrySet()) {
            K key = entry.getKey();
            // Those elements that already exist in this are not added
            if (!storage.containsKey(key)) {
                storage.put(key, entry.getValue());
            }
        }
        return this;
    }

    /**
     * Consecutive execution of merge(Map<K, V> extra) for a list of maps.
     *
     * @param extraList - source list of maps
     * @return this
     */
    public MapHolder<K, V> merge(List<? extends Map<K, V>> extraList) {
        if (extraList != null) {
            for (Map<K, V> extra : extraList) {
                this.merge(extra);
            }
        }
        return this;
    }

    // --- Check function ---

    /**
     * Validates the proposed changes.
     * Returns false if any key is missing in storage, or if the checkFunction fails.
     */
    public boolean checkChangeElements(Map<K,V> other, BiPredicate<V, V> checkFunction){
        for (Map.Entry<K,V> e : other.entrySet()) {
            V storageValue = storage.get(e.getKey());
            if(storageValue == null) return false;
            if (!checkFunction.test(storageValue, e.getValue())) {
                return false;
            }
        }

        return true;
    }

    /**
     * Validates the proposed changes.
     * Returns false if any key is missing in storage, or if the checkFunction fails.
     */
    public boolean checkChangeElements(List<? extends Map<K, V>> params, BiPredicate<V, List<V>> checkFunction) {
        Set<K> allKeys = collectAllKeys(params);

        // 1. Ensure ALL keys from params exist in the storage
        for (K key : allKeys) {
            if (!storage.containsKey(key)) {
                return false;
            }
        }

        // 2. Apply the validation function for each key
        for (K key : allKeys) {
            V storageValue = storage.get(key);
            List<V> paramValues = collectParamValues(key, params);

            if (!checkFunction.test(storageValue, paramValues)) {
                return false;
            }
        }

        return true;
    }

    // --- Helper Methods ---

    private Set<K> collectAllKeys(List<? extends Map<K, V>> params) {
        Set<K> allKeys = new HashSet<>();
        if (params != null) {
            for (Map<K, V> paramMap : params) {
                if (paramMap != null) {
                    allKeys.addAll(paramMap.keySet());
                }
            }
        }
        return allKeys;
    }

    private List<V> collectParamValues(K key, List<? extends Map<K, V>> params) {
        List<V> paramValues = new ArrayList<>();
        if (params != null) {
            for (Map<K, V> paramMap : params) {
                paramValues.add(paramMap != null ? paramMap.get(key) : null);
            }
        }
        return paramValues;
    }

    // --- Map Interface Delegation ---
    @Override public int size() { return storage.size(); }
    @Override public boolean isEmpty() { return storage.isEmpty(); }
    @Override public boolean containsKey(Object key) { return storage.containsKey(key); }
    @Override public boolean containsValue(Object value) { return storage.containsValue(value); }
    @Override public V get(Object key) { return storage.get(key); }
    @Override public V put(K key, V value) { return storage.put(key, value); }
    @Override public V remove(Object key) { return storage.remove(key); }
    @Override public void putAll(Map<? extends K, ? extends V> m) { storage.putAll(m); }
    @Override public void clear() { storage.clear(); }
    @Override public Set<K> keySet() { return storage.keySet(); }
    @Override public Collection<V> values() { return storage.values(); }
    @Override public Set<Entry<K, V>> entrySet() { return storage.entrySet(); }
}
