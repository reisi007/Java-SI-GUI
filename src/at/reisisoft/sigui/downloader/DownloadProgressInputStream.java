package at.reisisoft.sigui.downloader;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Florian on 25.06.2015.
 */
public class DownloadProgressInputStream extends FilterInputStream implements DownloadProgressInfo {
    private final long totalSizeInBytes;
    private long bytesTransferred = 0;
    private final ArrayList<DownloadProgressListener> downloadProgressListeners = new ArrayList<>();
    private boolean started = false;

    public DownloadProgressInputStream(InputStream i, long totalSizeInBytes) {
        super(i);
        this.totalSizeInBytes = totalSizeInBytes;
    }

    @Override
    public int read(byte[] b) throws IOException {
        int read = in.read(b);
        if (read >= 0) {
            bytesTransferred += read;
            fireProgressListener();
        }
        started = true;
        return read;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int read = in.read(b, off, len);
        if (read >= 0) {
            bytesTransferred += read;
            fireProgressListener();
        }
        started = true;
        return read;
    }

    @Override
    public long skip(long n) throws IOException {
        long skipped = in.skip(n);
        if (skipped >= 0) {
            bytesTransferred += skipped;
            fireProgressListener();
        }
        started = true;
        return skipped;
    }

    @Override
    public int read() throws IOException {
        int read = in.read();
        if (read >= 0) {
            bytesTransferred++;
            fireProgressListener();
        }
        started = true;
        return read;
    }

    private void fireProgressListener() {
        downloadProgressListeners.stream().forEach(e -> new Thread(() -> e.downloadProgressChanged(this)).start());
    }

    public void addDownloadProgressListener(DownloadProgressListener listener) {
        downloadProgressListeners.add(listener);
    }

    public void removeDownloadProgressListener(DownloadProgressListener listener) {
        downloadProgressListeners.remove(listener);
    }

    @Override
    public boolean hasStarted() {
        return started;
    }


    public long getTotalSizeInBytes() {
        return totalSizeInBytes;
    }

    public long getBytesTransferred() {
        return bytesTransferred;
    }

}
