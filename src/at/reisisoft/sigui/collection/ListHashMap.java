package at.reisisoft.sigui.collection;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Florian on 24.06.2015.
 */
public class ListHashMap<K, V> extends AbstractCollectionHashMap<K, List<V>, V> implements Serializable {

    public ListHashMap() {
        super(k -> new LinkedList<>());
    }

    public ListHashMap(Map<K, List<V>> values) {
        super(k -> new LinkedList<>(), values);
    }
}
