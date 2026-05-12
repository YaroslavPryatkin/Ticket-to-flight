package com.game.Ticket_To_Flight.Utilities;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

public class SomethingHolder<K extends Identifiable, V> implements Map<K, V> {
    private final Map<K, V> storage = new HashMap<>();



    /**
     * Applies the function to all keys found in the parameters, if they exist in storage.
     * Updates the storage with the newly returned value.
     *
     * @param params   List of parameter maps (e.g., [toAddMap, toRemoveMap]).
     * @param function Function taking (Current Value, List of Param Values) and returning the New Value.
     */
    public void change(List<Map<K, V>> params, BiFunction<V, List<V>, V> function) {
        Set<K> allKeys = collectAllKeys(params);

        for (K key : allKeys) {
            if (storage.containsKey(key)) {
                V storageValue = storage.get(key);
                List<V> paramValues = collectParamValues(key, params);

                // Apply the function and explicitly update the map
                V newValue = function.apply(storageValue, paramValues);
                if(newValue==null) storage.remove(key);
                else storage.put(key, newValue);
            }
        }
    }

    /**
     * Validates the proposed changes.
     * Returns false if any key is missing in storage, or if the checkFunction fails.
     */
    public boolean checkChange(List<Map<K, V>> params, BiPredicate<V, List<V>> checkFunction) {
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

    private Set<K> collectAllKeys(List<Map<K, V>> params) {
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

    private List<V> collectParamValues(K key, List<Map<K, V>> params) {
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
