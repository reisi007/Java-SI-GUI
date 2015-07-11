package at.reisisoft.sigui.ui.main;

import at.reisisoft.sigui.DownloadType;
import at.reisisoft.sigui.collection.CollectionHashMap;
import at.reisisoft.sigui.l10n.LocalisationSupport;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Accordion;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TitledPane;

import java.util.*;

/**
 * Created by Florian on 11.07.2015.
 */
public class DataToUiUtils {

    public static <V, C extends Collection<V>> Accordion getAccordeonFromCollectionHashMap(CollectionHashMap<DownloadType, C, V> collectionHashMap, LocalisationSupport localisationSupport, Accordion old) {
        Accordion accordion = Optional.ofNullable(old).orElse(new Accordion());
        accordion.getPanes().clear();
        Set<DownloadType> keySet = collectionHashMap.getKeySet();
        if (keySet.size() == 0)
            keySet = new TreeSet<>(Arrays.asList(DownloadType.values()));
        for (DownloadType key : collectionHashMap.getKeySet()) {
            C cur = collectionHashMap.get(key).orElse((C) Collections.emptyList());
            ObservableList<V> observableList = FXCollections.observableArrayList(cur);
            ChoiceBox<V> choiceBox = new ChoiceBox<>(observableList);
            choiceBox.setMaxWidth(250);
            TitledPane pane = new TitledPane(localisationSupport.getString(key), choiceBox);
            accordion.getPanes().add(pane);
        }
        return accordion;

    }

    public static <V, C extends Collection<V>> Accordion getAccordeonFromCollectionHashMap(CollectionHashMap<DownloadType, C, V> collectionHashMap, LocalisationSupport localisationSupport) {
        return getAccordeonFromCollectionHashMap(collectionHashMap, localisationSupport, null);
    }
}
