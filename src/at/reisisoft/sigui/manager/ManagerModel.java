package at.reisisoft.sigui.manager;

import at.reisisoft.sigui.collection.AbstractCollectionHashMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

/**
 * Created by Florian on 08.07.2015.
 */
public class ManagerModel extends AbstractCollectionHashMap<String, ObservableList<Path>, Path> {

    public ObservableList<KeyValuePair<String, ObservableList<Path>>> getObservableList() {
        return observableList;
    }

    private ObservableList<KeyValuePair<String, ObservableList<Path>>> observableList = FXCollections.observableArrayList();

    public ManagerModel() {
        this(Collections.emptyMap());
    }

    public ManagerModel(Map<String, ObservableList<Path>> values) {
        super((s) -> FXCollections.observableArrayList());
        values.keySet().stream().forEach(key -> {
            put(key, values.get(key));
        });
        observableList.size();
    }

    private void updateObservableList(String key) {
        ObservableList<Path> oList = map.get(key);
        observableList.removeIf(kvp -> key.equals(kvp.getKey()));
        if (oList.size() > 0)
            observableList.add(new KeyValuePair<>(key, oList));
    }

    @Override
    public boolean put(String key, Path value) {
        Optional<List<Path>> optional = Optional.ofNullable(map.get(key));
        boolean doit = true;
        if (optional.isPresent())
            doit = !optional.get().contains(value);
        if (doit) {
            boolean b = super.put(key, value);
            if (b) updateObservableList(key);
            return b;
        } else return true;
    }

    @Override
    public boolean put(String key, Collection<Path> values) {
        Optional<List<Path>> optional = Optional.ofNullable(map.get(key));
        if (optional.isPresent()) {
            List<Path> helper = optional.get();
            Path[] paths = values.parallelStream().filter(e -> !helper.contains(e)).toArray(Path[]::new);
            boolean b = true, update = false;
            for (Path p : paths) {
                b = b & (update = update | super.put(key, p));
            }
            if (update)
                updateObservableList(key);
            return b;
        }
        boolean b = super.put(key, values);
        if (b) updateObservableList(key);
        return b;

    }

    @Override
    public ObservableList<Path> remove(Object key) {
        ObservableList<Path> list = super.remove(key);
        observableList.removeIf(kvp -> key.equals(kvp.getKey()));
        FileVisitor<Path> fileVisitor = new FileVisitor<Path>() {
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
                return FileVisitResult.SKIP_SUBTREE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                try {
                    Files.delete(dir);
                } catch (FileNotFoundException fnf) {
                    return FileVisitResult.SKIP_SUBTREE;
                }
                return FileVisitResult.CONTINUE;
            }
        };
        for (Path p : list)
            try {
                Files.walkFileTree(p, fileVisitor);
            } catch (IOException e) {
                e.printStackTrace();
            }
        return list;
    }
}
