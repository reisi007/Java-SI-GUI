package at.reisisoft.sigui;

import at.reisisoft.sigui.l10n.TranslationKey;

/**
 * Created by Florian on 11.07.2015.
 */
public enum DownloadType implements TranslationKey {
    Archive, Testing, Stable, Daily;
    private static String PREFIX = "downloadtype.";

    @Override
    public String getTranslationKey() {
        return PREFIX + toString().toLowerCase();
    }
}
