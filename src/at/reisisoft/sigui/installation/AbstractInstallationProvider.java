package at.reisisoft.sigui.installation;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * Created by Florian on 09.07.2015.
 */
public abstract class AbstractInstallationProvider implements InstallationProvider {
    private Consumer<Path> consumer = null;

    @Override
    public final void install(Path installer, Path installationFolder) throws IOException {
        doInstallation(installer, installationFolder);
        if (consumer != null)
            consumer.accept(installationFolder);
    }

    /**
     * @param after A consumer which should be called after installation or {@literal null} to reset the consumer. The accepted path is the installation folder
     */
    @Override
    public final void andAfter(Consumer<Path> after) {
        consumer = after;
    }

    public abstract void doInstallation(Path installer, Path installationFolder) throws IOException;
}
