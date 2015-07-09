package at.reisisoft.sigui.installation;

import at.reisisoft.sigui.OS;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Created by Florian on 09.07.2015.
 */
public class WinMsiInstallationTqElement extends AbstractInstallationTqElement {
    @Override
    public Optional<InstallationProvider> getElementValue(OS from) {
        if (from == OS.Win)
            return Optional.of(getInstallationProvider());
        return Optional.empty();
    }

    private InstallationProvider getInstallationProvider() {
        return new AbstractInstallationProvider() {
            @Override
            public void doInstallation(Path installer, Path installationFolder) throws IOException {
                //TODO
            }
        };
    }
}
