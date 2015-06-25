package at.reisisoft.sigui.downloader;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Florian on 25.06.2015.
 */
public class CountableInputStream extends InputStream {
    private final InputStream i;
    private final long totalSizeInBytes;
    private long bytesTransferred = 0;

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

    public long getTotalSizeInBytes() {
        return totalSizeInBytes;
    }

    public long getBytesTransferred() {
        return bytesTransferred;
    }

}
