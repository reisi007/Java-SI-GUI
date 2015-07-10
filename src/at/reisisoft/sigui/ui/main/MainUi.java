package at.reisisoft.sigui.ui.main;

import at.reisisoft.sigui.l10n.LocalisationSupport;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

import java.util.EnumMap;
import java.util.Locale;

/**
 * Created by Florian on 10.07.2015.
 */
public class MainUi extends Application {

    private LocalisationSupport localisationSupport;
    private EnumMap<MainUITab, Tab> mainUITabs = new EnumMap<>(MainUITab.class);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        //TODO Load settings
        //Apply default language TODO Change to user selected language
        Locale.setDefault(Locale.ENGLISH);
        //Get localisation support
        localisationSupport = LocalisationSupport.getInstance();
        //Configure tabbed layout
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        Scene mainScene = new Scene(tabPane, 600, 400);
        primaryStage.setScene(mainScene);
        MainUITab[] tabs = MainUITab.values();
        for (MainUITab uiTab : tabs) {
            Tab cur = new Tab(localisationSupport.getString(uiTab));
            mainUITabs.put(uiTab, cur);
            tabPane.getTabs().add(cur);
            MainUiTabProvider.fillTab(uiTab, cur, localisationSupport);
        }
        //Set UI Strings
        primaryStage.setTitle(localisationSupport.getString(MainUiTranslation.APP_NAME));
        //Show stage
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        //TODO cleanup
        super.stop();
    }
}
