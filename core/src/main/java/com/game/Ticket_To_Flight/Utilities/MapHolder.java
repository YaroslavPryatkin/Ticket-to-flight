package com.game.Ticket_To_Flight.Utilities;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;


import java.util.Map;

import java.util.*;
import java.util.function.*;

public class MapHolder<K extends Identifiable, V> implements Map<Integer, V> {
    private final Int2ObjectOpenHashMap<V> storage = new Int2ObjectOpenHashMap<>();
    private final SetHolder<K> keyHolder;

    public MapHolder(SetHolder<K> keyHolder) {
        if (keyHolder == null) throw new NullPointerException();
        this.keyHolder = keyHolder;
    }



    public MapHolder(SetHolder<K> keyHolder, Map<? extends K, ? extends V> other) {
        if (keyHolder == null) throw new NullPointerException();
        this.keyHolder = keyHolder;
        if (other != null) {
            this.putAllIdentifiable(other);
        }
    }

    // --- specific methods for multi sets

    /**
     * Returns a lazy, immutable {@link List} view of the elements contained in the holder.
     * @param <K>    the type of identifiable elements
     * @param holder the MapHolder containing the storage and key references
     * @return a read-only list view that performs index-based lookups in O(log M) time
     * @throws NullPointerException if the provided holder is null
     */
    public static <K extends Identifiable> List<K> viewAsList(MapHolder<K, Integer> holder) {
        NavigableMap<Integer, Integer> indexMap = new TreeMap<>();
        int totalSize = 0;

        for (var entry : holder.storage.entrySet()) {
            if (entry.getValue() > 0) {
                indexMap.put(totalSize, entry.getKey());
                totalSize += entry.getValue();
            }
        }

        final int finalSize = totalSize;

        return new AbstractList<K>() {
            @Override
            public K get(int index) {
                if (index < 0 || index >= finalSize) {
                    throw new IndexOutOfBoundsException();
                }
                Integer startOffset = indexMap.floorKey(index);
                Integer storageKey = indexMap.get(startOffset);
                return holder.keyHolder.get(storageKey);
            }

            @Override
            public int size() {
                return finalSize;
            }
        };
    }

    /**
     * Returns a memory-efficient iterator over the elements in the holder.
     * <p>
     * This iterator is lazy and does not materialise the full list in memory.
     * <b>Note:</b> This implementation departs from the standard Iterator contract:
     * next() returns {@code null} instead of throwing {@link NoSuchElementException}
     * when the iteration is complete, allowing for a single-call check in hot loops.
     * </p>
     *
     * @param <K>    the type of identifiable elements
     * @param holder the MapHolder containing data
     * @return an iterator that returns elements or {@code null} when exhausted
     */
    public static <K extends Identifiable> Iterator<K> viewAsListIterator(MapHolder<K, Integer> holder) {
        return new Iterator<K>() {
            private final Iterator<Map.Entry<Integer, Integer>> entryIterator =
                holder.storage.entrySet().iterator();

            private K currentItem = null;
            private int remaining = 0;

            @Override
            public boolean hasNext() {
                // Ищем следующий доступный элемент, если текущий исчерпан
                while (remaining <= 0 && entryIterator.hasNext()) {
                    Map.Entry<Integer, Integer> entry = entryIterator.next();
                    Integer count = entry.getValue();

                    if (count != null && count > 0) {
                        K item = holder.keyHolder.get(entry.getKey());
                        if (item != null) {
                            currentItem = item;
                            remaining = count;
                        }
                    }
                }
                return remaining > 0;
            }

            @Override
            public K next() {
                if (!hasNext()) {
                    return null;
                }
                remaining--;
                return currentItem;
            }
        };
    }

