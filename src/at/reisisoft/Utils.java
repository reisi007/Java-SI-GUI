package at.reisisoft;

import java.util.*;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

public class Utils {

    public static <T> Function<Future<T>, T> mapFuture() {
        return collectionFuture -> {
            try {
                return collectionFuture.get();
            } catch (Exception e) {
                return (T) Collections.emptyList();
            }
        };
    }


    public static <T> Collector<Collection<T>, Collection<T>, Collection<T>> collectCollectionsToSingleCollection() {
        return new Collector<Collection<T>, Collection<T>, Collection<T>>() {
            @Override
            public Supplier<Collection<T>> supplier() {
                return () -> Collections.synchronizedCollection(new LinkedList<T>());
            }

            @Override
            public BiConsumer<Collection<T>, Collection<T>> accumulator() {
                return Collection::addAll;
            }

            @Override
            public BinaryOperator<Collection<T>> combiner() {
                return (a, b) -> {
                    a.addAll(b);
                    return b;
                };

            }

            @Override
            public Function<Collection<T>, Collection<T>> finisher() {
                return e -> new ArrayList<>(e);
            }

            @Override
            public Set<Characteristics> characteristics() {
                return new HashSet<>(Arrays.asList(Characteristics.CONCURRENT, Characteristics.UNORDERED));
            }
        };
    }

    public static <T> Collector<Collection<T>, Collection<T>, Stream<T>> collectCollectionToStream() {
        return new Collector<Collection<T>, Collection<T>, Stream<T>>() {
            @Override
            public Supplier<Collection<T>> supplier() {
                return LinkedList::new;
            }

            @Override
            public BiConsumer<Collection<T>, Collection<T>> accumulator() {
                return Collection::addAll;
            }

            @Override
            public BinaryOperator<Collection<T>> combiner() {
                return (a, b) -> {
                    a.addAll(b);
                    return a;
                };
            }

            @Override
            public Function<Collection<T>, Stream<T>> finisher() {
                return Collection::parallelStream;
            }

            @Override
            public Set<Characteristics> characteristics() {
                return new HashSet<>(Arrays.asList(Characteristics.CONCURRENT, Characteristics.UNORDERED));
            }
        };

    }

    public static class KeyValuePair<K, V> {
        private K key;
        private V value;

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
    }
}
