package com.game.Ticket_To_Flight.Utilities;

import java.util.*;
import java.util.function.*;

/**
 * A Set implementation that uses an internal Map for O(1) access by ID.
 */
public class SetHolder<T extends Identifiable> implements Set<T> {
    private final Map<Integer, T> storage = new HashMap<>();


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

    // --- Struct Modifier Methods ---

    /**
     * Applies a modifying function to a specific field of the objects.
     * Useful for mutable field objects (like internal collections or wrappers).
     */
    public <V> void changeAsStruct(
        Function<T, V> getField,
        List<Map<T, V>> params,
        BiConsumer<V, List<V>> function
    ) {
        Set<T> allKeys = collectAllKeys(params);

        for (T key : allKeys) {
            // Get the actual stored instance using the ID of the key
            T realItem = this.get(key);
            if (realItem != null) {
                V currentValue = getField.apply(realItem);
                List<V> paramValues = collectParamValues(key, params);

                // Mutate the inner structure of the field
                function.accept(currentValue, paramValues);
            }
        }
    }

    /**
     * Applies a function to calculate a new value for a field,
     * then uses the setter to assign it back to the object.
     * Useful for immutable fields (like Integer, String, Enum).
     */
    public <V> void changeAsStructWithSetter(
        BiConsumer<T, V> setter, // Takes the target object and the new value
        Function<T, V> getField,
        List<Map<T, V>> params,
        BiFunction<V, List<V>, V> function
    ) {
        Set<T> allKeys = collectAllKeys(params);

        for (T key : allKeys) {
            T realItem = this.get(key);
            if (realItem != null) {
                V currentValue = getField.apply(realItem);
                List<V> paramValues = collectParamValues(key, params);

                // Calculate new value and assign it using the setter
                V newValue = function.apply(currentValue, paramValues);
                setter.accept(realItem, newValue);
            }
        }
    }

    /**
     * Validates the proposed structural changes.
     * Returns false if any affected object is missing from storage,
     * or if the checkFunction fails for any object.
     */
    public <V> boolean checkChangeAsStruct(
        Function<T, V> getField,
        List<Map<T, V>> params,
        BiPredicate<V, List<V>> checkFunction
    ) {
        Set<T> allKeys = collectAllKeys(params);

        for (T key : allKeys) {
            T realItem = this.get(key);
            // 1. All objects affected by params must exist in the current storage
            if (realItem == null) {
                return false;
            }

            V currentValue = getField.apply(realItem);
            List<V> paramValues = collectParamValues(key, params);

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
    private <V> Set<T> collectAllKeys(List<Map<T, V>> params) {
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

    /**
     * Extracts values for a specific key from all parameter maps.
     * Maintains the same index order as the params list.
     */
    private <V> List<V> collectParamValues(T key, List<Map<T, V>> params) {
        List<V> paramValues = new ArrayList<>();
        if (params != null) {
            for (Map<T, V> paramMap : params) {
                paramValues.add(paramMap != null ? paramMap.get(key) : null);
            }
        }
        return paramValues;
    }

    // --- Core Logic Methods ---

    /**
     * Synchronizes the storage using the 3-step logic.
     * This method creates internal Holder copies to avoid modifying original input sets.
     */
    public void change(Set<T> toAdd, Set<T> toRemove) {
        // We wrap inputs into new Holders to perform logic without side effects
        SetHolder<T> addSetHolder = new SetHolder<>(toAdd);
        SetHolder<T> removeSetHolder = new SetHolder<>(toRemove);

        // Step 1: Elements in both toRemove and storage are removed from both.
        Iterator<T> removeIter = removeSetHolder.iterator();
        while (removeIter.hasNext()) {
            T item = removeIter.next();
            if (this.contains(item)) {
                this.remove(item);      // Removed from this storage
                removeIter.remove();    // Removed from local removeHolder copy
            }
        }

        // Step 2: Remaining elements in toRemove are subtracted from toAdd.
        if (!removeSetHolder.isEmpty() && !addSetHolder.isEmpty()) {
            addSetHolder.removeAll(removeSetHolder);
        }

        // Step 3: Remaining elements in toAdd are added to storage.
        this.addAll(addSetHolder);
    }

    /**
     * Validates if the change can be performed.
     * Uses standard Set methods since Identifiable implements equals/hashCode via ID and Class.
     */
    public boolean checkChange(Set<T> toAdd, Set<T> toRemove) {
        if (toRemove != null) {
            for (T item : toRemove) {
                // Step 1: Must exist in storage OR in toAdd
                boolean inStorage = this.contains(item);
                boolean inAdd = (toAdd != null && toAdd.contains(item));
                if (!inStorage && !inAdd) return false;
            }
        }

        if (toAdd != null) {
            for (T item : toAdd) {
                // Step 2: Cannot exist in storage unless marked for removal
                if (this.contains(item)) {
                    if (toRemove == null || !toRemove.contains(item)) {
                        return false;
                    }
                }
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
        return (o instanceof Identifiable) && storage.containsKey(((Identifiable) o).getId());
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
