package at.reisisoft.sigui.ui.main;

import at.reisisoft.sigui.l10n.ExceptionTranslation;
import at.reisisoft.sigui.l10n.LocalisationSupport;
import at.reisisoft.sigui.l10n.TranslationKey;
import at.reisisoft.sigui.settings.SiGuiSettings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;
import javafx.util.StringConverter;

import java.io.File;
import java.util.*;

import static at.reisisoft.sigui.settings.SiGuiSettings.BooleanSettingKey;
import static at.reisisoft.sigui.settings.SiGuiSettings.StringSettingKey;

/**
 * Created by Florian on 11.07.2015.
 */
public class MainUiSettingsTab extends Tab {

    private static MainUiSettingsTab instance = null;
    private Map<SiGuiSettings.StringSettingKey, TextField> stringSettingsMap = new EnumMap<>(StringSettingKey.class);
    private Map<SiGuiSettings.BooleanSettingKey, CheckBox> booleanSettingsMap = new EnumMap<>(BooleanSettingKey.class);
    private final Window window;

    public static MainUiSettingsTab getInstance(LocalisationSupport localisationSupport, Window window) {
        if (instance == null) {
            Objects.requireNonNull(localisationSupport);
            instance = new MainUiSettingsTab(localisationSupport, window);
        }
        return instance;
    }

    private final LocalisationSupport localisationSupport;

    private MainUiSettingsTab(LocalisationSupport localisationSupport, Window window) {
        super(localisationSupport.getString(MainUITab.SETTINGS));
        this.localisationSupport = localisationSupport;
        this.window = window;
        final SiGuiSettings settings = MainUi.getSettingsInstance();
        setOnSelectionChanged(event -> {
            if (isSelected())
                getLatestSettings();
        });
        VBox mainContent = new VBox(6);
        ScrollPane scrollPane = new ScrollPane(mainContent);
        setContent(scrollPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        StringSettingKey[] skeys = new StringSettingKey[]{StringSettingKey.DOWNLOADFOLDER, StringSettingKey.SHORTCUTFOLDER, StringSettingKey.INSTALL_PATH};
        BooleanSettingKey[] bkeys = new BooleanSettingKey[]{BooleanSettingKey.RENAME_FILES, BooleanSettingKey.INSTALL_SUBFOLDER};
        //Create UI
        for (StringSettingKey key : skeys) {
            HBox cur = new HBox(8);
            mainContent.getChildren().add(cur);
            TextField tf = new TextField();
            tf.setEditable(false);
            String title = localisationSupport.getString(MainUiTranslation.OPEN, localisationSupport.getString(getKeyFrom(key)));
            Button b = new Button(title);
            b.setTooltip(new Tooltip(localisationSupport.getString(getToolTipKeyFrom(key))));
            b.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    DirectoryChooser directoryChooser = new DirectoryChooser();
                    directoryChooser.setTitle(title);
                    directoryChooser.setInitialDirectory(new File(settings.get(key).orElseGet(() -> System.getProperty("user.home"))));
                    Optional<File> optional = Optional.ofNullable(directoryChooser.showDialog(window));
                    optional.ifPresent(f -> {
                        tf.setText(f.toString());
                        settings.set(key, f.toString());
                    });
                }
            });
            cur.getChildren().addAll(b, tf);
            stringSettingsMap.put(key, tf);
        }
        for (BooleanSettingKey key : bkeys) {
            CheckBox checkBox = new CheckBox();
            checkBox.setText(localisationSupport.getString(getKeyFrom(key)));
            checkBox.setTooltip(new Tooltip(localisationSupport.getString(getToolTipKeyFrom(key))));
            checkBox.setOnAction(e -> settings.set(key, checkBox.isSelected()));
            mainContent.getChildren().add(checkBox);
            booleanSettingsMap.put(key, checkBox);
        }
        ChoiceBox<Locale> l10n = new ChoiceBox<>(FXCollections.observableArrayList(LocalisationSupport.AVAILABLE_LOCALS));
        l10n.setConverter(new StringConverter<Locale>() {
            private Map<String, Locale> map = new HashMap<>();

            @Override
            public String toString(Locale object) {
                String s = object.getDisplayName();
                map.put(s, object);
                return s;
            }

            @Override
            public Locale fromString(String string) {
                return map.get(string);
            }
        });
        l10n.getSelectionModel().select(Locale.getDefault());
        l10n.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Locale>() {
            @Override
            public void changed(ObservableValue<? extends Locale> observable, Locale oldValue, Locale newValue) {
                settings.setUserLanguage(newValue);
            }
        });
        Label l10Label = new Label(localisationSupport.getString(MainUiSettingsTabTranslation.L10LABLE));
        mainContent.getChildren().add(new HBox(8, l10Label, l10n));
    }

    private TranslationKey getKeyFrom(StringSettingKey key) {
        switch (key) {
            case DOWNLOADFOLDER:
                return MainUiSettingsTabTranslation.FOLDER_DOWNLOAD;
            case SHORTCUTFOLDER:
                return MainUiSettingsTabTranslation.FOLDER_SHORTCUT;
            case INSTALL_PATH:
                return MainUiSettingsTabTranslation.INSTALLPATH;
            default:
                throw new IllegalArgumentException(localisationSupport.getString(ExceptionTranslation.ILLEGALARGUMENT_UNKNOWN, "key"));
        }
    }

    private TranslationKey getKeyFrom(BooleanSettingKey key) {
        switch (key) {
            case RENAME_FILES:
                return MainUiSettingsTabTranslation.RENAMEFILES;
            case INSTALL_SUBFOLDER:
                return MainUiSettingsTabTranslation.INSTALLSUBFOLDER;
            default:
                throw new IllegalArgumentException(localisationSupport.getString(ExceptionTranslation.ILLEGALARGUMENT_UNKNOWN, "key"));
        }
    }

    private TranslationKey getToolTipKeyFrom(StringSettingKey key) {
        switch (key) {
            case DOWNLOADFOLDER:
                return MainUiSettingsTabTranslation.FOLDER_DOWNLOAD_TOOLTIP;
            case SHORTCUTFOLDER:
                return MainUiSettingsTabTranslation.FOLDER_SHORTCUT_TOOLTIP;
            case INSTALL_PATH:
                return MainUiSettingsTabTranslation.INSTALLPATH_TOOLTIP;
            default:
                throw new IllegalArgumentException(localisationSupport.getString(ExceptionTranslation.ILLEGALARGUMENT_UNKNOWN, "key"));
        }
    }

    private TranslationKey getToolTipKeyFrom(BooleanSettingKey key) {
        switch (key) {
            case RENAME_FILES:
                return MainUiSettingsTabTranslation.RENAMEFILES_TOOLTIP;
            case INSTALL_SUBFOLDER:
                return MainUiSettingsTabTranslation.INSTALLSUBFOLDER_TOOLTIP;
            default:
                throw new IllegalArgumentException(localisationSupport.getString(ExceptionTranslation.ILLEGALARGUMENT_UNKNOWN, "key"));
        }
    }

    private void getLatestSettings() {
        final SiGuiSettings settings = MainUi.getSettingsInstance();
        for (StringSettingKey key : stringSettingsMap.keySet()) {
            Optional.ofNullable(stringSettingsMap.get(key)).ifPresent(tf -> tf.setText(settings.get(key).orElse("")));
        }
        for (BooleanSettingKey key : booleanSettingsMap.keySet()) {
            Optional.ofNullable(booleanSettingsMap.get(key)).ifPresent(cb -> cb.setSelected(settings.get(key)));
        }
    }
}
