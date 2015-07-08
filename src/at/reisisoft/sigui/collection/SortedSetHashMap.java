package at.reisisoft.sigui.collection;

import java.io.Serializable;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by Florian on 24.06.2015.
 */
public class SortedSetHashMap<K, V extends Comparable<V>> extends AbstractCollectionHashMap<K, SortedSet<V>, V> implements Serializable {


    public SortedSetHashMap() {
        super(k -> new TreeSet<>());
    }

    public SortedSetHashMap(Map<K, SortedSet<V>> values) {
        super(k -> new TreeSet<>(), values);
    }
}
