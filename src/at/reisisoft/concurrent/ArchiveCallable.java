package at.reisisoft.concurrent;

import at.reisisoft.Architecture;
import at.reisisoft.Constants;
import at.reisisoft.OS;
import com.google.common.util.concurrent.ListeningExecutorService;

/**
 * Created by Florian on 23.06.2015.
 */
public class ArchiveCallable extends VersionCallable {

    public static final String PREFIX = "Archive-";

    public ArchiveCallable(Architecture a, OS os, ListeningExecutorService executorService) {
        super(a, os, executorService, Constants.LIBO_ARCHIVE_URL, PREFIX);
    }
}
