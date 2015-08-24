package at.reisisoft.sigui;

import at.reisisoft.sigui.downloader.DownloadManager.Entry;

import java.net.URL;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Florian on 15.07.2015.
 */
public class DownloadHelper {
    public static Optional<Entry> getDownloadFileMain(DownloadInfo.DownloadLocation base) {
        return getDownloadFile(base, getRegex4Main(base));
    }


    public static Optional<Entry> getDownloadFileSdk(DownloadInfo.DownloadLocation base) {
        return getDownloadFile(base, getRegex4Sdk(base));
    }

    public static Optional<Entry> getDownloadFileHelp(DownloadInfo.DownloadLocation base, String hpLang) {
        return getDownloadFile(base, getRegex4Hp(base, hpLang));
    }

    public static Optional<Entry> getDownloadFileLangPack(DownloadInfo.DownloadLocation base, String lang) {
        return getDownloadFile(base,getRegex4Lang(base, lang));
    }

    private static Optional<Entry> getDownloadFile(DownloadInfo.DownloadLocation base, String regex) {
        try {
            String html = DownloadInfo.downloadFromUrl(base.getUrl());
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(html);
            if (!m.find())
                return Optional.empty();
            String g = m.group();
            g = g.substring(0, g.length() - 1);
            URL u = new URL(base.getUrl() + g);
            Entry e = new Entry(u);
            e.setFilename(g);
            return Optional.of(e);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private static String getRegex4Main(DownloadInfo.DownloadLocation base) {
        if (base.getDownloadType() == DownloadType.Daily)
            return base.getVersionPrefix() + ".+?" + base.getOs().getOSShortName() + "_" + base.getA().getInFilenameArchitecture().toLowerCase() + (base.getOs().isLinux() ? '_' + base.getOs().getLinuxPackagingSystem() : "_[^h].+?[^s]\\.") + base.getOs().getFileExtension() + '"';
        Architecture current = base.getA();
        String archString;
        if (base.getOs().isLinux()) {
            if (current == Architecture.X86) {
                archString = "x86";
            } else {
                archString = "x86-64";
            }
            archString+= ".{4}";
        } else
            archString = current.getInFilenameArchitecture().toLowerCase();

        return "Lib(O_|re).+?" + archString + ("exe".equalsIgnoreCase(base.getOs().getFileExtension()) ? ".+?install_multi.exe\"" : "\\." + base.getOs().getFileExtension() + '"');
    }

    private static String getRegex4Sdk(DownloadInfo.DownloadLocation base) {
        return (base.getDownloadType() == DownloadType.Daily ? base.getVersionPrefix() : "Lib") + ".+?sdk\\." + base.getOs().getFileExtension() + '"';
    }

    private static String getRegex4Hp(DownloadInfo.DownloadLocation base, String hpLang) {
        return (base.getDownloadType() == DownloadType.Daily ? base.getVersionPrefix() : "Lib") + ".+?helppack_" + hpLang + "\\." + base.getOs().getFileExtension() + '"';
    }
    private static String getRegex4Lang(DownloadInfo.DownloadLocation base, String lang){
        return getRegex4Hp(base, lang).replace("helppack","langpack");
    }
}
