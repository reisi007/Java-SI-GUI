package at.reisisoft.sigui;

import at.reisisoft.sigui.collection.CollectionHashMap;
import at.reisisoft.sigui.downloader.DownloadManager;
import at.reisisoft.sigui.downloader.DownloadProgressListener;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;

public class Main {
    private static Scanner console = new Scanner(System.in);

    public static void main(String[] args) throws Exception {
        //cmdDownload();
        DownloadInfo.DownloadLocation location = new DownloadInfo.DownloadLocation("Testing 5.0", "5.0.0", "http://download.documentfoundation.org/libreoffice/testing/5.0.0/win/x86_64/", Architecture.X86_64, OS.WinMsi);
        System.out.println(location);
        try (DownloadInfo info = new DownloadInfo()) {
            info.getAllLanguages(location);
        }
    }

    private static void cmdDownload() throws Exception {
        try (DownloadInfo d = new DownloadInfo();
             DownloadManager downloadManager = new DownloadManager()) {
            System.out.println("Hi! I am SI-GUI!");

            OS[] oss = OS.detect();
            OS os = null;

            Architecture a = Architecture.detect();
            if (oss.length == 0)
                oss = OS.values();
            else if (oss.length == 1)
                os = oss[0];
            if (os == null) {
                System.out.format("Choose your OS:%n");
                printArrayIndexed(oss, OS::getOSLongName);
                int choice = readChoice(1, oss.length);
                os = oss[choice - 1];
            }
            System.out.format("You chose %s - %s!%n", os.getOSLongName(), a);
            System.out.println("Fetching possible downloads in the background!");
            ListenableFuture<CollectionHashMap<DownloadType, SortedSet<DownloadInfo.DownloadLocation>, DownloadInfo.DownloadLocation>> allAvailableDownloads = d.getAllAvailableDownloads(a, os);
            DownloadType[] downloadTypes = DownloadType.values();
            System.out.println("In which of the following dowloadTypes are you interested");
            printArrayIndexed(downloadTypes);
            int choice = readChoice(1, downloadTypes.length);
            DownloadType dt = downloadTypes[choice - 1];
            boolean main = false, sdk = false, help = false;
            String helpLang = "en_US";
            while (!main && !sdk && !help) {
                console.nextLine();
                System.out.println("Which files do you want to download. Type 'm' for the main program, 's' for the SDK and 'h' for the help");
                String tmp = console.nextLine();
                main = tmp.indexOf('m') >= 0;
                sdk = tmp.indexOf('s') >= 0;
                help = tmp.indexOf('h') >= 0;
                if (help) {
                    System.out.format("You choose help file. Which language do you want the help file? (Case sensitive code needed! Default: %s)%n", helpLang);
                    tmp = console.nextLine();
                    if (tmp.length() > 0)
                        helpLang = tmp;
                }
            }
            System.out.format("You have shown interest in %s.%nWe now wait for the fetching to complete.", dt);

            CollectionHashMap<DownloadType, SortedSet<DownloadInfo.DownloadLocation>, DownloadInfo.DownloadLocation> s1 = allAvailableDownloads.get();
            System.out.println("Download finished!");
            DownloadInfo.DownloadLocation[] locations = s1.get(dt).get().toArray(new DownloadInfo.DownloadLocation[0]);
            printArrayIndexed(locations);
            DownloadProgressListener listener = e -> System.out.format("%s of %s (%.2f %%)%n", e.getBytesTransferred(), e.getTotalSizeInBytes(), e.getPercent() * 100);
            downloadManager.addTotalDownloadProgressListener(listener);
            choice = readChoice(1, locations.length);
            DownloadInfo.DownloadLocation location = locations[choice - 1];
            Optional<DownloadManager.Entry> fileMain = null, fileHelp = null, fileSdk = null;
            Collection<Optional<DownloadManager.Entry>> dlCollection = new ArrayList<>();
            if (main) dlCollection.add(downloadManager.getDownloadFileMain(location));
            if (help) dlCollection.add(downloadManager.getDownloadFileHelp(location, helpLang));
            if (sdk) dlCollection.add(downloadManager.getDownloadFileSdk(location));
            Path f = getTmpFolder();
            System.out.format("Download will be started to %s%n", f);
            DownloadManager.Entry[] array = dlCollection.stream().filter(Optional::isPresent).map(Optional::get).map(entry -> {
                entry.setTo(f.resolve(entry.getFilename()));
                return entry;
            }).peek(e -> System.out.println("Downloading: " + e)).toArray(DownloadManager.Entry[]::new);
            System.out.println("Downloading... This also hapens in the background");
            ListenableFuture<Optional<DownloadManager.Entry>>[] listenableFutures = new ListenableFuture[array.length];
            for (int i = 0; i < array.length; i++)
                listenableFutures[i] = downloadManager.submit(array[i]);
            Arrays.stream(listenableFutures).map(Utils.mapFuture()).filter(Optional::isPresent).map(Optional::get).forEach(e -> System.out.println("Downloaded: " + e));
            System.out.println("Download completed, program will exit now!");
        }
    }

    private static Path getTmpFolder() {
        try {
            return Files.createTempDirectory("java-si-gui");
        } catch (IOException e) {
            return new File(".").toPath();
        }
    }

    private static <T> void printArrayIndexed(T[] a) {
        printArrayIndexed(a, T::toString);
    }

    private static <T> void printArrayIndexed(T[] a, Function<T, String> function) {
        for (int i = 0; i < a.length; i++)
            System.out.format("%s -> %s%n", i + 1, function.apply(a[i]));
    }

    private static int readChoice(int lowerBound, int upperbound) {
        boolean valid = false;
        int choice = Integer.MAX_VALUE;
        while (!valid)
            try {
                choice = console.nextInt();
                if (choice < lowerBound || choice > upperbound)
                    throw new IllegalArgumentException("Not in range!");
                valid = true;
            } catch (Exception e) {
                System.err.format("No valid int or not between 1 and %s!%n", upperbound);
                System.out.println("Retry");
            }
        return choice;
    }
}
