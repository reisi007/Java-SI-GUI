package at.reisisoft.sigui.downloader;

/**
 * Created by Florian on 25.06.2015.
 */
@FunctionalInterface
public interface DownloadProgressListener {

    static DownloadProgressListener onlyReactEvery512KB(DownloadProgressListener listener) {
        return onlyReactEveryxByte(524288, listener);
    }

    static DownloadProgressListener onlyReactEveryxByte(final long x, final DownloadProgressListener listener) {
        return new DownloadProgressListener() {
            long next = x;

            @Override
            public void downloadProgressChanged(DownloadProgressInfo downloadProgressInfo) {
                long currentBytes = downloadProgressInfo.getBytesTransferred();
                if (currentBytes > next) {
                    next = currentBytes + x;
                    listener.downloadProgressChanged(downloadProgressInfo);
                }
            }
        };
    }

    void downloadProgressChanged(DownloadProgressInfo downloadProgressInfo);
}
