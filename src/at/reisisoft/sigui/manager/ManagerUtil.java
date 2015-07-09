package at.reisisoft.sigui.manager;

import at.reisisoft.sigui.collection.CollectionHashMap;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.Collections;
import java.util.TreeSet;

/**
 * Created by Florian on 09.07.2015.
 */
public class ManagerUtil {

    public static Collection<CollectionHashMap.KeyValuePair<String, Path>> scanForsoffice(Path start) throws IOException {
        Collection<CollectionHashMap.KeyValuePair<String, Path>> set = Collections.synchronizedSet(new TreeSet<>((a, b) -> a.getKey().compareTo(b.getKey())));
        FileVisitor<Path> fileVisitor = new FileVisitor<Path>() {
            private final String BASE = "registrymodifications.xcu";

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (attrs.isRegularFile()) {
                    if (BASE.equals(file.toFile().getName())) {
                        Path folder = file.getParent().getParent();
                        CollectionHashMap.KeyValuePair<String, Path> keyValuePair = new CollectionHashMap.KeyValuePair<>();
                        keyValuePair.setValue(folder);
                        String folderName = folder.toFile().getName();
                        keyValuePair.setKey(folderName);
                        set.add(keyValuePair);
                        return FileVisitResult.SKIP_SIBLINGS;
                    }
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                return FileVisitResult.SKIP_SUBTREE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                if (exc != null)
                    return FileVisitResult.SKIP_SUBTREE;
                return FileVisitResult.CONTINUE;
            }
        };
        Files.walkFileTree(start, fileVisitor);
        return set;
    }
}
