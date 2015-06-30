package at.reisisoft.sigui;

import at.reisisoft.sigui.collection.CollectionHashMap;
import at.reisisoft.sigui.downloader.DownloadManager;
import at.reisisoft.sigui.downloader.DownloadProgressListener;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

public class Main {
    private static Scanner console = new Scanner(System.in);

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        try (DownloadInfo d = new DownloadInfo();
             DownloadManager downloadManager = new DownloadManager()) {

            System.out.println("Hi, I am Java SI-GUI!");
            OS[] oss = OS.detect();
            OS os = null;
            Architecture[] arch = Architecture.values();
            Architecture a = null;
            if (oss.length == 0)
                oss = OS.values();
            else if (oss.length == 1)
                os = oss[0];
            if (os == null) {
                System.out.format("Choose your OS:%n");
                printArrayIndexed(oss, OS::getOSLongName);
                int choice = readChoice(1, oss.length);
                os = oss[choice - 1];
                System.out.format("Choose your System architecture%n");
            }
            printArrayIndexed(arch);
            int choice = readChoice(1, arch.length);
            a = arch[choice - 1];
            System.out.format("You chose %s - %s!%n", os.getOSLongName(), a);
            System.out.println("Fetching possible downloads in the background!");
            ListenableFuture<CollectionHashMap<DownloadInfo.DownloadType, SortedSet<DownloadInfo.DownloadLocation>, DownloadInfo.DownloadLocation>> allAvailableDownloads = d.getAllAvailableDownloads(a, os);
            DownloadInfo.DownloadType[] downloadTypes = DownloadInfo.DownloadType.values();
            System.out.println("In which of the following dowloadTypes are you interested");
            printArrayIndexed(downloadTypes);
            choice = readChoice(1, downloadTypes.length);
            DownloadInfo.DownloadType dt = downloadTypes[choice - 1];
            System.out.format("You have shown interest in %s.%nWe now wait for the fetching to complete.", dt);

            CollectionHashMap<DownloadInfo.DownloadType, SortedSet<DownloadInfo.DownloadLocation>, DownloadInfo.DownloadLocation> s1 = allAvailableDownloads.get();
            System.out.println("Download finished!");
            DownloadInfo.DownloadLocation[] locations = s1.get(dt).get().toArray(new DownloadInfo.DownloadLocation[0]);
            printArrayIndexed(locations);
            DownloadProgressListener listener = e -> System.out.format("%s of %s (%.2f %%)%n", e.getBytesTransferred(), e.getTotalSizeInBytes(), e.getPercent() * 100);
            downloadManager.addTotalDownloadProgressListener(listener);
            choice = readChoice(1, locations.length);
            DownloadInfo.DownloadLocation location = locations[choice - 1];
            Optional<DownloadManager.Entry> fileMain = downloadManager.getDownloadFileMain(location);
            if (!fileMain.isPresent()) {
                System.out.println("No download available");
                return;
            }
            DownloadManager.Entry entry = fileMain.get();
            entry.setTo(File.createTempFile("test", "123"));
            System.out.println("Downloading... This also hapens in the background");
            ListenableFuture<Optional<DownloadManager.Entry>> submit = downloadManager.submit(entry);
            submit.get();
            System.out.println("Download completed, program will exit now!");
        } catch (IOException e) {
            e.printStackTrace();
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
