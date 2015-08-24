package at.reisisoft.sigui.ui.main;

import at.reisisoft.sigui.*;
import at.reisisoft.sigui.collection.CollectionHashMap;
import at.reisisoft.sigui.collection.SortedSetHashMap;
import at.reisisoft.sigui.downloader.DownloadManager;
import at.reisisoft.sigui.downloader.DownloadProgressInfo;
import at.reisisoft.sigui.l10n.LocalisationSupport;
import at.reisisoft.sigui.settings.SiGuiSettings;
import at.reisisoft.sigui.ui.RunsOnJavaFXThread;
import at.reisisoft.sigui.ui.controls.DownloadAccordion;
import com.google.common.io.Files;
import com.google.common.util.concurrent.ListenableFuture;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

/**
 * Created by Florian on 11.07.2015.
 */
public class MainUiDownloadTab extends Tab implements AutoCloseable {

    private static MainUiDownloadTab instance = null;
    private final DownloadAccordion accordion = new DownloadAccordion();
    private final ChoiceBox<String> languages = new ChoiceBox<>();
    private DownloadManager downloadManager = new DownloadManager();
    private ProgressBar progressBar = new ProgressBar(0);
    private final Window window;

    public static MainUiDownloadTab getInstance(LocalisationSupport localisationSupport, Window window) {
        if (instance == null) {
            Objects.requireNonNull(localisationSupport);
            instance = new MainUiDownloadTab(localisationSupport, window);
        }
        return instance;
    }

    private final LocalisationSupport localisationSupport;

