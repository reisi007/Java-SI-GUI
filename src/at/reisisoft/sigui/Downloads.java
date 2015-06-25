package at.reisisoft.sigui;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Florian on 23.06.2015.
 */
public class Downloads {

    public static Collection<String> getLibOVersions(String url) {
        try {
            Collection<String> collection = new LinkedList<>();
            String html = DownloadInfo.downloadFromUrl(url);
            Matcher m = Pattern.compile("[^sdremote-]([0-9]\\.){2,3}[0-9](\\.beta[0-9]{1,2})?(-hotfix[0-9]{1,2})?").matcher(html);
            while (m.find() && m.find()) {
                String g = m.group();
                collection.add(g.substring(1, g.length()));
            }
            return collection;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
