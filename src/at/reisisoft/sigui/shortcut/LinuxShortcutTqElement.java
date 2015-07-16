package at.reisisoft.sigui.shortcut;

import at.reisisoft.sigui.OS;

import java.io.BufferedWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Created by Florian on 08.07.2015.
 */
public class LinuxShortcutTqElement extends AbstractShortcutTqElement {
    @Override
    public Optional<ShortcutProvider> getElementValue(OS from) {
        if (!from.isLinux())
            return Optional.empty();
        return Optional.of((path, name, destination, comment) -> {
            StringBuilder sb = new StringBuilder("[Desktop Entry]").append('\n');
            sb.append("Encoding=UTF-8").append('\n');
            sb.append("Name=").append(name).append('\n');
            sb.append("Exec=").append(destination.toFile().toString()).append('\n');
            sb.append("Terminal=false").append('\n');
            sb.append("Type=Application").append('\n');
            sb.append("Comment=").append(comment);
            Path dest = path.resolve(name + ".desktop");
            try (BufferedWriter writer = Files.newBufferedWriter(dest, Charset.forName("utf-8"))) {
                writer.write(sb.toString());
            }
            return dest;
        });

    }


}
