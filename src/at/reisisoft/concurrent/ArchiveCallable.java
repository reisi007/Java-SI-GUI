package at.reisisoft.concurrent;

import at.reisisoft.*;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Callable;

/**
 * Created by Florian on 23.06.2015.
 */
public class ArchiveCallable implements Callable<Collection<ListenableFuture<Collection<Download.Entry>>>> {
    private Architecture a;
    private OS os;

    public ArchiveCallable(Architecture a, OS os) {
        this.a = a;
        this.os = os;
    }

    @Override
    public Collection<ListenableFuture<Collection<Download.Entry>>> call() throws Exception {
        new VersionExtractor(Constants.LIBO_ARCHIVE_URL).call().parallelStream().
                map(f -> new Utils.KeyValuePair<>(f, Constants.LIBO_ARCHIVE_URL + f + '/' + os.toString().toLowerCase() + '/' + a.toString().toLowerCase()))
                .filter(kvp -> new Download.DownloadAvailablePredicate(os, kvp.getKey()).test(kvp.getValue())).
                map(keyValuePair -> new Download.Entry(keyValuePair.getKey(), keyValuePair.getValue(), a, os)).forEach(System.out::println);
        //TODO Match return type and thus better make it parallel

        return Collections.emptyList();
    }
}
