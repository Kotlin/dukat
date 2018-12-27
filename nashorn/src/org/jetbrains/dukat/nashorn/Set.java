package org.jetbrains.dukat.nashorn;

import java.util.HashSet;

public class Set<T> {
    public int size = 0;

    private HashSet<T> hashSet = new HashSet<>();

    public Set() {
    }

    public Set(T[] elements) {
        for (T element : elements) {
            hashSet.add(element);
        }
    }

    public Set<T> add(T value) {
        hashSet.add(value);
        return this;
    }

    public void clear() {
        hashSet.clear();
    }

    public boolean delete(T value) {
        return hashSet.remove(value);
    }

    public boolean has(T value) {
        return hashSet.contains(value);
    }

    @FunctionalInterface
    public interface SetForeachCallBack<D> {
        void apply(D value, D value2, Set<D> set);
    }

    public void forEach(SetForeachCallBack<T> callBack) {
        for (T value : hashSet) {
            callBack.apply(value, value, this);
        }
    }
}