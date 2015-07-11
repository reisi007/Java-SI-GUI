package at.reisisoft.sigui.l10n;

/**
 * Created by Florian on 10.07.2015.
 */
public enum ExceptionTranslation implements TranslationKey {
    ILLEGALARGUMENT_UNKNOWN, ILLEGALARGUMENT_FILEISNOFILE, GENERAL_MOREINFO, NULLPOINTER;
    public static final String PREFIX = "exception.";

    @Override
    public String getTranslationKey() {
        return PREFIX + toString().toLowerCase().replace('_', '.');
    }

}
