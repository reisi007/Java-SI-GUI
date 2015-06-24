package at.reisisoft;

import at.reisisoft.collection.CollectionHashMap;

import java.util.SortedSet;

public class Main {

    public static void main(String[] args) {
        try (Download d = new Download()) {
            long start = System.currentTimeMillis();
            CollectionHashMap<Download.DownloadType, SortedSet<Download.Entry>, Download.Entry> map = d.getAllAvailableDownloads(Architecture.X86, OS.Win);
            long total = System.currentTimeMillis() - start;
            System.out.format("Finished in %s ms.%n", total);
            for (Download.DownloadType dt : map.getKeySet()) {
                SortedSet<Download.Entry> set = map.get(dt).get();
                System.out.format("Found %s downloads of type %s:%n", set.size(), dt);
                set.forEach(System.out::println);
            }
        }
    }
}
