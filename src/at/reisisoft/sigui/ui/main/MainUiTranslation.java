package at.reisisoft.sigui.ui.main;

import at.reisisoft.sigui.l10n.TranslationKey;

/**
 * Created by Florian on 08.07.2015.
 */
public enum MainUiTranslation implements TranslationKey {
    APP_NAME, TAB, DOWNLOAD_UPDATE, INSTALLER_MAIN, INSTALLER_HELP, INSTALLER_SDK, INSTALLER_LANGPACK, DOWNLOAD_START, AVAILABLE_LANGUAGES, OPEN, INSTALL_START, FINDINSTALLATIONS, SHORTCUT_CREATE, MANAGER_DELETE;
    public static final String PREFIX = "ui.main.";

    @Override
    public String getTranslationKey() {
        return PREFIX + toString().toLowerCase().replace('_', '.');
    }


}
