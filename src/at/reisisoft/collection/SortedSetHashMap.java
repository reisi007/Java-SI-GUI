package at.reisisoft.collection;

import java.util.*;

/**
 * Created by Florian on 24.06.2015.
 */
public class SortedSetHashMap<K, V extends Comparable<V>> implements CollectionHashMap<K, SortedSet<V>, V> {
    private HashMap<K, SortedSet<V>> map;

    public SortedSetHashMap() {
        map = new HashMap<>();
    }

    public Optional<SortedSet<V>> get(Object o) {
        return Optional.ofNullable(map.get(o));
    }

    public boolean put(K key, V value) {
        return map.computeIfAbsent(key, k -> new TreeSet<>()).add(value);
    }

    @Override
    public Set<K> getKeySet() {
        return map.keySet();
    }

    public int size() {
        return map.values().stream().mapToInt(SortedSet<V>::size).sum();
    }
}
