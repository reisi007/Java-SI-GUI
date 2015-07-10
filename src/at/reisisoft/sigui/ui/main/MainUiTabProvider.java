package at.reisisoft.sigui.ui.main;

import at.reisisoft.sigui.l10n.LocalisationSupport;
import at.reisisoft.sigui.ui.ExceptionTranslation;
import javafx.scene.control.Tab;

/**
 * Created by Florian on 10.07.2015.
 */
public class MainUiTabProvider {

    public static void fillTab(MainUITab key, Tab tab, LocalisationSupport localisationSupport) {
        switch (key) {
            case DOWNLOAD:
                fillDownload(tab);
                break;
            case INSTALL:
                fillInstall(tab);
                break;
            case MANAGER:
                fillManager(tab);
                break;
            case SETTINGS:
                fillSettings(tab);
                break;
            default:
                throw new IllegalArgumentException(localisationSupport.getString(ExceptionTranslation.ILLEGALARGUMENT_UNKNOWN, localisationSupport.getString(MainUiTranslation.TAB)));
        }
    }

    private static void fillDownload(Tab t) {
        //TODO Implement
    }

    private static void fillInstall(Tab t) {
        //TODO Implement
    }

    private static void fillManager(Tab t) {
        //TODO Implement
    }

    private static void fillSettings(Tab t) {
        //TODO Implement
    }
}
