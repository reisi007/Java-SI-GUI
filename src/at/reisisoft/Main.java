package at.reisisoft;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        try (Download d = new Download()) {
            OSSettings[] settings = {OSSettings.of(Architecture.X86, OS.Win), OSSettings.of(Architecture.X86_64, OS.Win),
                    OSSettings.of(Architecture.X86, OS.LinuxDeb), OSSettings.of(Architecture.X86_64, OS.LinuxRPM),
                    OSSettings.of(Architecture.X86_64, OS.Mac), OSSettings.of(Architecture.X86, OS.Mac)};
            System.out.format("Found %s downloads in %s ms!%n", Arrays.stream(settings).parallel().map(osSettings -> d.getAllAvailableDownloads(osSettings.getArch(), osSettings.getOs())).collect(Utils.collectCollectionToStream()).peek(System.out::println).count(), System.currentTimeMillis() - start);
        }
    }
}
