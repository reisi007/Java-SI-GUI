package at.reisisoft.sigui.ui.main;

import at.reisisoft.sigui.*;
import at.reisisoft.sigui.collection.CollectionHashMap;
import at.reisisoft.sigui.collection.SortedSetHashMap;
import at.reisisoft.sigui.l10n.LocalisationSupport;
import at.reisisoft.sigui.settings.SiGuiSettings;
import at.reisisoft.sigui.ui.RunsOnJavaFXThread;
import com.google.common.util.concurrent.ListenableFuture;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.SortedSet;
import java.util.stream.Stream;

/**
 * Created by Florian on 11.07.2015.
 */
public class MainUiDownloadTab extends Tab {

    private static MainUiDownloadTab instance = null;
    private final Accordion accordion = new Accordion();
    private CheckBox main, sdk, hp;
    private HBox firstRow = new HBox(8), secondRow = new HBox(8), thirdrow = new HBox(8);

    public static MainUiDownloadTab getInstance(LocalisationSupport localisationSupport) {
        if (instance == null) {
            Objects.requireNonNull(localisationSupport);
            instance = new MainUiDownloadTab(localisationSupport);
        }
        return instance;
    }

    private final LocalisationSupport localisationSupport;

    private MainUiDownloadTab(LocalisationSupport localisationSupport) {
        super(localisationSupport.getString(MainUITab.DOWNLOAD));
        this.localisationSupport = localisationSupport;
        VBox mainContent = new VBox();
        setContent(mainContent);
        firstRow.setAlignment(Pos.CENTER);
        secondRow.setAlignment(Pos.CENTER);
        thirdrow.setAlignment(Pos.CENTER);
        mainContent.getChildren().addAll(firstRow, secondRow, thirdrow);
        //First row -> Download entry accordion + Update accordion -> Progressindicator
        updateAccordeon(null);
        Button button = new Button(localisationSupport.getString(MainUiTranslation.DOWNLOAD_UPDATE));

        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setVisible(false);
        firstRow.getChildren().addAll(accordion, button, progressIndicator);

        button.setOnAction(event -> {
            progressIndicator.setVisible(true);
            Runnable r = () -> {
                SiGuiSettings settings = MainUi.getSettingsInstance();
                List<ListenableFuture<CollectionHashMap<DownloadType, SortedSet<DownloadInfo.DownloadLocation>, DownloadInfo.DownloadLocation>>> futureList = new LinkedList<>();
                try (DownloadInfo downloadInfo = new DownloadInfo()) {
                    for (Architecture a : settings.getArchitectures())
                        for (OS os : settings.getOSs())
                            futureList.add(downloadInfo.getAllAvailableDownloads(a, os));
                    SortedSetHashMap<DownloadType, DownloadInfo.DownloadLocation> sortedSetHashMap = new SortedSetHashMap<>();
                    Stream<CollectionHashMap<DownloadType, SortedSet<DownloadInfo.DownloadLocation>, DownloadInfo.DownloadLocation>> step1 = futureList.stream().map(Utils.mapFuture());
                    for (CollectionHashMap<DownloadType, SortedSet<DownloadInfo.DownloadLocation>, DownloadInfo.DownloadLocation> cur : Utils.iterableFromStream(step1)) {
                        sortedSetHashMap.put(cur);
                    }
                    Platform.runLater(() -> {
                        updateAccordeon(sortedSetHashMap);
                        progressIndicator.setVisible(false);
                    });
                }
            };
            new Thread(r).start();
        });

        //Second row -> 3 checkboxes main, help, sdk
        main = new CheckBox(localisationSupport.getString(MainUiTranslation.INSTALLER_MAIN));
        sdk = new CheckBox(localisationSupport.getString(MainUiTranslation.INSTALLER_SDK));
        hp = new CheckBox(localisationSupport.getString(MainUiTranslation.INSTALLER_HELP));

        secondRow.getChildren().addAll(mainContent, hp, sdk);
    }

    @RunsOnJavaFXThread
    /**
     * Updates the accordion with the given.
     *
     * @param downloads Either NULL (fetching from settings) or the CollectionHashMap from which it will be updated
     */
    public void updateAccordeon(CollectionHashMap<DownloadType, SortedSet<DownloadInfo.DownloadLocation>, DownloadInfo.DownloadLocation> downloads) {
        SiGuiSettings settings = MainUi.getSettingsInstance();

        if (downloads != null)
            settings.setCachedDownloads(downloads);

        Accordion a = DataToUiUtils.getAccordeonFromCollectionHashMap(settings.getCachedDownloads(), localisationSupport, accordion);
        assert a == accordion : "UI accordeon is out of sync!";
    }
}
