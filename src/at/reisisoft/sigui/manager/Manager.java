package at.reisisoft.sigui.manager;

import at.reisisoft.sigui.collection.ListHashMap;

import java.nio.file.Path;
import java.util.List;

/**
 * Created by Florian on 08.07.2015.
 */
public class Manager extends ListHashMap<String, Path> {

    @Override
    public List<Path> remove(Object key) {
        List<Path> list = super.remove(key);
        //TODO delete file
        return list;
    }
}
