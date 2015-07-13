package at.reisisoft.sigui.downloader;

import at.reisisoft.sigui.DownloadInfo;
import at.reisisoft.sigui.DownloadType;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.function.ToLongFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Florian on 25.06.2015.
 */
public class DownloadManager implements AutoCloseable, PartiallyCancelable<DownloadManager.Entry> {
    private final ListeningExecutorService executorService = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
    private DownloadManagerTotalDownloadProgress totalDownloadProgress = new DownloadManagerTotalDownloadProgress();

    public DownloadProgressInfo getTotalDownloadProgress() {
        return totalDownloadProgress;
    }

    public void addTotalDownloadProgressListener(DownloadProgressListener listener) {
        totalDownloadProgress.addDownloadProgressListener(listener);
    }

    public void removeTotalDownloadProgressListener(DownloadProgressListener listener) {
        totalDownloadProgress.removeDownloadProgressListener(listener);
    }

    @SafeVarargs
    public final ListenableFuture<Optional<Entry>> submit(Entry entry, DownloadProgressListener... downloadProgressListeners) {
        List<DownloadProgressListener> listeners = new LinkedList<>(Arrays.asList(downloadProgressListeners));
        listeners.add(totalDownloadProgress.getDownloadProgressListener(entry));
        ListenableFuture<Optional<Entry>> listenableFuture = executorService.submit(getDownloadCallable(entry, listeners));
        totalDownloadProgress.addDownloadFinishedListener(entry, listenableFuture);
        return listenableFuture;
    }

    private Callable<Optional<Entry>> getDownloadCallable(Entry entry, List<DownloadProgressListener> downloadProgressListeners) {
        return () -> {
            try {
                URLConnection urlConnection = entry.from.openConnection();
                long totalSize = urlConnection.getContentLengthLong();
                DownloadProgressInputStream downloadProgressInputStream = new DownloadProgressInputStream(urlConnection.getInputStream(), totalSize);
                downloadProgressListeners.stream().forEach(downloadProgressInputStream::addDownloadProgressListener);
                Files.copy(downloadProgressInputStream, entry.to, StandardCopyOption.REPLACE_EXISTING);
                return Optional.of(entry);
            } catch (IOException e) {
                e.printStackTrace();
                return Optional.empty();
            }
        };
    }

    public Optional<Entry> getDownloadFileMain(DownloadInfo.DownloadLocation base) {
        return getDownloadFile(base, getRegex4Main(base));
    }


    public Optional<Entry> getDownloadFileSdk(DownloadInfo.DownloadLocation base) {
        return getDownloadFile(base, getRegex4Sdk(base));
    }

    public Optional<Entry> getDownloadFileHelp(DownloadInfo.DownloadLocation base, String hpLang) {
        return getDownloadFile(base, getRegex4Hp(base, hpLang));
    }

    public Optional<Entry> getDownloadFileLangPack(DownloadInfo.DownloadLocation base, String lang) {
        return Optional.empty();//TODO Implement
    }

