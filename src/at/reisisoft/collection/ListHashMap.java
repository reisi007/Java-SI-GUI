package at.reisisoft.collection;

import java.util.*;

/**
 * Created by Florian on 24.06.2015.
 */
public class ListHashMap<K, V> implements CollectionHashMap<K, List<V>, V> {
    private HashMap<K, List<V>> map;

    public ListHashMap() {
        map = new HashMap<>();
    }

    public Optional<List<V>> get(Object o) {
        return Optional.ofNullable(map.get(o));
    }

    public boolean put(K key, V value) {
        return map.computeIfAbsent(key, k -> new LinkedList<>()).add(value);
    }

    @Override
    public Set<K> getKeySet() {
        return map.keySet();
    }


    public int size() {
        return map.values().stream().mapToInt(List<V>::size).sum();
    }
}
