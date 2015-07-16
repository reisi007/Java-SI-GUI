package at.reisisoft.sigui.ui;

import at.reisisoft.sigui.collection.CollectionHashMap;
import at.reisisoft.sigui.l10n.ExceptionTranslation;
import at.reisisoft.sigui.l10n.LocalisationSupport;
import at.reisisoft.sigui.settings.SiGuiSettings;
import at.reisisoft.sigui.shortcut.ShortcutProviders;
import at.reisisoft.sigui.ui.main.MainUi;
import at.reisisoft.sigui.ui.main.MainUiManagerTab;
import javafx.application.Platform;
import javafx.stage.Window;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Created by Florian on 15.07.2015.
 */
public class AdditionalFunctions {

    public static Consumer<CollectionHashMap.KeyValuePair<String, Path>> addToManager(LocalisationSupport localisationSupport, Window window) {
        return kvp -> MainUiManagerTab.getInstance(localisationSupport, window).getModel().put(kvp);
    }

    public static Consumer<CollectionHashMap.KeyValuePair<String, Path>> createShortCut(SiGuiSettings settings, LocalisationSupport localisationSupport, Window window) {
        return kvp -> {
            ShortcutProviders.SHORTCUT_FACTORY.getValue(settings.getOSs().get(0)).ifPresent(shortcutProvider -> {
                Path shortcutfolder = Paths.get(settings.get(SiGuiSettings.StringSettingKey.SHORTCUTFOLDER).orElseGet(() -> Paths.get(System.getProperty("user.home")).toString()));
                try {
                    Path actualLocation = shortcutProvider.createShortcut(shortcutfolder, kvp.getKey(), kvp.getValue(), "");
                    MainUiManagerTab.getInstance(localisationSupport, window).getModel().put(kvp.getKey(), actualLocation);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        };
    }

    private static final String bootstrapKey = "UserInstallation", bootstrapValue = "$ORIGIN/..";

    public static Consumer<Path> editBootstrap(LocalisationSupport localisationSupport) {
        return path -> {
            Path p = path.resolve("program");
            try {
                Optional<String> opt = Files.list(p).map(Path::toFile).map(File::getName).filter(s
                        -> s.startsWith("boot")).findAny();
                if (!opt.isPresent())
                    throw new IllegalArgumentException(localisationSupport.getString(ExceptionTranslation.ILLEGALARGUMENT_UNKNOWN, "bootstrap"));
                p = p.resolve(opt.get());
                StringBuilder stringBuilder = new StringBuilder();
                try (BufferedReader reader = Files.newBufferedReader(p)) {
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith(bootstrapKey))
                            stringBuilder.append(bootstrapKey).append('=').append(bootstrapValue).append('\n');
                        else stringBuilder.append(line).append('\n');
                    }
                }
                com.google.common.io.Files.write(stringBuilder.toString(), p.toFile(), StandardCharsets.UTF_8);


            } catch (Exception e) {
                Platform.runLater(() -> MainUi.handleException(e));
            }
        };
    }


}