    /**
     * Returns a lazy iterator over entries (Item -> Count).
     * <b>Note:</b> next() returns null when exhausted to allow single-check iteration.
     */
    public static <K extends Identifiable> Iterator<Entry<K, Integer>> viewAsEntrySet(MapHolder<K, Integer> holder) {
        final Iterator<Map.Entry<Integer, Integer>> storageIterator = holder.storage.entrySet().iterator();

        return new Iterator<Entry<K, Integer>>() {
            private Entry<K, Integer> cachedNext = null;

            @Override
            public boolean hasNext() {
                if (cachedNext != null) return true;

                while (storageIterator.hasNext()) {
                    Map.Entry<Integer, Integer> storageEntry = storageIterator.next();
                    Integer count = storageEntry.getValue();

                    if (count != null && count > 0) {
                        K item = holder.keyHolder.get(storageEntry.getKey());
                        if (item != null) {
                            cachedNext = new AbstractMap.SimpleImmutableEntry<>(item, count);
                            return true;
                        }
                    }
                }
                return false;
            }

            @Override
            public Entry<K, Integer> next() {
                if (!hasNext()) {
                    return null;
                }
                Entry<K, Integer> result = cachedNext;
                cachedNext = null;
                return result;
            }
        };
    }
    // --- Change elemets functions ---
    {
    /**
     * Applies the replaceFunction to elements that exist in both storage and the provided params map.
     * If replaceFunction returns null, the element is removed from storage.
     */
//    public MapHolder<K,V> changeElementsType(Map<K, V> params, BiFunction<V, V, V> replaceFunction) {
//        if (params == null) return this;
//
//        for (Map.Entry<K, V> entry : params.entrySet()) {
//            K key = entry.getKey();
//            if (this.containsKey(key)) {
//                V storageValue = this.get(key);
//                V paramValue = entry.getValue();
//
//                V newValue = replaceFunction.apply(storageValue, paramValue);
//                if (newValue == null) {
//                    this.remove(key);
//                } else {
//                    this.put(key, newValue);
//                }
//            }
//        }
//        return this;
//    }
}
    /**
     * Applies the replaceFunction to elements that exist in both storage and the provided params map.
     * If replaceFunction returns null, the element is removed from storage.
     */
    public MapHolder<K, V> changeElements(Map<Integer, V> params, BiFunction<V, V, V> replaceFunction) {
        if (params == null) return this;

        for (Map.Entry<Integer, V> entry : params.entrySet()) {
            Integer key = entry.getKey();
            if (this.containsKey(key)) {
                V storageValue = this.get(key);
                V paramValue = entry.getValue();

                V newValue = replaceFunction.apply(storageValue, paramValue);
                if (newValue == null) {
                    this.remove(key);
                } else {
                    storage.put(key, newValue);
                }
            }
        }
        return this;
    }
    {
    /**
     * Applies the replaceFunction to all keys found in the parameters, if they exist in storage.
     * Updates the storage with the newly returned value.
     *
     * @param params   List of parameter maps (e.g., [toAddMap, toRemoveMap]).
     * @param replaceFunction Function taking (Current Value, List of Param Values) and returning the New Value.
     */
//    public MapHolder<K,V> changeElementsType(List<? extends Map<K, V>> params, BiFunction<V, List<V>, V> replaceFunction) {
//        if (params == null) return this;
//        Set<K> allKeys = collectAllKeysType(params);
//
//        for (K key : allKeys) {
//            if (this.containsKey(key)) {
//                V storageValue = this.get(key);
//                List<V> paramValues = collectParamValuesType(key, params);
//
//                // Apply the replaceFunction and explicitly update the map
//                V newValue = replaceFunction.apply(storageValue, paramValues);
//                if(newValue==null) this.remove(key);
//                else this.put(key, newValue);
//            }
//        }
//        return this;
//    }

}
    /**
     * Applies the replaceFunction to all keys found in the parameters, if they exist in storage.
     * Updates the storage with the newly returned value.
     *
     * @param params   List of parameter maps (e.g., [toAddMap, toRemoveMap]).
     * @param replaceFunction Function taking (Current Value, List of Param Values) and returning the New Value.
     */
    public MapHolder<K,V> changeElements(List<? extends Map<Integer, V>> params, BiFunction<V, List<V>, V> replaceFunction) {
        if (params == null) return this;
        Set<Integer> allKeys = collectAllKeysInteger(params);

        for (Integer key : allKeys) {
            if (this.containsKey(key)) {
                V thisValue = this.get(key);
                List<V> paramValues = collectParamValuesInteger(key, params);

                // Apply the replaceFunction and explicitly update the map
                V newValue = replaceFunction.apply(thisValue, paramValues);
                if(newValue==null) this.remove(key);
                else storage.put(key, newValue);
            }
        }
        return this;
    }

