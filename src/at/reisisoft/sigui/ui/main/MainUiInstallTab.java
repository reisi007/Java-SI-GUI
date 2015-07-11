package at.reisisoft.sigui.ui.main;

import at.reisisoft.sigui.l10n.LocalisationSupport;
import javafx.scene.control.Tab;

import java.util.Objects;

/**
 * Created by Florian on 11.07.2015.
 */
public class MainUiInstallTab extends Tab {

    private static MainUiInstallTab instance = null;

    public static MainUiInstallTab getInstance(LocalisationSupport localisationSupport) {
        if (instance == null) {
            Objects.requireNonNull(localisationSupport);
            instance = new MainUiInstallTab(localisationSupport);
        }
        return instance;
    }

    private final LocalisationSupport localisationSupport;

    private MainUiInstallTab(LocalisationSupport localisationSupport) {
        super(localisationSupport.getString(MainUITab.INSTALL));
        this.localisationSupport = localisationSupport;
    }
}
