package at.reisisoft.sigui.ui.main;

import at.reisisoft.sigui.l10n.TranslationKey;

/**
 * Created by Florian on 08.07.2015.
 */
public enum MainUiTranslation implements TranslationKey {
    APP_NAME, TAB;
    public static final String PREFIX = "ui.main.";

    @Override
    public String toString() {
        return PREFIX + super.toString().toLowerCase().replace('_', '.');
    }


}
