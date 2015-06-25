package at.reisisoft.sigui;

import java.io.IOException;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Created by Florian on 23.06.2015.
 */
public class DownloadAvailablePredicate implements Predicate<String> {
    private String extension, version;

    public DownloadAvailablePredicate(String extension, String version) {
        this.extension = extension;
        this.version = version;
    }

    public DownloadAvailablePredicate(OS os, String version) {
        this(os.getFileExtension(), version);
    }

    @Override
    public boolean test(String s) {
        try {
            Pattern p = Pattern.compile(version.substring(0, version.length() - 1) + ".*?\\." + extension);
            return p.matcher(Download.downloadFromUrl(s)).find();
        } catch (IOException e) {
            return false;
        }
    }
}