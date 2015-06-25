package at.reisisoft.sigui;

import at.reisisoft.sigui.collection.CollectionHashMap;
import at.reisisoft.sigui.concurrent.ArchiveCallable;
import at.reisisoft.sigui.concurrent.DailyBuildsCallable;
import at.reisisoft.sigui.concurrent.StableCallable;
import at.reisisoft.sigui.concurrent.TestingCallable;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
        this(Executors.newFixedThreadPool(10));
    }

    public Download(ExecutorService executorService) {
        decorateExecutor(executorService);
    }

    public Download(ListeningExecutorService executorService) {
        executor = executorService;
    }

    public CollectionHashMap<DownloadType, SortedSet<Entry>, Entry> getAllAvailableDownloads(Architecture a, OS os) {
        Stream<Collection<Entry>> step1 = Stream.of(getStableDownloads(a, os), getTestingDownloads(a, os), getDailyBuilds(a, os), getArchiveDownloads(a, os)).map(Utils.mapFuture());
        Stream<CollectionHashMap.KeyValuePair<DownloadType, Entry>> step2 = step1.collect(Utils.collectCollectionToStream()).map(entry -> {
            DownloadType downloadType = DownloadType.Daily;
            if (entry.getVersion().startsWith(StableCallable.PREFIX))
                downloadType = DownloadType.Stable;
            else if (entry.getVersion().startsWith(TestingCallable.PREFIX))
                downloadType = DownloadType.Testing;
            else if (entry.getVersion().startsWith(ArchiveCallable.PREFIX))
                downloadType = DownloadType.Archive;
            return new CollectionHashMap.KeyValuePair<>(downloadType, entry);
        });
        return step2.collect(Utils.collectToCollectionHashmap(TreeSet<Entry>::new));
    }

    private ListenableFuture<Collection<Entry>> getDailyBuilds(Architecture a, OS os) {
        return executor.submit(new DailyBuildsCallable(os, a));
    }

    private ListenableFuture<Collection<Entry>> getArchiveDownloads(Architecture a, OS os) {
        return executor.submit(new ArchiveCallable(a, os, executor));
    }

    private ListenableFuture<Collection<Entry>> getStableDownloads(Architecture a, OS os) {
        return executor.submit(new StableCallable(a, os, executor));
    }

    private ListenableFuture<Collection<Entry>> getTestingDownloads(Architecture a, OS os) {
        return executor.submit(new TestingCallable(a, os, executor));
    }

    public enum DownloadType {
        Archive, Testing, Stable, Daily;
    }

    @Override
    public void close() {
        executor.shutdown();
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
            return version + " " + os.getOSShortName() + '/' + a + " @ " + url;
        }

        @Override
        public int compareTo(Entry o) {
            return toString().compareTo(o.toString());
        }
    }
}
