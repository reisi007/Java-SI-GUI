package at.reisisoft.sigui.settings;

import at.reisisoft.sigui.*;
import at.reisisoft.sigui.collection.CollectionHashMap;
import at.reisisoft.sigui.l10n.LocalisationSupport;
import com.thoughtworks.xstream.XStream;

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

    private static XStream _xstream = null;

    public static XStream getXStream() {
        if (_xstream == null)
            _xstream = new XStream();
        return _xstream;
    }

    public enum StringSettingKey {DOWNLOADFOLDER, SHORTCUTFOLDER}

    public enum BooleanSettingKey {RENAME_FILES}

    private Map<StringSettingKey, String> stringSettings = new EnumMap<>(StringSettingKey.class);
    private Map<BooleanSettingKey, Boolean> booleanSettings = new EnumMap<>(BooleanSettingKey.class);

    private List<OS> oss = Arrays.asList(OS.detect());
    private List<Architecture> architectures = Arrays.asList(Architecture.detect());

    private CollectionHashMap<DownloadType, SortedSet<DownloadInfo.DownloadLocation>, DownloadInfo.DownloadLocation> cachedDownloads = CollectionHashMap.empty();
    private Locale userLanguage = Locale.getDefault();

    public Optional<String> getStringSettings(StringSettingKey key) {
        return Optional.ofNullable(stringSettings.get(key));
    }

    public void setSetting(StringSettingKey key, String value) {
        Objects.requireNonNull(key);
        if (value == null)
            stringSettings.remove(key);
        stringSettings.put(key, value);
    }

    public void setSetting(BooleanSettingKey key, Boolean value) {
        Objects.requireNonNull(key);
        if (value == null)
            booleanSettings.remove(key);
        booleanSettings.put(key, value);
    }

    public boolean get(BooleanSettingKey key, Boolean defaultValue) {
        return booleanSettings.getOrDefault(key, defaultValue);
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