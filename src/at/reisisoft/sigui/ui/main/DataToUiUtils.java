package at.reisisoft.sigui.ui.main;

import at.reisisoft.sigui.DownloadInfo;
import at.reisisoft.sigui.DownloadType;
import at.reisisoft.sigui.collection.CollectionHashMap;
import at.reisisoft.sigui.l10n.LocalisationSupport;
import at.reisisoft.sigui.ui.controls.DownloadAccordion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Accordion;

import java.util.*;

/**
 * Created by Florian on 11.07.2015.
 */
public class DataToUiUtils {

    public static Accordion getDownloadPane(CollectionHashMap<DownloadType, SortedSet<DownloadInfo.DownloadLocation>, DownloadInfo.DownloadLocation> collectionHashMap, LocalisationSupport localisationSupport, DownloadAccordion old) {
        Accordion accordion = Optional.ofNullable(old).orElse(new DownloadAccordion());
        accordion.getPanes().clear();
        Set<DownloadType> keySet = collectionHashMap.getKeySet();
        if (keySet.size() == 0)
            keySet = new TreeSet<>(Arrays.asList(DownloadType.values()));
        for (DownloadType key : collectionHashMap.getKeySet()) {
            SortedSet<DownloadInfo.DownloadLocation> cur = collectionHashMap.get(key).orElse(Collections.emptySortedSet());
            ObservableList<DownloadInfo.DownloadLocation> observableList = FXCollections.observableArrayList(cur);
            DownloadAccordion.DownloadPane dlPane = new DownloadAccordion.DownloadPane(key, localisationSupport);
            dlPane.getChoiceBox().setItems(observableList);
            dlPane.getChoiceBox().setMaxWidth(250);
            accordion.getPanes().add(dlPane);
        }
        return accordion;

    }
}
