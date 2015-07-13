package at.reisisoft.sigui.ui.main;

import at.reisisoft.sigui.l10n.LocalisationSupport;
import at.reisisoft.sigui.settings.SiGuiSettings;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by Florian on 10.07.2015.
 */
public class MainUi extends Application {

    private LocalisationSupport localisationSupport;
    private EnumMap<MainUITab, Tab> mainUITabs = new EnumMap<>(MainUITab.class);
    private static final Path settingsPath = new File(".").toPath().resolve("si-gui-java.settings.xml");
    private static SiGuiSettings _instance = null;

    public static SiGuiSettings getSettingsInstance() {
        if (_instance == null)
            _instance = SiGuiSettings.load(settingsPath);
        System.out.println(_instance.toString());//TODO Remove after testing
        return _instance;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        //TODO Load settings
        SiGuiSettings settings = getSettingsInstance();
        Locale.setDefault(settings.getUserLanguage());
        //Get localisation support
        localisationSupport = LocalisationSupport.getInstance();
        SiGuiSettings.setLocalisationSupport(localisationSupport);
        //Configure tabbed layout
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        Scene mainScene = new Scene(tabPane, 500, 300);
        primaryStage.setScene(mainScene);
        MainUITab[] tabs = MainUITab.values();
        for (MainUITab uiTab : tabs) {
            Tab cur = MainUiTabProvider.fillTab(uiTab, localisationSupport);
            mainUITabs.put(uiTab, cur);
            tabPane.getTabs().add(cur);
        }
        //3 cherckboxes
        //Set UI Strings
        primaryStage.setTitle(localisationSupport.getString(MainUiTranslation.APP_NAME));
        //Show stage
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        List<? extends AutoCloseable> list = Arrays.asList(MainUiDownloadTab.getInstance(localisationSupport));
        list.forEach(e -> {
            try {
                e.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
        getSettingsInstance().save(settingsPath, localisationSupport);
        super.stop();
    }
}
