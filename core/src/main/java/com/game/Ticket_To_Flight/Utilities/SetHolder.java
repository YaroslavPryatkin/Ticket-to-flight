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
    public T remove(Integer id) {return storage.remove(id);}

    public T get(T item) {
        return (item == null) ? null : storage.get(item.getId());
    }

    public boolean contains(Integer id){
        return storage.containsKey(id);
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
     * Applies a modifying function to a specific field of the objects.
     * Useful for mutable field objects (like internal collections or wrappers).
     * @return this
     */
    public <V> SetHolder<T> changeAsStruct(
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
        return this;
    }

    /**
     * Applies a function to calculate a new value for a field,
     * then uses the setter to assign it back to the object.
     * Useful for immutable fields (like Integer, String, Enum).
     * @return this
     */
    public <V> SetHolder<T> changeAsStructWithSetter(
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
        return this;
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
     * Adds all elements that this does not contain
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
     * Adds addFunction(otherElem) for all elements not in this
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
     * For every element in the union of extraList a List params is created
     * Adds addFunction(params) for all elements not in this
     * Replaces with replaceFunction(old, params) for all elements already in this
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
     * Iterates over 'other' and checks pairs (this.get(id), otherElem).
     * Note: This implementation only checks elements present in 'other'.
     * @param other - source
     * @param checkFunction - predicate to validate pairs
     * @return true if all pairs pass the check, false otherwise
     */
    public boolean check(Set<T> other, BiPredicate<T, T> checkFunction) {
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
    public boolean check(List<SetHolder<T>> other, BiPredicate<T, List<T>> checkFunction) {
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

    // --- ChangeSet Methods ---

    /**
     * Synchronizes the storage using the 3-step logic.
     * This method creates internal Holder copies to avoid modifying original input sets.
     */
    public SetHolder<T> changeSet(Set<T> toAdd, Set<T> toRemove) {
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
        return this;
    }

    /**
     * Validates if the change can be performed.
     */
    public boolean checkChangeSet(Set<T> toAdd, Set<T> toRemove) {
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
