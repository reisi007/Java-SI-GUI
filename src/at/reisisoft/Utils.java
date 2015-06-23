package at.reisisoft;

import java.util.*;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

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
}
