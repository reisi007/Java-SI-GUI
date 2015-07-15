package at.reisisoft.sigui.settings;

import at.reisisoft.sigui.*;
import at.reisisoft.sigui.collection.CollectionHashMap;
import at.reisisoft.sigui.l10n.ExceptionTranslation;
import at.reisisoft.sigui.l10n.LocalisationSupport;
import at.reisisoft.sigui.l10n.TranslationKey;
import com.thoughtworks.xstream.XStream;
import javafx.collections.ObservableList;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Created by Florian on 11.07.2015.
 */
@XmlRootElement(namespace = "http://sigui.reisisoft.com/java", name = "settings")
public class SiGuiSettings implements Serializable {

    private static LocalisationSupport localisationSupport;

    public static LocalisationSupport getLocalisationSupport() {
        return localisationSupport;
    }

    public static void setLocalisationSupport(LocalisationSupport localisationSupport) {
        Objects.requireNonNull(localisationSupport);
        SiGuiSettings.localisationSupport = localisationSupport;
    }

    private static String getLocalisedString(TranslationKey key, Object... format) {
        if (localisationSupport == null)
            return key.toString();
        return localisationSupport.getString(key, format);
    }

    private static XStream _xstream = null;

    public static XStream getXStream() {
        if (_xstream == null)
            _xstream = new XStream();
        return _xstream;
    }

    public enum StringSettingKey {DOWNLOADFOLDER, SHORTCUTFOLDER, DL_LANGUAGE, PATH_MAIN, PATH_SDK, PATH_HP, PATH_LANGPACK, INSTALL_PATH_LAST_FILEOPENED}

    public enum BooleanSettingKey {RENAME_FILES, CB_MAIN_TICKED, CB_HELP_TICKED, CB_SDK_TICKED, CB_LANGPACK_TICKED}

    private Map<StringSettingKey, String> stringSettings = new EnumMap<>(StringSettingKey.class);
    private Map<BooleanSettingKey, Boolean> booleanSettings = new EnumMap<>(BooleanSettingKey.class);
    private Map<String, ObservableList<Path>> managerEntries;

    public Map<String, ObservableList<Path>> getManagerEntries() {
        return managerEntries;
    }

    public void setManagerEntries(Map<String, ObservableList<Path>> managerEntries) {
        this.managerEntries = managerEntries;
    }

    public Map<DownloadType, DownloadInfo.DownloadLocation> getSelectedDownloadLocations() {
        return selectedDownloadLocations;
    }

    public void setSelectedDownloadLocations(Map<DownloadType, DownloadInfo.DownloadLocation> selectedDownloadLocations) {
        this.selectedDownloadLocations = selectedDownloadLocations;
    }

    private Map<DownloadType, DownloadInfo.DownloadLocation> selectedDownloadLocations = Collections.emptyMap();

    public Collection<String> getAvailableLanguages() {
        return availableLanguages;
    }

    public void setAvailableLanguages(Collection<String> availableLanguages) {
        this.availableLanguages = availableLanguages;
    }

    private Collection<String> availableLanguages = Collections.emptyList();

    private List<OS> oss = Arrays.asList(OS.detect());
    private List<Architecture> architectures = Arrays.asList(Architecture.detect());

    private CollectionHashMap<DownloadType, SortedSet<DownloadInfo.DownloadLocation>, DownloadInfo.DownloadLocation> cachedDownloads = CollectionHashMap.empty();
    private Locale userLanguage = Locale.getDefault();

    public SiGuiSettings() {
        set(BooleanSettingKey.CB_MAIN_TICKED, true);
        set(BooleanSettingKey.CB_HELP_TICKED, true);
        set(BooleanSettingKey.CB_SDK_TICKED, false);
        set(BooleanSettingKey.CB_LANGPACK_TICKED, false);
        set(StringSettingKey.DOWNLOADFOLDER, com.google.common.io.Files.createTempDir().toString());
        set(BooleanSettingKey.RENAME_FILES, true);
        managerEntries = Collections.emptyMap();
    }

    public Optional<String> get(StringSettingKey key) {
        return Optional.ofNullable(stringSettings.get(key));
    }

    public void set(StringSettingKey key, String value) {
        Objects.requireNonNull(key);
        if (value == null)
            stringSettings.remove(key);
        stringSettings.put(key, value);
    }

    public void set(BooleanSettingKey key, Boolean value) {
        Objects.requireNonNull(key);
        if (value == null)
            booleanSettings.remove(key);
        booleanSettings.put(key, value);
    }

    public boolean get(BooleanSettingKey key) {
        Boolean b = booleanSettings.get(key);
        if (b == null)
            throw new IllegalArgumentException(getLocalisedString(ExceptionTranslation.NOKEY, key));
        return b;
    }


    public CollectionHashMap<DownloadType, SortedSet<DownloadInfo.DownloadLocation>, DownloadInfo.DownloadLocation> getCachedDownloads() {
        return cachedDownloads;
    }

    public void setCachedDownloads(CollectionHashMap<DownloadType, SortedSet<DownloadInfo.DownloadLocation>, DownloadInfo.DownloadLocation> cachedDownloads) {
        this.cachedDownloads = cachedDownloads;
    }

    public Locale getUserLanguage() {
        return userLanguage;
    }

    public void setUserLanguage(Locale userLanguage) {
        this.userLanguage = userLanguage;
    }

    public List<OS> getOSs() {
        return oss;
    }

    public List<Architecture> getArchitectures() {
        return architectures;
    }

    public void setOSs(List<OS> oss) {
        Objects.requireNonNull(oss);
        this.oss = oss;
    }

    public void setArchitectures(List<Architecture> architectures) {
        Objects.requireNonNull(architectures);
        this.architectures = architectures;
    }

    public void save(Path p, LocalisationSupport localisationSupport) throws IOException {
        save(this, p, localisationSupport);
    }

    public static SiGuiSettings load(Path p) {
        if (!Files.exists(p))
            return new SiGuiSettings();

        try {
            return (SiGuiSettings) getXStream().fromXML(Files.newBufferedReader(p, StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
            return new SiGuiSettings();
        }
    }

    public static void save(SiGuiSettings settings, Path p, LocalisationSupport localisationSupport) throws IOException {
        Objects.requireNonNull(settings, "settings");
        System.out.println("Saving Settings to: " + p);
        getXStream().toXML(settings, Files.newBufferedWriter(p, StandardCharsets.UTF_8));
    }

    @Override
    public String toString() {
        return "SiGuiSettings{" +
                "stringSettings=" + stringSettings +
                ", booleanSettings=" + booleanSettings +
                ", oss=" + Utils.toString(oss, os -> os.getOSLongName()) +
                ", architectures=" + architectures +
                ", cachedDownloads=" + cachedDownloads +
                ", userLanguage=" + userLanguage +
                '}';
    }
}
