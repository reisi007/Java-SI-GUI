package at.reisisoft.sigui;

import at.reisisoft.sigui.downloader.DownloadManager.Entry;

import java.net.URL;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Florian on 15.07.2015.
 */
public class DownloafHelper {
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
        return Optional.empty();//TODO Implement
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
        return "Lib(O_|re).+?" + base.getA().getInFilenameArchitecture().toLowerCase() + ("exe".equalsIgnoreCase(base.getOs().getFileExtension()) ? ".+?install_multi.exe\"" : "\\." + base.getOs().getFileExtension() + "[^\\.asc]");
    }

    private static String getRegex4Sdk(DownloadInfo.DownloadLocation base) {
        return (base.getDownloadType() == DownloadType.Daily ? base.getVersionPrefix() : "Lib") + ".+?sdk\\." + base.getOs().getFileExtension() + '"';
    }

    private static String getRegex4Hp(DownloadInfo.DownloadLocation base, String hpLang) {
        return (base.getDownloadType() == DownloadType.Daily ? base.getVersionPrefix() : "Lib") + ".+?helppack_" + hpLang + "\\." + base.getOs().getFileExtension() + '"';
    }
}
