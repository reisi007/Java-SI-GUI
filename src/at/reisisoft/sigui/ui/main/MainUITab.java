package at.reisisoft.sigui.ui.main;

import at.reisisoft.sigui.l10n.TranslationKey;

/**
 * Created by Florian on 10.07.2015.
 */
public enum MainUITab implements TranslationKey {
    DOWNLOAD, INSTALL, MANAGER, SETTINGS, CREDITS;
    public static final String PREFIX = MainUiTranslation.PREFIX + "tabs.";

    @Override
    public String getTranslationKey() {
        return PREFIX + toString().toLowerCase();
    }
}
