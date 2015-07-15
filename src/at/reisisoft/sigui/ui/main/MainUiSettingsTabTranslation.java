package at.reisisoft.sigui.ui.main;

import at.reisisoft.sigui.l10n.TranslationKey;

/**
 * Created by Florian on 08.07.2015.
 */
public enum MainUiSettingsTabTranslation implements TranslationKey {
    RENAMEFILES, RENAMEFILES_TOOLTIP, FOLDER_DOWNLOAD, FOLDER_SHORTCUT, FOLDER_DOWNLOAD_TOOLTIP, FOLDER_SHORTCUT_TOOLTIP, L10LABLE, INSTALLPATH, INSTALLPATH_TOOLTIP, INSTALLSUBFOLDER, INSTALLSUBFOLDER_TOOLTIP, OS, ARCH;
    public static final String PREFIX = "ui.main.setting.";

    @Override
    public String getTranslationKey() {
        return PREFIX + toString().toLowerCase().replace('_', '.');
    }


}
