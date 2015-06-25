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

    public CollectionHashMap<DownloadType, SortedSet<DownloadLocation>, DownloadLocation> getAllAvailableDownloads(Architecture a, OS os) {
        Stream<Collection<DownloadLocation>> step1 = Stream.of(getStableDownloads(a, os), getTestingDownloads(a, os), getDailyBuilds(a, os), getArchiveDownloads(a, os)).map(Utils.mapFuture());
        Stream<CollectionHashMap.KeyValuePair<DownloadType, DownloadLocation>> step2 = step1.collect(Utils.collectCollectionToStream()).map(entry -> new CollectionHashMap.KeyValuePair<>(entry.getDownloadType(), entry));
        return step2.collect(Utils.collectToCollectionHashmap(TreeSet<DownloadLocation>::new));
    }

    private ListenableFuture<Collection<DownloadLocation>> getDailyBuilds(Architecture a, OS os) {
        return executor.submit(new DailyBuildsCallable(os, a));
    }

    private ListenableFuture<Collection<DownloadLocation>> getArchiveDownloads(Architecture a, OS os) {
        return executor.submit(new ArchiveCallable(a, os, executor));
    }

    private ListenableFuture<Collection<DownloadLocation>> getStableDownloads(Architecture a, OS os) {
        return executor.submit(new StableCallable(a, os, executor));
    }

    private ListenableFuture<Collection<DownloadLocation>> getTestingDownloads(Architecture a, OS os) {
        return executor.submit(new TestingCallable(a, os, executor));
    }

    public enum DownloadType {
        Archive, Testing, Stable, Daily;
    }

    @Override
    public void close() {
        executor.shutdown();
    }

    public static class DownloadLocation implements Comparable<DownloadLocation> {
        private final String versionCode, versionPrefix, url;
        private final Architecture a;
        private final OS os;

        public DownloadLocation(String versionCode, String versionPrefix, String url, Architecture a, OS os) {
            this.versionCode = versionCode;
            this.versionPrefix = versionPrefix;
            this.url = url;
            this.a = a;
            this.os = os;
        }

        public String getVersionCode() {
            return versionCode;
        }

        public String getVersionPrefix() {
            return versionPrefix;
        }

        public String getUrl() {
            return url;
        }

        public Architecture getA() {
            return a;
        }

        public OS getOs() {
            return os;
        }

        public DownloadType getDownloadType() {
            if (getVersionPrefix().startsWith(StableCallable.PREFIX))
                return DownloadType.Stable;
            if (getVersionPrefix().startsWith(TestingCallable.PREFIX))
                return DownloadType.Testing;
            if (getVersionPrefix().startsWith(ArchiveCallable.PREFIX))
                return DownloadType.Archive;
            return DownloadType.Daily;
        }

        @Override
        public String toString() {
            return versionPrefix + versionCode + " " + os.getOSShortName() + '/' + a + " @ " + url;
        }

        @Override
        public int compareTo(DownloadLocation o) {
            return toString().compareTo(o.toString());
        }
    }
}
