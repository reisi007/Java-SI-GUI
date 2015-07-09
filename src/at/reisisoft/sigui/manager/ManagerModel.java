package at.reisisoft.sigui.manager;

import at.reisisoft.sigui.collection.ListHashMap;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

/**
 * Created by Florian on 08.07.2015.
 */
public class ManagerModel extends ListHashMap<String, Path> {

    @Override
    public List<Path> remove(Object key) {

        List<Path> list = super.remove(key);
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
                if (exc != null)
                    exc.printStackTrace();
                return FileVisitResult.SKIP_SUBTREE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                if (exc != null)
                    exc.printStackTrace();
                Files.delete(dir);
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