    /**
     * Validates the proposed changes.
     * Returns false if any key is missing in storage, or if the checkFunction fails.
     */
    public boolean checkChangeElements(Map<Integer,V> other, BiPredicate<V, V> checkFunction){
        for (Map.Entry<Integer,V> e : other.entrySet()) {
            V thisValue = this.get(e.getKey());
            if(thisValue == null) return false;
            if (!checkFunction.test(thisValue, e.getValue())) {
                return false;
            }
        }

        return true;
    }

    /**
     * Validates the proposed changes.
     * Returns false if any key is missing in storage, or if the checkFunction fails.
     */
    public boolean checkChangeElements(List<? extends Map<Integer, V>> params, BiPredicate<V, List<V>> checkFunction) {
        Set<Integer> allKeys = collectAllKeysInteger(params);

        // 1. Ensure ALL keys from params exist in the this
        for (Integer key : allKeys) {
            if (!this.containsKey(key)) {
                return false;
            }
        }

        // 2. Apply the validation function for each key
        for (Integer key : allKeys) {
            V thisValue = this.get(key);
            List<V> paramValues = collectParamValuesInteger(key, params);

            if (!checkFunction.test(thisValue, paramValues)) {
                return false;
            }
        }

        return true;
    }


    // --- Merge functions

    /**
     * Adds addFunction(paramValue) for keys not in storage. <br>
     * Replaces existing values with replaceFunction(oldValue, paramValue). <br>
     * If replaceFunction returns null, the element is removed.<br>
     * @return this
     */
    public MapHolder<K,V> merge(Map<Integer, V> extra, Function<V, V> addFunction, BiFunction<V, V, V> replaceFunction) {
        if (extra == null || replaceFunction == null || addFunction == null) return this;

        for (Map.Entry<Integer, V> entry : extra.entrySet()) {
            Integer key = entry.getKey();
            V extraValue = entry.getValue();

            if (!this.containsKey(key)) {
                V newValue = addFunction.apply(extraValue);
                if (newValue != null) {
                    storage.put(key, newValue);
                }
            } else {
                V oldValue = this.get(key);
                V newValue = replaceFunction.apply(oldValue, extraValue);
                if (newValue == null) {
                    this.remove(key);
                } else {
                    storage.put(key, newValue);
                }
            }
        }
        return this;
    }

    public static <V> Map<Integer, V> merge(Map<Integer, V> cur, Map<Integer, V> extra, Function<V, V> addFunction, BiFunction<V, V, V> replaceFunction){
        if(extra==null) return cur;
        if(cur == null) cur = new HashMap<>();
        for (Map.Entry<Integer, V> entry : extra.entrySet()) {
            Integer key = entry.getKey();
            V extraValue = entry.getValue();

            if (!cur.containsKey(key)) {
                V newValue = addFunction.apply(extraValue);
                if (newValue != null) {
                    cur.put(key, newValue);
                }
            } else {
                V oldValue = cur.get(key);
                V newValue = replaceFunction.apply(oldValue, extraValue);
                if (newValue == null) {
                    cur.remove(key);
                } else {
                    cur.put(key, newValue);
                }
            }
        }
        if(cur.isEmpty()) cur = null;
        return cur;
    }
    /**
     * For every key in the union of extraList: <br>
     * If key is not in storage, adds addFunction(paramValues). <br>
     * If key is in storage, replaces with replaceFunction(oldValue, paramValues). <br>
     * If replaceFunction returns null, the element is removed.
     * @return this
     */
    public MapHolder<K,V> merge(
        List<? extends Map<Integer, V>> extraList,
        Function<List<V>, V> addFunction, BiFunction<V, List<V>, V> replaceFunction) {
        if (extraList == null) return this;

        Set<Integer> allExtraKeys = collectAllKeysInteger(extraList);

        for (Integer key : allExtraKeys) {
            List<V> paramValues = collectParamValuesInteger(key, extraList);

            if (!this.containsKey(key)) {
                V newValue = addFunction.apply(paramValues);
                if (newValue != null) {
                    storage.put(key, newValue);
                }
            } else {
                V oldValue = this.get(key);
                V newValue = replaceFunction.apply(oldValue, paramValues);
                if (newValue == null) {
                    this.remove(key);
                } else {
                    storage.put(key, newValue);
                }
            }
        }
        return this;
    }


