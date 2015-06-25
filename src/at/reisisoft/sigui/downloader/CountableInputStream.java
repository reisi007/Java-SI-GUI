package at.reisisoft.sigui.downloader;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Florian on 25.06.2015.
 */
public class CountableInputStream extends InputStream implements DownloadProgressInfo {
    private final InputStream i;
    private final long totalSizeInBytes;
    private long bytesTransferred = 0;
    private final ArrayList<DownloadProgressListener> downloadProgressListeners = new ArrayList<>();

    public CountableInputStream(InputStream i, long totalSizeInBytes) {
        this.i = i;
        this.totalSizeInBytes = totalSizeInBytes;
    }

    @Override
    public int read(byte[] b) throws IOException {
        bytesTransferred += b.length;
        return super.read(b);
    }

    @Override
    public int read() throws IOException {
        bytesTransferred++;
        return i.read();
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
