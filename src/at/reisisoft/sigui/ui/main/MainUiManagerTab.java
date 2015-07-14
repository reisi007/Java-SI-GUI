package at.reisisoft.sigui.ui.main;

import at.reisisoft.sigui.collection.CollectionHashMap;
import at.reisisoft.sigui.l10n.LocalisationSupport;
import at.reisisoft.sigui.manager.ManagerModel;
import at.reisisoft.sigui.manager.ManagerUtil;
import at.reisisoft.sigui.settings.SiGuiSettings;
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
import java.nio.file.Path;
import java.util.Collection;
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

    private MainUiManagerTab(LocalisationSupport localisationSupport, Window window) {
        super(localisationSupport.getString(MainUITab.MANAGER));
        this.localisationSupport = localisationSupport;
        this.window = window;
        SiGuiSettings settings = MainUi.getSettingsInstance();
        VBox mainContent = new VBox(6);
        setContent(mainContent);

        Button searchFor = new Button(localisationSupport.getString(MainUiTranslation.FINDINSTALLATIONS));
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setVisible(false);

        model = new ManagerModel(settings.getManagerEntries());
        tableView = new TableView<>(model.getObservableList());
        mainContent.getChildren().addAll(tableView, new HBox(8, searchFor, progressIndicator));

        //Create Table TODO l10n
        TableColumn<CollectionHashMap.KeyValuePair<String, ObservableList<Path>>, String> key = new TableColumn<>("Key");
        key.setCellValueFactory(new PropertyValueFactory<>("key"));
        TableColumn<CollectionHashMap.KeyValuePair<String, ObservableList<Path>>, String> value = new TableColumn<>("Value");
        value.setCellValueFactory(new PropertyValueFactory<>("value"));
        key.prefWidthProperty().bind(tableView.widthProperty().divide(5));
        value.prefWidthProperty().bind(tableView.widthProperty().multiply(4d / 5).subtract(20));
        tableView.getColumns().addAll(key, value);

        //Button
        searchFor.setOnAction(event -> {
            Optional<File> optional = chooseFolder();
            optional.ifPresent(f -> {
                progressIndicator.setVisible(true);
                Runnable r = () -> {
                    try {
                        Collection<CollectionHashMap.KeyValuePair<String, Path>> collection = ManagerUtil.scanForsoffice(f.toPath());
                        Platform.runLater(() -> {
                            model.put(collection);
                            progressIndicator.setVisible(false);
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                };
                new Thread(r).start();
            });

        });


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
