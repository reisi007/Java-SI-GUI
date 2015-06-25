package at.reisisoft.sigui;

import at.reisisoft.sigui.collection.CollectionHashMap;

import java.util.SortedSet;
import java.util.concurrent.ExecutionException;

public class Main {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        try (Download d = new Download()) {
            long start = System.currentTimeMillis();
            CollectionHashMap<Download.DownloadType, SortedSet<Download.DownloadLocation>, Download.DownloadLocation> map = d.getAllAvailableDownloads(Architecture.X86, OS.Win).get();
            long total = System.currentTimeMillis() - start;
            System.out.format("Finished in %s ms.%n", total);
            for (Download.DownloadType dt : map.getKeySet()) {
                SortedSet<Download.DownloadLocation> set = map.get(dt).get();
                System.out.format("Found %s downloads of type %s:%n", set.size(), dt);
                set.forEach(System.out::println);
            }
        }
    }
}