    /**
     * For every key in the union of extraList: <br>
     * If key is not in storage, adds addFunction(paramValues). <br>
     * If key is in storage, replaces with replaceFunction(oldValue, paramValues). <br>
     * If replaceFunction returns null, the element is removed.
     * Replaces all null parameters with insteadOfNull(paramNumber)
     * @return this
     */
    public MapHolder<K,V> merge(
        List<? extends Map<Integer, V>> extraList,
        Function<List<V>, V> addFunction, BiFunction<V, List<V>, V> replaceFunction,
        Function<Integer, V> insteadOfNull) {
        if (extraList == null || addFunction == null || insteadOfNull == null || replaceFunction == null) return this;

        Set<Integer> allExtraKeys = collectAllKeysInteger(extraList);

        for (Integer key : allExtraKeys) {
            List<V> paramValues = collectParamValuesInteger(key, extraList);
            for (int i = 0; i < paramValues.size(); i++) {
                if (paramValues.get(i) == null) {
                    paramValues.set(i, insteadOfNull.apply(i));
                }
            }
            if (!this.containsKey(key)) {
                V newValue = addFunction.apply(paramValues);
                if (newValue != null) {
                    storage.put(key, newValue);
                }
            } else {
                V oldValue = this.get(key);
                V newValue = replaceFunction.apply(oldValue, paramValues);
                if (newValue == null) {
                    this.remove(key);
                } else {
                    storage.put(key, newValue);
                }
            }
        }
        return this;
    }


