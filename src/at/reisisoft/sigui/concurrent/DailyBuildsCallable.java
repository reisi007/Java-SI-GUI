package at.reisisoft.sigui.concurrent;

import at.reisisoft.sigui.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Florian on 22.06.2015.
 */
public class DailyBuildsCallable implements Callable<Collection<Download.Entry>> {

    private OS os;
    private Architecture a;

    public DailyBuildsCallable(OS os, Architecture a) {
        this.os = os;
        this.a = a;
    }

    @Override
    public Collection<Download.Entry> call() throws Exception {
        if (os == OS.Win_EXE)
            return Collections.emptyList();
        String html = Download.downloadFromUrl(Constants.DEV_BUILD_URL);
        Collection e = new LinkedList<>();
        String[] versions = html.split("/icons/folder.gif");
        String[] subUrls = new String[versions.length - 1];
        Pattern p = Pattern.compile(">[a-z]{0,11}(-[0-9]{1,2}){0,2}\\/");
        String cur;
        Matcher matcher;
        for (int i = 1; i < versions.length; i++) {
            cur = versions[i];
            matcher = p.matcher(cur);
            if (matcher.find())
                subUrls[i - 1] = cur.substring(matcher.start() + 1, matcher.end());
        }
        return Arrays.stream(subUrls).map(g -> this.getThinderBoxCallable(Constants.DEV_BUILD_URL, g)).collect(Utils.collectCollectionsToSingleCollection());
    }

    public Collection<Download.Entry> getThinderBoxCallable(String base, String branchUrl) {
        try {
            String html = Download.downloadFromUrl(base + branchUrl);

            Pattern p = Pattern.compile(os.getOSShortName() + "(\\w|_|-)+?" + a + "@[0-9]+?.*?\\/");
            Collection<String> thinderboxes = new LinkedList<>();
            Matcher m = p.matcher(html);
            while (m.find() && m.find())
                thinderboxes.add(m.group());
            Stream<String> stream = thinderboxes.parallelStream().filter(t -> new DownloadAvailablePredicate(os, branchUrl.substring(0, branchUrl.length() - 1)).test(base + branchUrl + t + "current/"));
            return stream.map(u -> new Download.Entry(branchUrl + u.substring(0, u.length() - 1), base + branchUrl + u + "current/", a, os)).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
