package at.reisisoft.sigui;

import at.reisisoft.sigui.collection.AbstractCollectionHashMap;
import at.reisisoft.sigui.collection.CollectionHashMap;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
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

    public static <T> Collector<Stream<T>, Stream.Builder<T>, Stream<T>> reduceStreamOfStreams() {
        return new Collector<Stream<T>, Stream.Builder<T>, Stream<T>>() {
            @Override
            public Supplier<Stream.Builder<T>> supplier() {
                return Stream::builder;
            }

            @Override
            public BiConsumer<Stream.Builder<T>, Stream<T>> accumulator() {
                return (tBuilder, tStream) -> tStream.forEach(tBuilder::add);
            }

            @Override
            public BinaryOperator<Stream.Builder<T>> combiner() {
                return (tBuilder, tBuilder2) -> {
                    tBuilder.build().forEach(tBuilder2::add);
                    return tBuilder2;
                };
            }

            @Override
            public Function<Stream.Builder<T>, Stream<T>> finisher() {
                return Stream.Builder::build;
            }

            @Override
            public Set<Characteristics> characteristics() {
                return Collections.emptySet();
            }
        };
    }

    public static <T> Iterable<T> iterableFromStream(Stream<T> stream) {
        return stream::iterator;
    }

    public static <K, V, T extends Collection<V>> Collector<CollectionHashMap.KeyValuePair<K, V>, CollectionHashMap<K, T, V>, CollectionHashMap<K, T, V>> collectToCollectionHashmap(Supplier<T> valueStorage) {
        return new Collector<CollectionHashMap.KeyValuePair<K, V>, CollectionHashMap<K, T, V>, CollectionHashMap<K, T, V>>() {
            @Override
            public Supplier<CollectionHashMap<K, T, V>> supplier() {
                return () -> new AbstractCollectionHashMap<K, T, V>(k -> valueStorage.get()) {
                };
            }

            @Override
            public BiConsumer<CollectionHashMap<K, T, V>, CollectionHashMap.KeyValuePair<K, V>> accumulator() {
                return CollectionHashMap<K, T, V>::put;
            }

            @Override
            public BinaryOperator<CollectionHashMap<K, T, V>> combiner() {
                return new BinaryOperator<CollectionHashMap<K, T, V>>() {
                    @Override
                    public CollectionHashMap<K, T, V> apply(CollectionHashMap<K, T, V> ktvCollectionHashMap, CollectionHashMap<K, T, V> ktvCollectionHashMap2) {
                        Set<K> keySet = ktvCollectionHashMap2.getKeySet();
                        for (K key : keySet) {
                            T colVal = ktvCollectionHashMap2.get(key).get(); //This is save
                            colVal.forEach(v -> ktvCollectionHashMap.put(key, v));
                        }
                        return ktvCollectionHashMap;
                    }
                };
            }

            @Override
            public Function<CollectionHashMap<K, T, V>, CollectionHashMap<K, T, V>> finisher() {
                return Function.identity();
            }

            @Override
            public Set<Characteristics> characteristics() {
                return new HashSet<>(Arrays.asList(Characteristics.CONCURRENT));
            }
        };
    }

    public static ResourceBundle.Control getUTFRessourceBundleControl() {
        return new ResourceBundle.Control() {
            @Override
            public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws IllegalAccessException, InstantiationException, IOException {
                String ressourceName = toResourceName(toBundleName(baseName, locale), "properties");
                Reader reader = null;
                Charset charset = StandardCharsets.UTF_8;
                if (reload) {
                    URL urlConnection = loader.getResource(ressourceName);
                    if (urlConnection == null)
                        return null;
                    reader = new InputStreamReader(urlConnection.openStream(), charset);
                } else reader = new InputStreamReader(loader.getResourceAsStream(ressourceName), charset);

                ResourceBundle bundle = new PropertyResourceBundle(reader);
                reader.close();
                return bundle;
            }
        };
    }

    public static <T> String toString(List<T> list, Function<T, String> toStringMapper) {
        String[] strings = new String[list.size()];
        for (int i = 0; i < strings.length; i++)
            strings[i] = toStringMapper.apply(list.get(i));
        return Arrays.toString(strings);
    }

    public static <T> String toString(T[] array, Function<T, String> toStringMapper) {
        String[] strings = new String[array.length];
        for (int i = 0; i < array.length; i++)
            strings[i] = toStringMapper.apply(array[i]);
        return Arrays.toString(strings);
    }

    public static void deleteFolder(Path folder) throws IOException {
        Files.walkFileTree(folder, new FileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
