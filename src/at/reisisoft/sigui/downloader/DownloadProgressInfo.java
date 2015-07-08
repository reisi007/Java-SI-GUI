package at.reisisoft.sigui.downloader;

/**
 * Created by Florian on 25.06.2015.
 */
public interface DownloadProgressInfo {
    /**
     * Indicates whether the download has already been started
     *
     * @return {@value true} if the download has alreqady been started, else {@value false}
     */
    boolean hasStarted();

    /**
     * Sets the {@link #hasStarted()} to false
     *
     * @throws IllegalStateException
     */
    void resetHasStarted() throws IllegalStateException;

    long getTotalSizeInBytes();

    long getBytesTransferred();

    default boolean isFinished() {
        return hasStarted() && getBytesTransferred() >= getTotalSizeInBytes();
    }

    default double getPercent() {
        long bytes = getBytesTransferred(), total = getTotalSizeInBytes();
        if (total == 0)
            if (bytes == 0)
                return hasStarted() ? 1d : 0d;
            else
                return Double.NaN;
        return ((double) bytes) / total;
    }

}
