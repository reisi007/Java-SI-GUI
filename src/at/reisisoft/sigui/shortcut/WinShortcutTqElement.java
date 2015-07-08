package at.reisisoft.sigui.shortcut;

import at.reisisoft.sigui.OS;
import net.jimmc.jshortcut.JShellLink;

import java.util.Optional;

/**
 * Created by Florian on 08.07.2015.
 */
public class WinShortcutTqElement extends AbstractShortcutTqElement {
    @Override
    public Optional<ShortcutProvider> getElementValue(OS from) {
        if (!from.isWindows())
            return Optional.empty();
        return Optional.of((path, name, destination, comment) -> {
            JShellLink link = new JShellLink();
            String source = path.toFile().toString();
            link.setFolder(source);
            link.setWorkingDirectory(source);
            link.setName(name);
            link.setDescription(comment);
            link.setIconLocation(destination.toFile().toString());
            link.setPath(destination.toFile().toString());
            link.save();
        });
    }
}
