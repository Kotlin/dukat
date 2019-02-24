package org.jetbrains.dukat.nashorn;

import java.util.HashMap;

public class Map<K, V> {

    private HashMap<K, V> hashMap = new HashMap<>();

    public Map() {
    }

    public void clear() {
        hashMap.clear();
    }

    public Boolean delete(K key) {
        return hashMap.remove(key) != null;
    }

    public V get(K key) {
        return hashMap.get(key);
    }

    public Boolean has(K key) {
        return hashMap.containsKey(key) && (hashMap.get(key) != null);
    }

    public Map<K, V> set(K key, V value) {
        hashMap.put(key, value);
        return this;
    }
}
