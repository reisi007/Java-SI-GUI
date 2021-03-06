package at.reisisoft.sigui.ui.main;

import at.reisisoft.sigui.l10n.LocalisationSupport;
import at.reisisoft.sigui.settings.SiGuiSettings;
import at.reisisoft.sigui.ui.RunsOnJavaFXThread;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.Executors;

/**
 * Created by Florian on 10.07.2015.
 */
public class MainUi extends Application implements AutoCloseable {

    private LocalisationSupport localisationSupport;
    private EnumMap<MainUITab, Tab> mainUITabs = new EnumMap<>(MainUITab.class);
    private static final Path settingsPath = new File(".").toPath().resolve("si-gui-java.settings.xml");
    private static SiGuiSettings _instance = null;
    private Window w;
    /*package*/ static ListeningExecutorService listeningExecutorService = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());

    public static SiGuiSettings getSettingsInstance() {
        if (_instance == null)
            _instance = SiGuiSettings.load(settingsPath);
        return _instance;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            SiGuiSettings settings = getSettingsInstance();
            Locale.setDefault(settings.getUserLanguage());
            //Get localisation support
            localisationSupport = LocalisationSupport.getInstance();
            SiGuiSettings.setLocalisationSupport(localisationSupport);
            w = primaryStage.getOwner();
            //Configure tabbed layout
            TabPane tabPane = new TabPane();
            tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
            Scene mainScene = new Scene(tabPane, 550, 350);
            primaryStage.setScene(mainScene);
            primaryStage.setResizable(false);
            MainUITab[] tabs = MainUITab.values();
            for (MainUITab uiTab : tabs) {
                Tab cur = MainUiTabProvider.fillTab(uiTab, localisationSupport, w);
                mainUITabs.put(uiTab, cur);
                tabPane.getTabs().add(cur);
            }
            //3 cherckboxes
            //Set UI Strings
            primaryStage.setTitle(localisationSupport.getString(MainUiTranslation.APP_NAME));
            //Show stage
            primaryStage.show();
        } catch (Exception e) {
            handleException(e);
        }
    }

    @Override
    public void stop() throws Exception {
        try {
            List<? extends AutoCloseable> list = Arrays.asList(MainUiDownloadTab.getInstance(localisationSupport, w), MainUiManagerTab.getInstance(localisationSupport, w), this);
            list.forEach(e -> {
                try {
                    e.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            });
            getSettingsInstance().save(settingsPath, localisationSupport);

        } catch (Exception e) {
            handleException(e);
        } finally {
            super.stop();
        }
    }

    @RunsOnJavaFXThread
    public static void handleException(final Throwable e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(e.getClass().getCanonicalName());
        alert.setHeaderText(e.getMessage());
        StringBuilder stringBuilder = new StringBuilder();
        Throwable cur = e;
        while (cur != null) {
            StackTraceElement[] stackTraceElements = cur.getStackTrace();
            cur.printStackTrace();
            for (StackTraceElement se : stackTraceElements)
                stringBuilder.append(se).append('\n');
            stringBuilder.append("\n\n");
            cur = e.getCause();
        }
        alert.setContentText(stringBuilder.toString());
        alert.showAndWait();
    }

    @Override
    public void close() {
        listeningExecutorService.shutdown();
    }
}