    private MainUiDownloadTab(LocalisationSupport localisationSupport, Window window) {
        super(localisationSupport.getString(MainUITab.DOWNLOAD));
        this.localisationSupport = localisationSupport;
        this.window = window;

        final HBox firstRow = new HBox(8), secondRow = new HBox(8), thirdrow = new HBox(8), fourthRow = new HBox(8), fifthRow = new HBox(8), sixthRow = new HBox(8);
        final SiGuiSettings settings = MainUi.getSettingsInstance();
        VBox mainContent = new VBox(6);
        setContent(mainContent);
        firstRow.setAlignment(Pos.CENTER);
        secondRow.setAlignment(Pos.CENTER);
        thirdrow.setAlignment(Pos.CENTER);
        fourthRow.setAlignment(Pos.CENTER);
        fifthRow.setAlignment(Pos.CENTER);
        sixthRow.setAlignment(Pos.CENTER);

        mainContent.getChildren().addAll(firstRow, secondRow, thirdrow, fourthRow, fifthRow, sixthRow);
        //First row -> Download entry accordion + Update accordion -> Progressindicator
        updateAccordeon(null);
        accordion.setSelectedDownloadLocations(settings.getSelectedDownloadLocations());
        Button button = new Button(localisationSupport.getString(MainUiTranslation.DOWNLOAD_UPDATE));

        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setVisible(false);
        firstRow.getChildren().addAll(accordion, button, progressIndicator);

        button.setOnAction(event -> {
            progressIndicator.setVisible(true);
            Runnable r = () -> {
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
                    sortedSetHashMap.get(DownloadType.Archive).ifPresent(downloadLocations -> {
                        DownloadInfo.DownloadLocation max = downloadLocations.last();
                        try {
                            settings.setAvailableLanguages(downloadInfo.getAllLanguages(max).get());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });

                    Platform.runLater(() -> {
                                updateAccordeon(sortedSetHashMap);
                                ObservableList<String> observableList = languages.getItems();
                                observableList.clear();
                                observableList.addAll(settings.getAvailableLanguages());
                                accordion.setSelectedDownloadLocations(settings.getSelectedDownloadLocations());
                                progressIndicator.setVisible(false);
                            }
                    );
                }
            };
            new Thread(r).start();
        });

        //Second row -> 3 checkboxes main, help, sdk
        CheckBox main = new CheckBox(localisationSupport.getString(MainUiTranslation.INSTALLER_MAIN));
        CheckBox sdk = new CheckBox(localisationSupport.getString(MainUiTranslation.INSTALLER_SDK));
        CheckBox hp = new CheckBox(localisationSupport.getString(MainUiTranslation.INSTALLER_HELP));
        CheckBox langPack = new CheckBox(localisationSupport.getString(MainUiTranslation.INSTALLER_LANGPACK));
        List<OS> oses = MainUi.getSettingsInstance().getOSs();
        langPack.setVisible(oses.size() > 0 && !oses.get(0).isWindows());
        //Set checked status
        main.setSelected(settings.get(SiGuiSettings.BooleanSettingKey.CB_MAIN_TICKED));
        sdk.setSelected(settings.get(SiGuiSettings.BooleanSettingKey.CB_SDK_TICKED));
        hp.setSelected(settings.get(SiGuiSettings.BooleanSettingKey.CB_HELP_TICKED));
        langPack.setSelected(settings.get(SiGuiSettings.BooleanSettingKey.CB_LANGPACK_TICKED));
        //Set checked change listener
        main.setOnAction(event -> {
            settings.set(SiGuiSettings.BooleanSettingKey.CB_MAIN_TICKED, main.isSelected());
        });
        sdk.setOnAction(event -> {
            settings.set(SiGuiSettings.BooleanSettingKey.CB_SDK_TICKED, sdk.isSelected());
        });
        hp.setOnAction(event -> {
            settings.set(SiGuiSettings.BooleanSettingKey.CB_HELP_TICKED, hp.isSelected());
        });
        langPack.setOnAction(event -> {
            settings.set(SiGuiSettings.BooleanSettingKey.CB_LANGPACK_TICKED, langPack.isSelected());
        });

        secondRow.getChildren().addAll(main, hp);
        thirdrow.getChildren().addAll(sdk, langPack);
        //Forth row -> Available languages
        Label label = new Label(localisationSupport.getString(MainUiTranslation.AVAILABLE_LANGUAGES));
        languages.getItems().addAll(settings.getAvailableLanguages());
        settings.get(SiGuiSettings.StringSettingKey.DL_LANGUAGE).ifPresent(s -> languages.getSelectionModel().select(s));
        languages.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            settings.set(SiGuiSettings.StringSettingKey.DL_LANGUAGE, newValue);
        });
        fourthRow.getChildren().addAll(label, languages);
        //Fith row
        Button startDL = new Button(localisationSupport.getString(MainUiTranslation.DOWNLOAD_START));
        startDL.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Runnable r = () -> {
                    getAccordeonDlLocation().ifPresent(location -> {
                        Platform.runLater(() -> startDL.setDisable(true));
                        String lang = languages.selectionModelProperty().get().getSelectedItem();
                        String dlPath = settings.get(SiGuiSettings.StringSettingKey.DOWNLOADFOLDER).orElseGet(() -> Files.createTempDir().toString());
                        List<Optional<DownloadManager.Entry>> entries = new LinkedList<>();

                        final HashMap<String, MainUiTranslation> eventType = new HashMap<>();

                        if (settings.get(SiGuiSettings.BooleanSettingKey.CB_MAIN_TICKED)) {
                            Optional<DownloadManager.Entry> e = DownloadHelper.getDownloadFileMain(location);
                            e.ifPresent(en -> {
                                if (settings.get(SiGuiSettings.BooleanSettingKey.RENAME_FILES))
                                    en.setFilename("libo_main" + en.getFilename().substring(en.getFilename().lastIndexOf('.')));
                                eventType.put(en.getFilename(), MainUiTranslation.INSTALLER_MAIN);
                            });
                            entries.add(e);

                        }
                        if (settings.get(SiGuiSettings.BooleanSettingKey.CB_HELP_TICKED)) {
                            Optional<DownloadManager.Entry> e = DownloadHelper.getDownloadFileHelp(location, lang);
                            e.ifPresent(en -> {
                                if (settings.get(SiGuiSettings.BooleanSettingKey.RENAME_FILES))
                                    en.setFilename("libo_main" + en.getFilename().substring(en.getFilename().lastIndexOf('.')));
                                eventType.put(en.getFilename(), MainUiTranslation.INSTALLER_HELP);
                            });
                            entries.add(e);
                        }
                        if (settings.get(SiGuiSettings.BooleanSettingKey.CB_SDK_TICKED)) {
                            Optional<DownloadManager.Entry> e = DownloadHelper.getDownloadFileSdk(location);
                            e.ifPresent(en -> {
                                if (settings.get(SiGuiSettings.BooleanSettingKey.RENAME_FILES))
                                    en.setFilename("libo_main" + en.getFilename().substring(en.getFilename().lastIndexOf('.')));
                                eventType.put(en.getFilename(), MainUiTranslation.INSTALLER_SDK);
                            });
                            entries.add(e);
                        }
                        if (settings.get(SiGuiSettings.BooleanSettingKey.CB_LANGPACK_TICKED)) {
                            Optional<DownloadManager.Entry> e = DownloadHelper.getDownloadFileLangPack(location, lang);
                            e.ifPresent(en -> {
                                if (settings.get(SiGuiSettings.BooleanSettingKey.RENAME_FILES))
                                    en.setFilename("libo_main" + en.getFilename().substring(en.getFilename().lastIndexOf('.')));
                                eventType.put(en.getFilename(), MainUiTranslation.INSTALLER_LANGPACK);
                            });
                            entries.add(e);
                        }
                        entries.stream().filter(Optional::isPresent).map(Optional::get).map(e -> {
                            e.setTo(Paths.get(dlPath, e.getFilename()));
                            return e;
                        }).map(downloadManager::submit).forEach(lf -> {
                            lf.addListener(() -> {
                                try {
                                    Optional<DownloadManager.Entry> text = lf.get();
                                    text.ifPresent(entry -> entry.getTo().ifPresent(path -> {
                                        MainUiTranslation mainUiTranslation = eventType.get(entry.getFilename());
                                        Platform.runLater(() -> {
                                                    MainUiInstallTab.getInstance(localisationSupport, window).updatePath(path.toString(), mainUiTranslation, settings);
                                                    if (!downloadManager.getTotalDownloadProgress().hasStarted()) {

                                                        progressBar.progressProperty().set(0d);
                                                        startDL.setDisable(false);
                                                    }
                                                }
                                        );
                                    }));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }, MainUi.listeningExecutorService);
                        });

                    });
                };
                new Thread(r).start();
            }
        });
        fifthRow.getChildren().add(startDL);
        //Sixth row
        Button cancel = new Button("X");
        cancel.setOnAction(event -> {
            downloadManager.cancel();
            startDL.setDisable(false);
            //Reset progress bar
            progressBar.progressProperty().set(0d);
        });
        progressBar.setMinWidth(200);
        progressBar.setMaxWidth(Double.MAX_VALUE);
        downloadManager.addTotalDownloadProgressListener(this::updateProgressbar);
        sixthRow.getChildren().addAll(progressBar, cancel);

    }


    @RunsOnJavaFXThread
    /**
     * Updates the accordion with the given.
     *
     * @param downloads Either NULL (fetching from settings) or the CollectionHashMap from which it will be updated
     */
    public void updateAccordeon
            (CollectionHashMap<DownloadType, SortedSet<DownloadInfo.DownloadLocation>, DownloadInfo.DownloadLocation> downloads) {
        SiGuiSettings settings = MainUi.getSettingsInstance();

        if (downloads != null)
            settings.setCachedDownloads(downloads);
        downloads = settings.getCachedDownloads();
        accordion.getPanes().clear();
        Set<DownloadType> keySet = downloads.getKeySet();
        if (keySet.size() == 0)
            keySet = new TreeSet<>(Arrays.asList(DownloadType.values()));
        for (DownloadType key : downloads.getKeySet()) {
            SortedSet<DownloadInfo.DownloadLocation> cur = downloads.get(key).orElse(Collections.emptySortedSet());
            ObservableList<DownloadInfo.DownloadLocation> observableList = FXCollections.observableArrayList(cur);
            DownloadAccordion.DownloadPane dlPane = new DownloadAccordion.DownloadPane(key, localisationSupport);
            dlPane.getChoiceBox().setItems(observableList);
            dlPane.getChoiceBox().setMaxWidth(250);
            accordion.getPanes().add(dlPane);
        }

    }

    @RunsOnJavaFXThread
    private void updateProgressbar(DownloadProgressInfo info) {
        progressBar.progressProperty().set(info.getPercent());
    }

    @Override
    public void close() throws Exception {
        SiGuiSettings settings = MainUi.getSettingsInstance();
        settings.setSelectedDownloadLocations(accordion.getSelectedDownloadLocations());
        if (downloadManager != null)
            downloadManager.close();
    }

    private Optional<DownloadInfo.DownloadLocation> getAccordeonDlLocation() throws IllegalStateException {
        TitledPane pane = accordion.getExpandedPane();
        if (pane == null)
            return Optional.empty();
        if (!(pane instanceof DownloadAccordion.DownloadPane))
            throw new IllegalStateException("DownloadPane is dirty! " + pane.getClass());
        DownloadAccordion.DownloadPane dlPane = (DownloadAccordion.DownloadPane) pane;
        return Optional.of(dlPane.getChoiceBox().getValue());
    }


}
