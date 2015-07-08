package at.reisisoft.sigui.l10n;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by Florian on 08.07.2015.
 */
public class LocalisationSupport {

    private static LocalisationSupport _instance = null;

    public static LocalisationSupport getInstance() {
        if (_instance == null)
            _instance = new LocalisationSupport();
        return _instance;
    }

    private LocalisationSupport() {

    }

    private ResourceBundle bundle = ResourceBundle.getBundle(LocalisationSupport.class.getPackage().getName()+  ".Messages", Locale.getDefault());

    public String getString(TranslationsKey key, Object... format) {
        String value = bundle.getString(key.toString());
        if (format.length == 0)
            return value;
        return MessageFormat.format(value, format);
    }

}