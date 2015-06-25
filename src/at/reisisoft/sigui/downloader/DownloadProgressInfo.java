package at.reisisoft.sigui.downloader;

/**
 * Created by Florian on 25.06.2015.
 */
public interface DownloadProgressInfo {

    long getTotalSizeInBytes();

    long getBytesTransferred();

    default boolean isFinished() {
        return getBytesTransferred() == getTotalSizeInBytes();
    }

}
