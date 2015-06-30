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

    static DownloadProgressListener onlyReactEveryXTimes(final int x, final DownloadProgressListener listener) {
        return new DownloadProgressListener() {
            private EevryXConsumer<DownloadProgressInfo> eevryXConsumer = new EevryXConsumer<>(x, listener::downloadProgressChanged);

            @Override
            public void downloadProgressChanged(DownloadProgressInfo downloadProgressInfo) {
                eevryXConsumer.accept(downloadProgressInfo);
            }
        };
    }

    void downloadProgressChanged(DownloadProgressInfo downloadProgressInfo);
}
