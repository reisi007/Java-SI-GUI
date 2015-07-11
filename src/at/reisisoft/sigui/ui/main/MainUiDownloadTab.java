package at.reisisoft.sigui.ui.main;

import at.reisisoft.sigui.l10n.LocalisationSupport;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tab;

import java.util.Objects;

/**
 * Created by Florian on 11.07.2015.
 */
public class MainUiDownloadTab extends Tab {

    private static MainUiDownloadTab instance = null;

    public static MainUiDownloadTab getInstance(LocalisationSupport localisationSupport) {
        if (instance == null) {
            Objects.requireNonNull(localisationSupport);
            instance = new MainUiDownloadTab(localisationSupport);
        }
        return instance;
    }

    private final LocalisationSupport localisationSupport;

    private MainUiDownloadTab(LocalisationSupport localisationSupport) {
        super(localisationSupport.getString(MainUITab.DOWNLOAD));
        this.localisationSupport = localisationSupport;
    }
}
