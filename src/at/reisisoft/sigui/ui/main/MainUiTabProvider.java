package at.reisisoft.sigui.ui.main;

import at.reisisoft.sigui.l10n.ExceptionTranslation;
import at.reisisoft.sigui.l10n.LocalisationSupport;
import javafx.scene.control.Tab;

/**
 * Created by Florian on 10.07.2015.
 */
public class MainUiTabProvider {

    public static Tab fillTab(MainUITab key, LocalisationSupport localisationSupport) {
        switch (key) {
            case DOWNLOAD:
                return MainUiDownloadTab.getInstance(localisationSupport);
            case INSTALL:
                return MainUiInstallTab.getInstance(localisationSupport);
            case MANAGER:
                return MainUiManagerTab.getInstance(localisationSupport);
            case SETTINGS:
                return MainUiSettingsTab.getInstance(localisationSupport);
            default:
                throw new IllegalArgumentException(localisationSupport.getString(ExceptionTranslation.ILLEGALARGUMENT_UNKNOWN, localisationSupport.getString(MainUiTranslation.TAB)));
        }
    }
}
