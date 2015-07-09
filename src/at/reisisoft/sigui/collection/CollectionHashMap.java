package at.reisisoft.sigui.collection;

import java.io.Serializable;
import java.util.*;

/**
 * Created by Florian on 24.06.2015.
 */
public interface CollectionHashMap<K, C extends Collection<V>, V> {

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
