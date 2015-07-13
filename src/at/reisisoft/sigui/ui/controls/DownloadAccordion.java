package at.reisisoft.sigui.ui.controls;

import at.reisisoft.sigui.DownloadInfo;
import at.reisisoft.sigui.DownloadInfo.DownloadLocation;
import at.reisisoft.sigui.DownloadType;
import at.reisisoft.sigui.Utils;
import at.reisisoft.sigui.collection.CollectionHashMap;
import at.reisisoft.sigui.l10n.LocalisationSupport;
import javafx.scene.control.Accordion;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TitledPane;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Created by Florian on 13.07.2015.
 */
public class DownloadAccordion extends Accordion {

    protected Optional<DownloadPane> getChildren(DownloadType type) {
        return getDownloadPanes().filter(e -> e.downloadType == type).findAny();
    }

    public Map<DownloadType, DownloadLocation> getSelectedDownloadLocations() {
        Map<DownloadType, DownloadLocation> map = new EnumMap<>(DownloadType.class);
        Stream<CollectionHashMap.KeyValuePair<DownloadType, DownloadLocation>> iterator = getDownloadPanes().map(dp -> new CollectionHashMap.KeyValuePair<DownloadType, DownloadLocation>(dp.downloadType, dp.choiceBox.getValue()));
        for (CollectionHashMap.KeyValuePair<DownloadType, DownloadLocation> kvp : Utils.iterableFromStream(iterator))
            map.put(kvp.getKey(), kvp.getValue());
        return map;
    }

    public void setSelectedDownloadLocations(Map<DownloadType, DownloadInfo.DownloadLocation> selectedDownloadLocations) {
        getDownloadPanes().filter(e -> selectedDownloadLocations.containsKey(e.downloadType)).forEach(dp -> {
            DownloadLocation value = selectedDownloadLocations.get(dp.downloadType);
            dp.getChoiceBox().getSelectionModel().select(value);
        });
    }

    private Stream<DownloadPane> getDownloadPanes() {
        return getPanes().stream().filter(e -> e instanceof DownloadPane).map(e -> (DownloadPane) e);
    }

    public static class DownloadPane extends TitledPane {

        private final ChoiceBox<DownloadLocation> choiceBox;
        private final DownloadType downloadType;

        public ChoiceBox<DownloadLocation> getChoiceBox() {
            return choiceBox;
        }

        public DownloadPane(DownloadType downloadType, LocalisationSupport localisationSupport) {
            super(localisationSupport.getString(downloadType), new ChoiceBox<>());
            choiceBox = (ChoiceBox<DownloadLocation>) getContent();
            this.downloadType = downloadType;

        }

    }
}
