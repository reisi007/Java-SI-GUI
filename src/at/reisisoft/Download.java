package at.reisisoft;

import at.reisisoft.concurrent.ArchiveCallable;
import at.reisisoft.concurrent.DailyBuildsCallable;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Created by Florian on 22.06.2015.
 */
public class Download implements AutoCloseable {

    private ListeningExecutorService executor = null;

    private void decorateExecutor(ExecutorService executorService) {
        if (executorService != null)
            executor = MoreExecutors.listeningDecorator(executorService);
    }

    public static String downloadFromUrl(String s) throws IOException {
        return downloadFromUrl(new URL(s));
    }

    public static String downloadFromUrl(URL u) throws IOException {
        return Resources.toString(u, Charsets.UTF_8);
    }

    public Download() {
        this(Executors.newFixedThreadPool(8));
    }

    public Download(ExecutorService executorService) {
        decorateExecutor(executorService);
    }

    public Download(ListeningExecutorService executorService) {
        executor = executorService;
    }

    public Collection<Entry> getAllAvailableDownloads(Architecture a, OS os) {
        Stream<ListenableFuture<Collection<Entry>>> s1 = Stream.of(getDailyBuilds(a, os));
        Stream<ListenableFuture<Collection<ListenableFuture<Collection<Entry>>>>> s2 = Stream.of(getArchiveDownloads(a, os));
        Stream<ListenableFuture<Collection<Entry>>> s3 = s2.map(Utils.mapFuture()).collect(Utils.collectCollectionToStream());
        Stream<ListenableFuture<Collection<Entry>>> s4 = Stream.concat(s1, s3);

        return s4.map(Utils.mapFuture()).collect(Utils.collectCollectionsToSingleCollection());
    }

    private ListenableFuture<Collection<Entry>> getDailyBuilds(Architecture a, OS os) {
        return executor.submit(new DailyBuildsCallable(os, a));
    }

    private ListenableFuture<Collection<ListenableFuture<Collection<Entry>>>> getArchiveDownloads(Architecture a, OS os) {
        return executor.submit(new ArchiveCallable(a, os));
    }

    @Override
    public void close() {
        executor.shutdown();
    }

    public static class DownloadAvailablePredicate implements Predicate<String> {
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
                return p.matcher(downloadFromUrl(s)).find();
            } catch (IOException e) {
                return false;
            }
        }
    }


    public static class Entry implements Comparable<Entry> {
        private String version, url;
        private Architecture a;
        private OS os;

        public String getVersion() {
            return version;
        }


        public OS getOs() {
            return os;
        }


        public Architecture getA() {
            return a;
        }


        public String getUrl() {
            return url;
        }


        public Entry(String version, String url, Architecture a, OS os) {
            this.version = version;
            this.url = url;
            this.a = a;
            this.os = os;
        }

        @Override
        public String toString() {
            return version + " " + os + '/' + a + " @ " + url;
        }

        @Override
        public int compareTo(Entry o) {
            return toString().compareTo(o.toString());
        }
    }
}
