package at.reisisoft.sigui.ui.main;

import at.reisisoft.sigui.l10n.LocalisationSupport;
import javafx.scene.control.Tab;

import java.util.Objects;

/**
 * Created by Florian on 11.07.2015.
 */
public class MainUiSettingsTab extends Tab {

    private static MainUiSettingsTab instance = null;

    public static MainUiSettingsTab getInstance(LocalisationSupport localisationSupport) {
        if (instance == null) {
            Objects.requireNonNull(localisationSupport);
            instance = new MainUiSettingsTab(localisationSupport);
        }
        return instance;
    }

    private final LocalisationSupport localisationSupport;

    private MainUiSettingsTab(LocalisationSupport localisationSupport) {
        super(localisationSupport.getString(MainUITab.SETTINGS));
        this.localisationSupport = localisationSupport;
    }
}
