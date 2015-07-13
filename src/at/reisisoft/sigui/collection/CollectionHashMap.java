package at.reisisoft.sigui.collection;

import java.io.Serializable;
import java.util.*;

/**
 * Created by Florian on 24.06.2015.
 */
public interface CollectionHashMap<K, C extends Collection<V>, V> {
    /**
     * @param <K> The key of the Hashmap
     * @param <C> The collecion used to store the values
     * @param <V> The value
     * @return An immuteable, empty Collectionhashmap
     */
    static <K, C extends Collection<V>, V> CollectionHashMap<K, C, V> empty() {
        return new CollectionHashMap<K, C, V>() {
            @Override
            public Optional<C> get(Object o) {
                return Optional.empty();
            }

            @Override
            public boolean put(K key, V value) {
                return false;
            }

            @Override
            public Set<K> getKeySet() {
                return Collections.emptySet();
            }

            @Override
            public C remove(Object key) {
                return null;
            }

            @Override
            public int size() {
                return 0;
            }

            @Override
            public Map<K, C> toCollection() {
                return Collections.emptyMap();
            }

            @Override
            public String toString() {
                return "CollectionHashmap[]";
            }
        };
    }


    Optional<C> get(Object o);

    boolean put(K key, V value);

    Set<K> getKeySet();

    default boolean put(KeyValuePair<K, V> kvp) {
        return put(kvp.getKey(), kvp.getValue());
    }

    default boolean put(KeyValuePair<K, V>... kvp) {
        boolean b = true;
        for (KeyValuePair<K, V> k : kvp)
            b = b && put(k.getKey(), k.getValue());
        return b;
    }

    default boolean put(Collection<? extends KeyValuePair<K, V>> collection) {
        boolean b = true;
        for (KeyValuePair<K, V> k : collection)
            b = b && put(k.getKey(), k.getValue());
        return b;
    }

    default boolean put(K key, V[] values) {
        return put(key, Arrays.asList(values));
    }

    default boolean put(K key, Collection<V> values) {
        boolean worked = true;
        for (V value : values)
            worked = worked & put(key, value);
        return worked;
    }

    default void put(CollectionHashMap<? extends K, ? extends C, ? extends V> another) {
        for (K key : another.getKeySet()) {
            Optional<? extends C> optional = another.get(key);
            if (optional.isPresent())
                put(key, optional.get());
        }
    }

    C remove(Object key);

    int size();

    Map<K, C> toCollection();

    class KeyValuePair<K, V> implements Serializable {
        private K key;
        private V value;

        public KeyValuePair() {
            this(null, null);
        }

        public KeyValuePair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public void setKey(K key) {
            this.key = key;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.format("(%s->%s)", key, value);
        }

    }
}
