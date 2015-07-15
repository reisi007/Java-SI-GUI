package at.reisisoft.sigui.installation;

import java.nio.file.Path;

/**
 * Created by Florian on 09.07.2015.
 */
public abstract class AbstractInstallationProvider implements InstallationProvider {
    protected final AbstractInstallationTqElement tqElement;

    public AbstractInstallationProvider(AbstractInstallationTqElement tqElement) {
        this.tqElement = tqElement;
    }

    @Override
    public final boolean install(Path installer, Path installationFolder) throws InstallatioException {
        return doInstallation(installer, installationFolder);
    }

    public abstract boolean doInstallation(Path installer, Path installationFolder) throws InstallatioException;
}
