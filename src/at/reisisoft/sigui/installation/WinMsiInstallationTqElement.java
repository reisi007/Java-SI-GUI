package at.reisisoft.sigui.installation;

import at.reisisoft.sigui.OS;

import java.nio.file.Path;
import java.util.Optional;

/**
 * Created by Florian on 09.07.2015.
 */
public class WinMsiInstallationTqElement extends AbstractInstallationTqElement {
    @Override
    public Optional<InstallationProvider> getElementValue(OS from) {
        if (from == OS.WinMsi)
            return Optional.of(getInstallationProvider());
        return Optional.empty();
    }

    private InstallationProvider getInstallationProvider() {
        return new AbstractInstallationProvider(this) {
            @Override
            public boolean doInstallation(Path installer, Path installationFolder) throws InstallatioException {
                String base = "\"%s\"";
                String finalStartString = String.format(base, installer, installationFolder);
                try {
                    ProcessBuilder pb = new ProcessBuilder("msiexec", "/qr", "/a", String.format(base, installer), "TARGETDIR=" + String.format(base, installationFolder));
                    Process p = pb.start();
                    p.waitFor();
                    int exitValue = p.exitValue();
                    return exitValue == 0;
                } catch (Exception e) {
                    throw new InstallatioException(e);
                }
            }
        };
    }
}
