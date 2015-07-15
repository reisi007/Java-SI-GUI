package at.reisisoft.sigui.ui.main;

import at.reisisoft.sigui.l10n.LocalisationSupport;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Tab;
import javafx.scene.web.WebView;
import javafx.stage.Window;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.html.HTMLAnchorElement;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Created by Florian on 15.07.2015.
 */
public class MainUiCreditsTab extends Tab {

    public static MainUiCreditsTab getInstance(LocalisationSupport localisationSupport, Window window) {
        if (instance == null) {
            Objects.requireNonNull(localisationSupport);
            instance = new MainUiCreditsTab(localisationSupport);
        }
        return instance;
    }

    private final LocalisationSupport localisationSupport;
    private static MainUiCreditsTab instance;

    private static String creditsHTML;

    {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(MainUiSettingsTab.class.getResourceAsStream("CREDITS.html"), StandardCharsets.UTF_8))) {
            StringBuffer stringBuffer = new StringBuffer();
            String s;
            while ((s = in.readLine()) != null)
                stringBuffer.append(s);
            creditsHTML = stringBuffer.toString();
        } catch (IOException e) {
            creditsHTML = "<html><head/><body><h1>Error loading CREDITS.html</h1></body></html>";
        }
    }

    private MainUiCreditsTab(LocalisationSupport localisationSupport) {
        super(localisationSupport.getString(MainUITab.CREDITS));
        this.localisationSupport = localisationSupport;
        WebView webView = new WebView();
        setContent(webView);
        webView.getEngine().loadContent(creditsHTML);
        EventListener eventListener = new EventListener() {
            @Override
            public void handleEvent(Event event) {
                EventTarget target = event.getCurrentTarget();
                if (target != null && target instanceof HTMLAnchorElement) {
                    HTMLAnchorElement a = (HTMLAnchorElement) target;
                    try {
                        Desktop d = Desktop.getDesktop();
                        if (d.isSupported(Desktop.Action.BROWSE)) {
                            d.browse(new URI(a.getHref()));
                            event.preventDefault();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        webView.getEngine().getLoadWorker().workDoneProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                NodeList nodeList = webView.getEngine().getDocument().getElementsByTagName("a");
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node cur = nodeList.item(i);
                    EventTarget target = (EventTarget) cur;
                    target.addEventListener("click", eventListener, false);
                }
            }
        });


    }
}
