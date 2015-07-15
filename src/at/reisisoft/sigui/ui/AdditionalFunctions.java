package at.reisisoft.sigui.ui;

import at.reisisoft.sigui.collection.CollectionHashMap;
import at.reisisoft.sigui.l10n.LocalisationSupport;
import at.reisisoft.sigui.settings.SiGuiSettings;
import at.reisisoft.sigui.shortcut.ShortcutProviders;
import at.reisisoft.sigui.ui.main.MainUiManagerTab;
import javafx.stage.Window;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;

/**
 * Created by Florian on 15.07.2015.
 */
public class AdditionalFunctions {

    public static Consumer<CollectionHashMap.KeyValuePair<String, Path>> addToManager(LocalisationSupport localisationSupport, Window window) {
        return kvp -> MainUiManagerTab.getInstance(localisationSupport, window).getModel().put(kvp);
    }

    public static Consumer<CollectionHashMap.KeyValuePair<String, Path>> createShortCut(SiGuiSettings settings) {
        return kvp -> {
            ShortcutProviders.SHORTCUT_FACTORY.getValue(settings.getOSs().get(0)).ifPresent(shortcutProvider -> {
                Path shortcutfolder = Paths.get(settings.get(SiGuiSettings.StringSettingKey.SHORTCUTFOLDER).orElseGet(() -> Paths.get(System.getProperty("user.home")).toString()));
                try {
                    shortcutProvider.createShortcut(shortcutfolder, kvp.getKey(), kvp.getValue(), "");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        };
    }


}
