package at.reisisoft.sigui.ui;

import at.reisisoft.sigui.l10n.TranslationKey;

/**
 * Created by Florian on 10.07.2015.
 */
public enum ExceptionTranslation implements TranslationKey {
    ILLEGALARGUMENT_UNKNOWN;
    public static final String PREFIX = "exception.";

    @Override
    public String toString() {
        return PREFIX + super.toString().toLowerCase().replace('_', '.');
    }
}
