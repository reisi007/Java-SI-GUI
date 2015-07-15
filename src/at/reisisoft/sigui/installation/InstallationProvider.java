package at.reisisoft.sigui.installation;

import java.nio.file.Path;

/**
 * Created by Florian on 09.07.2015.
 */
public interface InstallationProvider {

    boolean install(Path installer, Path installationFolder) throws InstallatioException;

}
