package at.reisisoft.concurrent;

import at.reisisoft.Download;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Florian on 23.06.2015.
 */
public class VersionExtractor implements Callable<Collection<String>> {
    private String url;

    public VersionExtractor(String url) {
        this.url = url;
    }

    @Override
    public Collection<String> call() throws Exception {
        try {
            Collection<String> collection = new LinkedList<>();
            String html = Download.downloadFromUrl(url);
            Matcher m = Pattern.compile("[^sdremote-]([0-9]\\.){2,3}[0-9](\\.beta[0-9]{1,2})?(-hotfix[0-9]{1,2})?").matcher(html);
            while (m.find() && m.find()) {
                String g = m.group();
                collection.add(g.substring(1, g.length()));
            }
            return collection;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
