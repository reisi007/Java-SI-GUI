package at.reisisoft.sigui.ui.main;

import at.reisisoft.sigui.OS;
import at.reisisoft.sigui.collection.CollectionHashMap;
import at.reisisoft.sigui.installation.InstallationProvider;
import at.reisisoft.sigui.installation.InstallationProviders;
import at.reisisoft.sigui.l10n.ExceptionTranslation;
import at.reisisoft.sigui.l10n.LocalisationSupport;
import at.reisisoft.sigui.settings.SiGuiSettings;
import at.reisisoft.sigui.ui.RunsOnJavaFXThread;
import com.google.common.util.concurrent.MoreExecutors;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Created by Florian on 11.07.2015.
 */
public class MainUiInstallTab extends Tab {

    private static MainUiInstallTab instance = null;
    private final Window window;
    private final List<MainUiTranslation> allowed = OS.isWindowsVM() ? Arrays.asList(MainUiTranslation.INSTALLER_MAIN, MainUiTranslation.INSTALLER_HELP, MainUiTranslation.INSTALLER_SDK) : Arrays.asList(MainUiTranslation.INSTALLER_MAIN, MainUiTranslation.INSTALLER_HELP, MainUiTranslation.INSTALLER_LANGPACK, MainUiTranslation.INSTALLER_SDK);
    private final ProgressIndicator progressIndicator = new ProgressIndicator();
    private final Button startInstallation;

    public static MainUiInstallTab getInstance(LocalisationSupport localisationSupport, Window window) {
        if (instance == null) {
            Objects.requireNonNull(localisationSupport);
            instance = new MainUiInstallTab(localisationSupport, window);
        }
        return instance;
    }

    private final LocalisationSupport localisationSupport;

