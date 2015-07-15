package at.reisisoft.sigui.installation;

import at.reisisoft.sigui.OS;

import java.nio.file.Path;
import java.util.Optional;

/**
 * Created by Florian on 09.07.2015.
 */
public class LinuxRpmInstallationTqElement extends AbstractInstallationTqElement {
    @Override
    public Optional<InstallationProvider> getElementValue(OS from) {
        if (from == OS.LinuxRPM)
            return Optional.of(getInstallationProvider());
        return Optional.empty();
    }

    private InstallationProvider getInstallationProvider() {
        return new AbstractInstallationProvider(this) {
            @Override
            public boolean doInstallation(Path installer, Path installationFolder) throws InstallatioException {
                return false;   //TODO
            }
        };
    }
}
