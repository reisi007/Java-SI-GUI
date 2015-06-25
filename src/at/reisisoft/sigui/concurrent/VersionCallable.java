package at.reisisoft.sigui.concurrent;

import at.reisisoft.sigui.*;
import at.reisisoft.sigui.collection.CollectionHashMap;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Florian on 24.06.2015.
 */
public class VersionCallable implements Callable<Collection<Download.DownloadLocation>> {

    protected final Architecture a;
    protected final OS os;
    private ListeningExecutorService executorService;
    private final String prefix, url;

    public VersionCallable(Architecture a, OS os, ListeningExecutorService executorService, String url, String prefix) {
        this.a = a;
        this.os = os;
        this.executorService = executorService;
        this.prefix = prefix;
        this.url = url;
    }

    @Override
    public Collection<Download.DownloadLocation> call() throws Exception {
        Stream<CollectionHashMap.KeyValuePair<String, Download.DownloadLocation>> step1 = Downloads.getLibOVersions(url).parallelStream().map(m -> new CollectionHashMap.KeyValuePair<>(m, new Download.DownloadLocation(m, prefix, url + m + '/' + os.toString().toLowerCase() + '/' + a.toString().toLowerCase() + '/', a, os)));
        Stream<CollectionHashMap.KeyValuePair<Download.DownloadLocation, CallablePredicate>> step2 = step1.map(kvp -> new CollectionHashMap.KeyValuePair<>(kvp.getValue(), new CallablePredicate<>(new DownloadAvailablePredicate(os, kvp.getKey()), kvp.getValue().getUrl())));
        Stream<CollectionHashMap.KeyValuePair<Download.DownloadLocation, ListenableFuture<Boolean>>> step2b = step2.map(kvp -> new CollectionHashMap.KeyValuePair<>(kvp.getKey(), executorService.submit(kvp.getValue())));
        Collection<CollectionHashMap.KeyValuePair<Download.DownloadLocation, ListenableFuture<Boolean>>> step3 = step2b.collect(Collectors.toList());
        return step3.parallelStream().filter(e -> {
            try {
                return e.getValue().get();
            } catch (Exception e1) {
                e1.printStackTrace();
                return false;
            }
        }).map(CollectionHashMap.KeyValuePair::getKey).collect(Collectors.toList());
    }
}