    private MainUiInstallTab(LocalisationSupport localisationSupport, Window window) {
        super(localisationSupport.getString(MainUITab.INSTALL));
        this.localisationSupport = localisationSupport;
        this.window = window;

        final SiGuiSettings settings = MainUi.getSettingsInstance();
        VBox mainContent = new VBox(6);
        mainContent.setAlignment(Pos.CENTER);
        setContent(mainContent);
        allowed.stream().map(e -> getHBoxfor(e, settings)).forEach(mainContent.getChildren()::add);
        //Add start Install button
        startInstallation = new Button(localisationSupport.getString(MainUiTranslation.INSTALL_START));
        mainContent.getChildren().addAll(startInstallation, progressIndicator);
        progressIndicator.setProgress(0);
        progressIndicator.setMinSize(75, 75);
        int max = allowed.size();
        //Start installation onclick listener TODO Add functionality
        startInstallation.setOnAction(event -> {
            startInstallation.setDisable(true);
            Runnable r = () -> {
                Stream<String> stringStream = Stream.of(settings.get(SiGuiSettings.StringSettingKey.PATH_MAIN), settings.get(SiGuiSettings.StringSettingKey.PATH_HP), settings.get(SiGuiSettings.StringSettingKey.PATH_LANGPACK), settings.get(SiGuiSettings.StringSettingKey.PATH_SDK)).filter(Optional::isPresent).map(Optional::get).map(String::trim).filter(s -> s.length() > 0);
                stringStream.map(s -> new CollectionHashMap.KeyValuePair<>(s, InstallationProviders.INSTALLATION_FACTORY)).forEach(kvp -> {
                    Optional<InstallationProvider> optional = kvp.getValue().getValue(OS.fromFileName(kvp.getKey()));
                    optional.ifPresent(installationProvider -> {
                        MainUi.listeningExecutorService.submit(() -> {
                            try {
                                installationProvider.install(new File(kvp.getKey()).toPath(),/*TODO ADD INSTALLOCATION HERE*/null);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }).addListener(() -> Platform.runLater(() -> installationFinishedFeedback(1d / max)), MoreExecutors.sameThreadExecutor());
                    });
                });
                Platform.runLater(() -> startInstallation.setDisable(false));
            };
            new Thread(r).start();
        });
    }


    @RunsOnJavaFXThread
    private void installationFinishedFeedback(double amount) {
        progressIndicator.progressProperty().add(amount);
        if (Math.abs(amount - 1) <= 0.009)
            progressIndicator.progressProperty().set(1);
    }

    private HBox getHBoxfor(MainUiTranslation kind, SiGuiSettings settings) {
        if (!isMagicMainUiTranslation(kind))
            throw new IllegalArgumentException(localisationSupport.getString(ExceptionTranslation.ILLEGALARGUMENT_UNKNOWN, MainUiTranslation.class + " 'kind'"));
        HBox hBox = new HBox(8);
        final TextField textField = new TextField();
        textField.setEditable(false);
        textField.setText(settings.get(getStringSettingsKeyFrom(kind)).orElse(""));
        String buttonText = localisationSupport.getString(MainUiTranslation.OPEN, localisationSupport.getString(kind));
        Button b = new Button(buttonText);
        FileChooser fileChooser = getFileChoserFrom(kind, buttonText, settings);
        b.setOnAction(event -> {
            File file = fileChooser.showOpenDialog(window);
            textField.setText(file == null ? "" : file.toString());
            settings.set(getStringSettingsKeyFrom(kind), textField.getText());
            if (file != null)
                settings.set(SiGuiSettings.StringSettingKey.INSTALL_PATH_LAST_FILEOPENED, file.getParent());
        });
        Button cancel = new Button("X");
        cancel.setOnAction(event -> {
            textField.setText("");
            settings.set(getStringSettingsKeyFrom(kind), "");
        });
        hBox.getChildren().addAll(b, textField, cancel);
        return hBox;
    }

    private SiGuiSettings.StringSettingKey getStringSettingsKeyFrom(MainUiTranslation kind) {
        if (!isMagicMainUiTranslation(kind))
            throw new IllegalArgumentException(localisationSupport.getString(ExceptionTranslation.ILLEGALARGUMENT_UNKNOWN, MainUiTranslation.class + " 'kind'"));
        switch (kind) {
            case INSTALLER_MAIN:
                return SiGuiSettings.StringSettingKey.PATH_MAIN;
            case INSTALLER_HELP:
                return SiGuiSettings.StringSettingKey.PATH_HP;
            case INSTALLER_LANGPACK:
                return SiGuiSettings.StringSettingKey.PATH_LANGPACK;
            case INSTALLER_SDK:
                return SiGuiSettings.StringSettingKey.PATH_SDK;
            default:
                throw new IllegalArgumentException(localisationSupport.getString(ExceptionTranslation.ILLEGALARGUMENT_UNKNOWN, MainUiTranslation.class + " 'kind'"));
        }
    }

    private FileChooser.ExtensionFilter[] getExtensionFilterFrom(MainUiTranslation kind) {
        if (!isMagicMainUiTranslation(kind))
            throw new IllegalArgumentException(localisationSupport.getString(ExceptionTranslation.ILLEGALARGUMENT_UNKNOWN, MainUiTranslation.class + " 'kind'"));
        switch (kind) {
            case INSTALLER_MAIN:
                return new FileChooser.ExtensionFilter[]{new FileChooser.ExtensionFilter(localisationSupport.getString(MainUiTranslation.INSTALLER_MAIN), "*Lib*4.*", "*Lib*6.*", "*Lib*all.*", "*Lib*multi.*")};
            case INSTALLER_HELP:
                return new FileChooser.ExtensionFilter[]{new FileChooser.ExtensionFilter(localisationSupport.getString(MainUiTranslation.INSTALLER_HELP), "*helppack*.*")};
            case INSTALLER_LANGPACK:
                return new FileChooser.ExtensionFilter[]{new FileChooser.ExtensionFilter(localisationSupport.getString(MainUiTranslation.INSTALLER_LANGPACK), "*langpack*.*")};
            case INSTALLER_SDK:
                return new FileChooser.ExtensionFilter[]{new FileChooser.ExtensionFilter(localisationSupport.getString(MainUiTranslation.INSTALLER_SDK), "*sdk*.*")};
            default:
                throw new IllegalArgumentException(localisationSupport.getString(ExceptionTranslation.ILLEGALARGUMENT_UNKNOWN, MainUiTranslation.class + " 'kind'"));
        }
    }

    private FileChooser getFileChoserFrom(MainUiTranslation kind, String title, SiGuiSettings settings) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().addAll(getExtensionFilterFrom(kind));
        fileChooser.setInitialDirectory(new File(settings.get(SiGuiSettings.StringSettingKey.INSTALL_PATH_LAST_FILEOPENED).orElseGet(() -> System.getProperty("user.home"))));
        return fileChooser;
    }

    private boolean isMagicMainUiTranslation(MainUiTranslation kind) {
        return allowed.indexOf(kind) >= 0;
    }
}
