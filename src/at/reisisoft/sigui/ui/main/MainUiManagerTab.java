package at.reisisoft.sigui.ui.main;

import at.reisisoft.sigui.OS;
import at.reisisoft.sigui.collection.CollectionHashMap;
import at.reisisoft.sigui.l10n.LocalisationSupport;
import at.reisisoft.sigui.manager.ManagerModel;
import at.reisisoft.sigui.manager.ManagerUtil;
import at.reisisoft.sigui.settings.SiGuiSettings;
import at.reisisoft.sigui.ui.AdditionalFunctions;
import com.google.common.util.concurrent.MoreExecutors;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by Florian on 11.07.2015.
 */
public class MainUiManagerTab extends Tab implements AutoCloseable {

    private static MainUiManagerTab instance = null;
    private TableView<CollectionHashMap.KeyValuePair<String, ObservableList<Path>>> tableView;
    private final ManagerModel model;
    private final Window window;

    public static MainUiManagerTab getInstance(LocalisationSupport localisationSupport, Window window) {
        if (instance == null) {
            Objects.requireNonNull(localisationSupport);
            instance = new MainUiManagerTab(localisationSupport, window);
        }
        return instance;
    }

    private final LocalisationSupport localisationSupport;

    public ManagerModel getModel() {
        return model;
    }

    private MainUiManagerTab(LocalisationSupport localisationSupport, Window window) {
        super(localisationSupport.getString(MainUITab.MANAGER));
        this.localisationSupport = localisationSupport;
        this.window = window;
        SiGuiSettings settings = MainUi.getSettingsInstance();
        VBox mainContent = new VBox(6);
        setContent(mainContent);

        Button searchFor = new Button(localisationSupport.getString(MainUiTranslation.FINDINSTALLATIONS));
        ProgressIndicator searchProgressIndicator = new ProgressIndicator(), deleteProgressIndicator = new ProgressIndicator();
        searchProgressIndicator.setVisible(false);
        deleteProgressIndicator.setVisible(false);
        model = new ManagerModel(settings.getManagerEntries());
        tableView = new TableView<>(model.getObservableList());
        mainContent.getChildren().addAll(tableView, new HBox(8, searchFor, searchProgressIndicator));

        //Create Table
        TableColumn<CollectionHashMap.KeyValuePair<String, ObservableList<Path>>, String> key = new TableColumn<>(localisationSupport.getString(MainUiTranslation.MANAGER_TABLE_KEY));
        key.setCellValueFactory(new PropertyValueFactory<>("key"));
        TableColumn<CollectionHashMap.KeyValuePair<String, ObservableList<Path>>, String> value = new TableColumn<>(localisationSupport.getString(MainUiTranslation.MANAGER_TABLE_VALUE));
        value.setCellValueFactory(new PropertyValueFactory<>("value"));
        key.prefWidthProperty().bind(tableView.widthProperty().divide(5));
        value.prefWidthProperty().bind(tableView.widthProperty().multiply(4d / 5).subtract(20));
        tableView.getColumns().addAll(key, value);
        //Context menu for create shortcut
        MenuItem createShortCut = new MenuItem(localisationSupport.getString(MainUiTranslation.SHORTCUT_CREATE));
        createShortCut.setOnAction(event -> {
            CollectionHashMap.KeyValuePair<String, ObservableList<Path>> selectedItem = tableView.getSelectionModel().getSelectedItem();
            Path val = getSofficePath(selectedItem.getValue(), settings.getOSs().get(0));
            AdditionalFunctions.createShortCut(settings).accept(new CollectionHashMap.KeyValuePair<>(selectedItem.getKey(), getSofficePath(selectedItem.getValue(), settings.getOSs().get(0))));
        });
        MenuItem deleteCur = new MenuItem(localisationSupport.getString(MainUiTranslation.MANAGER_DELETE));
        deleteCur.setOnAction(event1 -> {
            deleteProgressIndicator.setVisible(true);
            MainUi.listeningExecutorService.submit(() -> model.remove(tableView.getSelectionModel().getSelectedItem().getKey())).addListener(() ->
                    Platform.runLater(() -> deleteProgressIndicator.setVisible(false))
                    , MoreExecutors.sameThreadExecutor());
        });
        tableView.setContextMenu(new ContextMenu(createShortCut, deleteCur));


        //Button
        searchFor.setOnAction(event -> {
            Optional<File> optional = chooseFolder();
            optional.ifPresent(f -> {
                searchProgressIndicator.setVisible(true);
                searchFor.setDisable(true);
                Runnable r = () -> {
                    try {
                        Collection<CollectionHashMap.KeyValuePair<String, Path>> collection = ManagerUtil.scanForsoffice(f.toPath());
                        Platform.runLater(() -> {
                            model.put(collection);
                            searchProgressIndicator.setVisible(false);
                            searchFor.setDisable(false);
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                };
                new Thread(r).start();
            });

        });


    }

    private Path getSofficePath(List<Path> list, OS os) {
        String soffice = "soffice." + os.getExecutingExtension();
        for (Path p : list) {
            Path sofficePath = p.resolve("program").resolve(soffice);
            if (Files.exists(sofficePath))
                return sofficePath;
        }
        return list.get(0);
    }

    @Override
    public void close() {
        SiGuiSettings settings = MainUi.getSettingsInstance();
        settings.setManagerEntries(model.toMap());
    }

    private Optional<File> chooseFolder() {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle(localisationSupport.getString(MainUiTranslation.FINDINSTALLATIONS));
        return Optional.ofNullable(dirChooser.showDialog(window));
    }
}
