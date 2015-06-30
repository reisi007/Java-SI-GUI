package at.reisisoft.sigui.downloader;

import at.reisisoft.sigui.DownloadInfo;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
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
public class DownloadManager implements AutoCloseable {
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
                Files.copy(downloadProgressInputStream, entry.to.toPath(), StandardCopyOption.REPLACE_EXISTING);
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
        if (base.getDownloadType() == DownloadInfo.DownloadType.Daily)
            return base.getVersionPrefix() + ".+?" + base.getOs().getOSShortName() + "_" + base.getA().getInFilenameArchitecture().toLowerCase() + "_[^h].+?[^sdk]\\." + base.getOs().getFileExtension() + '"';
        return "Lib(O_|re).+?" + base.getA().getInFilenameArchitecture().toLowerCase() + ("exe".equalsIgnoreCase(base.getOs().getFileExtension()) ? ".+?install_multi.exe\"" : "\\." + base.getOs().getFileExtension() + "[^\\.asc]");
    }

    private String getRegex4Sdk(DownloadInfo.DownloadLocation base) {
        return (base.getDownloadType() == DownloadInfo.DownloadType.Daily ? base.getVersionPrefix() : "Lib") + ".+?sdk\\." + base.getOs().getFileExtension() + "<";
    }

    private String getRegex4Hp(DownloadInfo.DownloadLocation base, String hpLang) {
        return (base.getDownloadType() == DownloadInfo.DownloadType.Daily ? base.getVersionPrefix() : "Lib") + ".+?helppack_" + hpLang + "\\." + base.getOs().getFileExtension() + "<";
    }


    @Override
    public void close() {
        executorService.shutdown();
    }

    public static class Entry {
        private final URL from;
        private File to;
        private String filename;

        public Entry(URL from) {
            this(from, null);
        }

        public Entry(URL from, File to) {
            this.from = from;
            this.to = to;
        }

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public void setTo(File to) {
            this.to = to;
        }

        public URL getFrom() {
            return from;
        }

        public Optional<File> getTo() {
            return Optional.ofNullable(to);
        }
    }

    private static class DownloadManagerTotalDownloadProgress implements DownloadProgressInfo {
        private Map<Entry, DownloadProgressInfo> hashMap = Collections.synchronizedMap(new HashMap<>());
        private long total = 0, downloaded = 0;
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
                DownloadProgressInfo progressInfo = hashMap.put(entry, d);
                if (progressInfo == null) {
                    updateTotal();
                    downloadProgressListener.setX(hashMap.size());
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
            listenableFuture.addListener(() -> {
                hashMap.remove(entry);
                downloadProgressListener.setX(hashMap.size());
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
            return hashMap.values().stream().mapToLong(toLongFunction).sum();
        }


        @Override
        public long getTotalSizeInBytes() {
            return total;
        }

        @Override
        public long getBytesTransferred() {
            return downloaded;
        }
    }
}