    /**
     * Validates the proposed changes. <br>
     * If key do not exist in storage, checks checkFunctionIfNotExist(other[key])<br>
     * else checks checkFunctionIfExist(this[key], other[key])<br>
     * If any function equals null, then it is supposed to be always true
     * @return false if at least one check returned false
     */
    public boolean checkMergeElements(Map<Integer,V> other, Predicate<V> checkFunctionIfNotExist, BiPredicate<V, V> checkFunctionIfExist){
        if(checkFunctionIfNotExist != null && checkFunctionIfExist != null) {
            for (Map.Entry<Integer, V> e : other.entrySet()) {
                V thisValue = this.get(e.getKey());
                if (thisValue == null) {
                    if (!checkFunctionIfNotExist.test(e.getValue())) {
                        return false;
                    }
                } else {
                    if (!checkFunctionIfExist.test(thisValue, e.getValue())) {
                        return false;
                    }
                }
            }
        }
        else if(checkFunctionIfExist != null){
            for (Map.Entry<Integer, V> e : other.entrySet()) {
                V thisValue = this.get(e.getKey());
                if (thisValue != null) {
                    if (!checkFunctionIfExist.test(thisValue, e.getValue())) {
                        return false;
                    }
                }
            }
        }
        else if(checkFunctionIfNotExist != null){
            for (Map.Entry<Integer, V> e : other.entrySet()) {
                V thisValue = this.get(e.getKey());
                if (thisValue == null) {
                    if (!checkFunctionIfNotExist.test(e.getValue())) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Validates the proposed changes. <br>
     * If key do not exist in storage, checks checkFunctionIfNotExist(other[key]) <br>
     * else checks checkFunctionIfExist(this[key], other[key])<br>
     * If any function equals null, then it is supposed to be always true
     * @return false if at least one check returned false
     */
    public boolean checkMergeElements(List<? extends Map<Integer, V>> params, Predicate<List<V>> checkFunctionIfNotExist, BiPredicate<V, List<V>> checkFunctionIfExist) {
        Set<Integer> allKeys = collectAllKeysInteger(params);


        if(checkFunctionIfNotExist != null && checkFunctionIfExist != null) {
            for (Integer key : allKeys) {
                V thisValue = this.get(key);
                List<V> paramValues = collectParamValuesInteger(key, params);
                if(thisValue == null){
                    if (!checkFunctionIfNotExist.test(paramValues)) {
                        return false;
                    }
                }
                else {
                    if (!checkFunctionIfExist.test(thisValue, paramValues)) {
                        return false;
                    }
                }
            }
        }
        else if(checkFunctionIfExist != null){
            for (Integer key : allKeys) {
                V thisValue = this.get(key);
                List<V> paramValues = collectParamValuesInteger(key, params);
                if(thisValue != null){
                    if (!checkFunctionIfExist.test(thisValue, paramValues)) {
                        return false;
                    }
                }
            }
        }
        else if(checkFunctionIfNotExist != null){
            for (Integer key : allKeys) {
                V thisValue = this.get(key);
                List<V> paramValues = collectParamValuesInteger(key, params);
                if(thisValue == null){
                    if (!checkFunctionIfNotExist.test(paramValues)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Validates the proposed changes. <br>
     * If key do not exist in storage, checks checkFunctionIfNotExist(other[key]) <br>
     * else checks checkFunctionIfExist(this[key], other[key])<br>
     * If any function equals null, then it is supposed to be always true
     * Replaces all null parameters with insteadOfNull(paramNumber)
     * @return false if at least one check returned false
     */
    public boolean checkMergeElements(
        List<? extends Map<Integer, V>> params,
        Predicate<List<V>> checkFunctionIfNotExist, BiPredicate<V, List<V>> checkFunctionIfExist,
        Function<Integer, V> insteadOfNull) {
        Set<Integer> allKeys = collectAllKeysInteger(params);


        if(checkFunctionIfNotExist != null && checkFunctionIfExist != null) {
            for (Integer key : allKeys) {
                V thisValue = this.get(key);
                List<V> paramValues = collectParamValuesInteger(key, params);
                for (int i = 0; i < paramValues.size(); i++) {
                    if (paramValues.get(i) == null) {
                        paramValues.set(i, insteadOfNull.apply(i));
                    }
                }
                if(thisValue == null){
                    if (!checkFunctionIfNotExist.test(paramValues)) {
                        return false;
                    }
                }
                else {
                    if (!checkFunctionIfExist.test(thisValue, paramValues)) {
                        return false;
                    }
                }
            }
        }
        else if(checkFunctionIfExist != null){
            for (Integer key : allKeys) {
                V thisValue = this.get(key);
                List<V> paramValues = collectParamValuesInteger(key, params);
                if(thisValue != null){
                    if (!checkFunctionIfExist.test(thisValue, paramValues)) {
                        return false;
                    }
                }
            }
        }
        else if(checkFunctionIfNotExist != null){
            for (Integer key : allKeys) {
                V thisValue = this.get(key);
                List<V> paramValues = collectParamValuesInteger(key, params);
                if(thisValue == null){
                    if (!checkFunctionIfNotExist.test(paramValues)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }






    {

//    public MapHolder<K, V> merge(Map<K, V> extra) {
//        if (extra == null) return this;
//
//        for (Map.Entry<K, V> entry : extra.entrySet()) {
//            K key = entry.getKey();
//            // Those elements that already exist in this are not added
//            if (!this.containsKey(key)) {
//                this.put(key, entry.getValue());
//            }
//        }
//        return this;
//    }

    /**
     * Consecutive execution of merge(Map<K, V> extra) for a list of maps.
     *
     * @param extraList - source list of maps
     * @return this
     */
//    public MapHolder<K, V> merge(List<? extends Map<K, V>> extraList) {
//        if (extraList != null) {
//            for (Map<K, V> extra : extraList) {
//                this.merge(extra);
//            }
//        }
//        return this;
//    }

    // --- Check function ---

    /**
     * Validates the proposed changes.
     * Returns false if any key is missing in storage, or if the checkFunction fails.
     */
//    public boolean checkChangeElements(Map<K,V> other, BiPredicate<V, V> checkFunction){
//        for (Map.Entry<K,V> e : other.entrySet()) {
//            V thisValue = this.get(e.getKey());
//            if(thisValue == null) return false;
//            if (!checkFunction.test(thisValue, e.getValue())) {
//                return false;
//            }
//        }
//
//        return true;
//    }



    /**
     * Validates the proposed changes.
     * Returns false if any key is missing in storage, or if the checkFunction fails.
     */
//    public boolean checkChangeElements(List<? extends Map<K, V>> params, BiPredicate<V, List<V>> checkFunction) {
//        Set<K> allKeys = collectAllKeysType(params);
//
//        // 1. Ensure ALL keys from params exist in the this
//        for (K key : allKeys) {
//            if (!this.containsKey(key)) {
//                return false;
//            }
//        }
//
//        // 2. Apply the validation function for each key
//        for (K key : allKeys) {
//            V thisValue = this.get(key);
//            List<V> paramValues = collectParamValuesType(key, params);
//
//            if (!checkFunction.test(thisValue, paramValues)) {
//                return false;
//            }
//        }
//
//        return true;
//    }


    // --- Helper Methods ---

//    private Set<K> collectAllKeysType(List<? extends Map<K, V>> params) {
//        Set<K> allKeys = new HashSet<>();
//        if (params != null) {
//            for (Map<K, V> paramMap : params) {
//                if (paramMap != null) {
//                    allKeys.addAll(paramMap.keySet());
//                }
//            }
//        }
//        return allKeys;
//    }

//    private List<V> collectParamValuesType(K key, List<? extends Map<K, V>> params) {
//        List<V> paramValues = new ArrayList<>();
//        if (params != null) {
//            for (Map<K, V> paramMap : params) {
//                paramValues.add(paramMap != null ? paramMap.get(key) : null);
//            }
//        }
//        return paramValues;
//    }
}

    private Set<Integer> collectAllKeysInteger(List<? extends Map<Integer, V>> params) {
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

    private List<V> collectParamValuesInteger(Integer key, List<? extends Map<Integer, V>> params) {
        List<V> paramValues = new ArrayList<>();
        if (params != null) {
            for (Map<Integer, V> paramMap : params) {
                paramValues.add(paramMap != null ? paramMap.get(key) : null);
            }
        }
        return paramValues;
    }





    // --- Map Interface Delegation ---

    public void removeAllRefsToNotExistingObjects() {
        storage.keySet().removeIf(key -> !keyHolder.contains(key));
    }

    @Override public int size() { return storage.size(); }
    @Override public boolean isEmpty() { return storage.isEmpty(); }
    @Override public boolean containsKey(Object key) {
        if(key instanceof Integer)
            return storage.containsKey( key);
        if(key instanceof Identifiable)
            return storage.containsKey(((Identifiable) key) .getId());
        return false;
    }
    @Override public boolean containsValue(Object value) { return storage.containsValue(value); }
    @Override public V get(Object key) {
        if(key instanceof Integer)
            return storage.get( key);
        if(key instanceof Identifiable)
            return storage.get(((Identifiable) key) .getId());
        return null;
    }
    public V put(K key, V value) { return storage.put(key.getId(), value); }
    @Override public V put(Integer key, V value) {
        if(!keyHolder.contains(key)) throw new IllegalArgumentException("Now original object exist.");
        return storage.put(key, value);
    }
    @Override public V remove(Object key) {
        if(key instanceof Integer)
            return storage.remove( key);
        if(key instanceof Identifiable)
            return storage.remove(((Identifiable) key).getId());
        return null;
    }

    public void putAllIdentifiable(Map<? extends K, ? extends V> m) {
        if (m == null) {
            return;
        }
        for (Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override public void putAll(Map<? extends Integer, ? extends V> m) {
        if(m==null) return;
        for(Entry<? extends Integer, ? extends V> e : m.entrySet()){
            this.put(e.getKey(), e.getValue());
        }
    }
    @Override public void clear() { storage.clear(); }
    @Override public Set<Integer> keySet() {return storage.keySet();}
    public Set<K> realKeySet(){
        Set<K> res = new HashSet<>();
        for(Integer id : storage.keySet()){
            K k = keyHolder.get(id);
            if(k!=null) res.add(k);
        }
        return res;
    }
    @Override public Collection<V> values() { return storage.values(); }
    @Override public Set<Entry<Integer, V>> entrySet() {return storage.entrySet(); }
}
