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
            public void doInstallation(Path installer, Path installationFolder) throws InstallatioException {
                String base = "msiexec /qr /norestart /a \"%s\" TARGETDIR =\"%s\"";
                String finalStartString = String.format(base, installer, installationFolder);
                System.out.println("Installing with cmd \"" + finalStartString + '"');
                try {
                    Runtime.getRuntime().exec(finalStartString).waitFor();
                } catch (Exception e) {
                    throw new InstallatioException(e);
                }
            }
        };
    }
}