    private Optional<Entry> getDownloadFile(DownloadInfo.DownloadLocation base, String regex) {
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

    private String getRegex4Main(DownloadInfo.DownloadLocation base) {
        if (base.getDownloadType() == DownloadType.Daily)
            return base.getVersionPrefix() + ".+?" + base.getOs().getOSShortName() + "_" + base.getA().getInFilenameArchitecture().toLowerCase() + "_[^h].+?[^sdk]\\." + base.getOs().getFileExtension() + '"';
        return "Lib(O_|re).+?" + base.getA().getInFilenameArchitecture().toLowerCase() + ("exe".equalsIgnoreCase(base.getOs().getFileExtension()) ? ".+?install_multi.exe\"" : "\\." + base.getOs().getFileExtension() + "[^\\.asc]");
    }

    private String getRegex4Sdk(DownloadInfo.DownloadLocation base) {
        return (base.getDownloadType() == DownloadType.Daily ? base.getVersionPrefix() : "Lib") + ".+?sdk\\." + base.getOs().getFileExtension() + '"';
    }

    private String getRegex4Hp(DownloadInfo.DownloadLocation base, String hpLang) {
        return (base.getDownloadType() == DownloadType.Daily ? base.getVersionPrefix() : "Lib") + ".+?helppack_" + hpLang + "\\." + base.getOs().getFileExtension() + '"';
    }


    @Override
    public void close() {
        cancel();
        executorService.shutdown();
    }

    @Override
    public void cancel() {
        totalDownloadProgress.cancel();
    }

    @Override
    public void cancel(Entry toCancel) {
        totalDownloadProgress.cancel(toCancel);
    }

    public static class Entry {
        private final URL from;
        private Path to;
        private String filename;

        public Entry(URL from) {
            this(from, null);
        }

        public Entry(URL from, Path to) {
            this.from = from;
            this.to = to;
        }

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public void setTo(Path to) {
            this.to = to;
        }

        public URL getFrom() {
            return from;
        }

        public Optional<Path> getTo() {
            return Optional.ofNullable(to);
        }

        @Override
        public String toString() {
            return String.format("Download from '%s' to '%s'!", from, to);
        }
    }

    private static class DownloadManagerTotalDownloadProgress implements DownloadProgressInfo, PartiallyCancelable<Entry> {
        private Map<Entry, DownloadProgressInfo> progressInfoMap = Collections.synchronizedMap(new HashMap<>());
        private Map<Entry, ListenableFuture<?>> listenableFutureMap = Collections.synchronizedMap(new HashMap<>());
        private long total = 0, downloaded = 0;
        private boolean hasStarted = false;
        private List<DownloadProgressListener> downloadProgressListeners = new ArrayList<>();

        public void addDownloadProgressListener(DownloadProgressListener listener) {
            downloadProgressListeners.add(listener);
        }

        public void removeDownloadProgressListener(DownloadProgressListener listener) {
            downloadProgressListeners.remove(listener);
        }

        private void fireDownloadProgressListener() {
            downloadProgressListener.accept(downloadProgressListeners);
        }

        private EevryXConsumer<List<DownloadProgressListener>> downloadProgressListener = new EevryXConsumer<>(1, l -> l.forEach(a -> a.downloadProgressChanged(this)));


        public DownloadProgressListener getDownloadProgressListener(Entry entry) {
            return DownloadProgressListener.onlyReactEvery512KB(d -> {
                hasStarted = true;
                DownloadProgressInfo progressInfo = progressInfoMap.put(entry, d);
                if (progressInfo == null) {
                    updateTotal();
                    downloadProgressListener.setX(progressInfoMap.size());
                }
                updateDownloaded();
                fireDownloadProgressListener();
            });
        }

        /**
         * @param entry            An entry
         * @param listenableFuture A ListenableFuture object of the same entry
         */
        public void addDownloadFinishedListener(Entry entry, ListenableFuture<?> listenableFuture) {
            listenableFutureMap.put(entry, listenableFuture);
            listenableFuture.addListener(() -> {
                progressInfoMap.remove(entry);
                listenableFutureMap.remove(entry);
                downloadProgressListener.setX(progressInfoMap.size());
                updateTotal();
                updateDownloaded();
                fireDownloadProgressListener();
            }, MoreExecutors.sameThreadExecutor());
        }

        private void updateTotal() {
            total = update(DownloadProgressInfo::getTotalSizeInBytes);
        }

        private void updateDownloaded() {
            downloaded = update(DownloadProgressInfo::getBytesTransferred);
        }

        private long update(ToLongFunction<DownloadProgressInfo> toLongFunction) {
            return progressInfoMap.values().stream().mapToLong(toLongFunction).sum();
        }


        @Override
        public boolean hasStarted() {
            return hasStarted;
        }

        @Override
        public void resetHasStarted() throws IllegalStateException {
            if (progressInfoMap.size() != 0 || listenableFutureMap.size() != 0)
                throw new IllegalStateException("You have to cancel all downloads / let all download finish prior to resetting!");
            hasStarted = false;
        }


        @Override
        public long getTotalSizeInBytes() {
            return total;
        }

        @Override
        public long getBytesTransferred() {
            return downloaded;
        }

        @Override
        public void cancel() {
            for (ListenableFuture<?> lf : listenableFutureMap.values())
                lf.cancel(true);
            progressInfoMap.clear();
            listenableFutureMap.clear();
        }

        @Override
        public void cancel(Entry toCancel) {
            progressInfoMap.remove(toCancel);
            ListenableFuture<?> lf = listenableFutureMap.remove(toCancel);
            if (lf != null)
                lf.cancel(true);
        }
    }
}
