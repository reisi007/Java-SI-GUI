package at.reisisoft.sigui.ui.main;

import at.reisisoft.sigui.l10n.ExceptionTranslation;
import at.reisisoft.sigui.l10n.LocalisationSupport;
import javafx.scene.control.Tab;
import javafx.stage.Window;

/**
 * Created by Florian on 10.07.2015.
 */
public class MainUiTabProvider {

    public static Tab fillTab(MainUITab key, LocalisationSupport localisationSupport, Window window) {
        switch (key) {
            case DOWNLOAD:
                return MainUiDownloadTab.getInstance(localisationSupport, window);
            case INSTALL:
                return MainUiInstallTab.getInstance(localisationSupport, window);
            case MANAGER:
                return MainUiManagerTab.getInstance(localisationSupport, window);
            case SETTINGS:
                return MainUiSettingsTab.getInstance(localisationSupport, window);
            default:
                throw new IllegalArgumentException(localisationSupport.getString(ExceptionTranslation.ILLEGALARGUMENT_UNKNOWN, localisationSupport.getString(MainUiTranslation.TAB)));
        }
    }
}
