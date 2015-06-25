package at.reisisoft.sigui.concurrent;

import at.reisisoft.sigui.Architecture;
import at.reisisoft.sigui.Constants;
import at.reisisoft.sigui.OS;
import com.google.common.util.concurrent.ListeningExecutorService;

/**
 * Created by Florian on 23.06.2015.
 */
public class TestingCallable extends VersionCallable {

    public static final String PREFIX = "Testing-";

    public TestingCallable(Architecture a, OS os, ListeningExecutorService executorService) {
        super(a, os, executorService, Constants.LIBO_TESTING_URL, PREFIX);
    }
}
