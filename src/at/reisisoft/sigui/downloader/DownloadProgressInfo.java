package at.reisisoft.sigui.downloader;

/**
 * Created by Florian on 25.06.2015.
 */
public interface DownloadProgressInfo {

    long getTotalSizeInBytes();

    long getBytesTransferred();

    default boolean isFinished() {
        return getBytesTransferred() >= getTotalSizeInBytes();
    }

    default double getPercent() {
        long bytes = getBytesTransferred(), total = getTotalSizeInBytes();
        if (total == 0)
            if (bytes == 0)
                return 0d;
            else
                return Double.NaN;
        return ((double) bytes) / total;
    }

}
