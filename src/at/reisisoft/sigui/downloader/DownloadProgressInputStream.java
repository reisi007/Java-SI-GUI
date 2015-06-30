package at.reisisoft.sigui.downloader;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Florian on 25.06.2015.
 */
public class DownloadProgressInputStream extends InputStream implements DownloadProgressInfo {
    private final InputStream i;
    private final long totalSizeInBytes;
    private long bytesTransferred = 0;
    private final ArrayList<DownloadProgressListener> downloadProgressListeners = new ArrayList<>();

    public DownloadProgressInputStream(InputStream i, long totalSizeInBytes) {
        this.i = i;
        this.totalSizeInBytes = totalSizeInBytes;
    }

    @Override
    public int read(byte[] b) throws IOException {
        int read = i.read(b);
        if (read >= 0) {
            bytesTransferred += read;
            fireProgressListener();
        }
        return read;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int read = i.read(b, off, len);
        if (read >= 0) {
            bytesTransferred += read;
            fireProgressListener();
        }
        return read;
    }

    @Override
    public long skip(long n) throws IOException {
        long skipped = i.skip(n);
        if (skipped >= 0) {
            bytesTransferred += skipped;
            fireProgressListener();
        }
        return skipped;
    }

    @Override
    public int read() throws IOException {
        int read = i.read();
        if (read >= 0) {
            bytesTransferred++;
            fireProgressListener();
        }
        return read;
    }

    @Override
    public int available() throws IOException {
        return i.available();
    }

    @Override
    public void close() throws IOException {
        i.close();
        super.close();
    }

    @Override
    public synchronized void mark(int readlimit) {
        i.mark(readlimit);
    }

    @Override
    public synchronized void reset() throws IOException {
        i.reset();
    }

    @Override
    public boolean markSupported() {
        return i.markSupported();
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

    public long getTotalSizeInBytes() {
        return totalSizeInBytes;
    }

    public long getBytesTransferred() {
        return bytesTransferred;
    }

}
