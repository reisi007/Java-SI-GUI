package at.reisisoft.sigui.ui.main;

import at.reisisoft.sigui.l10n.LocalisationSupport;
import javafx.scene.control.Tab;
import javafx.stage.Window;

import java.util.Objects;

/**
 * Created by Florian on 11.07.2015.
 */
public class MainUiManagerTab extends Tab {

    private static MainUiManagerTab instance = null;

    public static MainUiManagerTab getInstance(LocalisationSupport localisationSupport, Window window) {
        if (instance == null) {
            Objects.requireNonNull(localisationSupport);
            instance = new MainUiManagerTab(localisationSupport);
        }
        return instance;
    }

    private final LocalisationSupport localisationSupport;

    private MainUiManagerTab(LocalisationSupport localisationSupport) {
        super(localisationSupport.getString(MainUITab.MANAGER));
        this.localisationSupport = localisationSupport;
    }
}
