package at.reisisoft;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

public class Main {

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        try (Download d = new Download()) {
            OSSettings[] settings = {OSSettings.of(Architecture.X86, OS.Win), OSSettings.of(Architecture.X86_64, OS.Win),
                    OSSettings.of(Architecture.X86, OS.Linux), OSSettings.of(Architecture.X86_64, OS.Linux),
                    OSSettings.of(Architecture.X86_64, OS.Mac), OSSettings.of(Architecture.X86, OS.Mac)};
            Set<Download.Entry> fin = new TreeSet<>(
                    Arrays.stream(settings).parallel().map(osSettings -> d.getAllAvailableDownloads(osSettings.getArch(), osSettings.getOs())).collect(Utils.collectCollectionsToSingleCollection()));
            long end = System.currentTimeMillis();
            System.out.format("Found %s thinderboxes in %s ms!%n", fin.size(), end - start);
            fin.forEach(System.out::println);
        }

    }
}
