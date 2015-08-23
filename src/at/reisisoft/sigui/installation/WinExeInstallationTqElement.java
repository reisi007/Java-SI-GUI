package at.reisisoft.sigui.installation;

import at.reisisoft.sigui.OS;
import at.reisisoft.sigui.Utils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Created by Florian on 09.07.2015.
 */
public class WinExeInstallationTqElement extends AbstractInstallationTqElement {
    @Override
    public Optional<InstallationProvider> getElementValue(OS from) {
        if (from == OS.WinExe)
            return Optional.of(getInstallationProvider());
        return Optional.empty();
    }

    private InstallationProvider getInstallationProvider() {
        return new AbstractInstallationProvider(this) {
            @Override
            public boolean doInstallation(Path installer, Path installationFolder) throws InstallatioException {
                Path msiFile = null;
                Path tempDirectory = null;
                //TODO extract EXE
                boolean b = true;
                if (b)
                    return false;
                Optional<InstallationProvider> installationProvider = getHeadOfQueue().getValue(OS.WinMsi);
                if (installationProvider.isPresent()) {
                    boolean working = installationProvider.get().install(msiFile, installationFolder);
                    try {
                        if (tempDirectory != null)
                            Utils.deleteFolder(tempDirectory);
                        return working;
                    } catch (IOException e) {
                        e.printStackTrace();
                        return false;
                    }

                } else return false;

            }
        };
    }
}
