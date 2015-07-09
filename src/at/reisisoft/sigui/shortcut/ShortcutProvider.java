package at.reisisoft.sigui.shortcut;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by Florian on 08.07.2015.
 */
@FunctionalInterface
public interface ShortcutProvider {

    /**
     * @param path        Path to a folder in which the shortcut should be created
     * @param name        Name of the shortcut
     * @param destination The destination of he URL link
     * @param comment     If possible, have a comment / tooltip associated with the shortcut
     * @throws IOException
     */
    void createShortcut(Path path, String name, Path destination, String comment) throws IOException;
}
