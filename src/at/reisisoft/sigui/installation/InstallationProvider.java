package at.reisisoft.sigui.installation;

import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * Created by Florian on 09.07.2015.
 */
public interface InstallationProvider {

    void install(Path installer, Path installationFolder) throws InstallatioException;


    void andAfter(Consumer<Path> after);
}
