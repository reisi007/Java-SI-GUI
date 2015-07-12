package at.reisisoft.sigui.ui.main;

import at.reisisoft.sigui.l10n.TranslationKey;

/**
 * Created by Florian on 08.07.2015.
 */
public enum MainUiTranslation implements TranslationKey {
    APP_NAME, TAB, DOWNLOAD_UPDATE, INSTALLER_MAIN, INSTALLER_HELP, INSTALLER_SDK;
    public static final String PREFIX = "ui.main.";

    @Override
    public String getTranslationKey() {
        return PREFIX + toString().toLowerCase().replace('_', '.');
    }


}
