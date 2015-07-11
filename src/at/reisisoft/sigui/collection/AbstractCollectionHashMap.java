package at.reisisoft.sigui.collection;

import java.util.*;
import java.util.function.Function;

/**
 * Created by Florian on 08.07.2015.
 */
public class AbstractCollectionHashMap<K, C extends Collection<V>, V> implements CollectionHashMap<K, C, V> {
    protected HashMap<K, C> map;
    private Function<K, C> entryNotExistFunction;

    public AbstractCollectionHashMap(Function<K, C> createCollectionFunction) {
        map = new HashMap<>();
        entryNotExistFunction = createCollectionFunction;
    }

    public AbstractCollectionHashMap(Function<K, C> createCollectionFunction, Map<K, C> values) {
        this(createCollectionFunction);
        map.entrySet().stream().forEach(kvp -> put(kvp.getKey(), kvp.getValue()));
    }


    @Override
    public Optional<C> get(Object o) {
        return Optional.ofNullable(map.get(o));
    }

    @Override
    public boolean put(K key, V value) {
        return map.computeIfAbsent(key, entryNotExistFunction).add(value);
    }

    @Override
    public Set<K> getKeySet() {
        return map.keySet();
    }

    @Override
    public C remove(Object key) {
        return map.remove(key);
    }

    @Override
    public boolean put(K key, Collection<V> values) {
        return map.computeIfAbsent(key, entryNotExistFunction).addAll(values);
    }

    @Override
    public int size() {
        return map.values().stream().mapToInt(Collection::size).sum();
    }

    @Override
    public Map<K, C> toCollection() {
        return Collections.unmodifiableMap(map);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("CollectionHashmap[");
        for (K key : map.keySet())
            sb.append(key).append(" => ").append(map.get(key));
        return sb.append(']').toString();
    }
}
