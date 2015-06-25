package at.reisisoft.sigui.downloader;

import at.reisisoft.sigui.Download;

import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Florian on 25.06.2015.
 */
public class DownloadManager {


    public Optional<Entry> getDownloadFileMain(Download.DownloadLocation base) {
        return getDownloadFile(base, getRegex4Main(base));
    }

    public Optional<Entry> getDownloadFileSdk(Download.DownloadLocation base) {
        return getDownloadFile(base, getRegex4Sdk(base));
    }

    public Optional<Entry> getDownloadFileHelp(Download.DownloadLocation base, String hpLang) {
        return getDownloadFile(base, getRegex4Hp(base, hpLang));
    }

    private Optional<Entry> getDownloadFile(Download.DownloadLocation base, String regex) {
        try {
            String html = Download.downloadFromUrl(base.getUrl());
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(html);
            if (!m.find())
                return Optional.empty();
            String g = m.group();
            g = g.substring(0, g.length() - 1);
            URL u = new URL(base.getUrl() + g);
            return Optional.of(new Entry(u));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private String getRegex4Main(Download.DownloadLocation base) {
        if (base.getDownloadType() == Download.DownloadType.Daily)
            return base.getVersionCode() + ".+?" + base.getOs().getOSShortName() + "_" + base.getA().toString().toLowerCase() + "_[^h].+?[^sdk]\\." + base.getOs().getFileExtension() + "<";
        return "Lib.*?" + base.getA().toString().toLowerCase() + "\\." + base.getOs().getFileExtension() + "[^\\.asc]";
    }

    private String getRegex4Sdk(Download.DownloadLocation base) {
        return (base.getDownloadType() == Download.DownloadType.Daily ? base.getVersionPrefix() : "Lib") + ".+?sdk\\." + base.getOs().getFileExtension() + "<";
    }

    private String getRegex4Hp(Download.DownloadLocation base, String hpLang) {
        return (base.getDownloadType() == Download.DownloadType.Daily ? base.getVersionPrefix() : "Lib") + ".+?helppack_" + hpLang + "\\." + base.getOs().getFileExtension() + "<";
    }

    public static class Entry {
        private final URL from;
        private File to;

        public Entry(URL from) {
            this(from, null);
        }

        public Entry(URL from, File to) {
            this.from = from;
            this.to = to;
        }

        public URL getFrom() {
            return from;
        }

        public Optional<File> getTo() {
            return Optional.ofNullable(to);
        }
    }
}
