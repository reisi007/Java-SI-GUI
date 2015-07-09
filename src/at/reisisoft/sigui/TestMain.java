package at.reisisoft.sigui;

import at.reisisoft.sigui.collection.CollectionHashMap;
import at.reisisoft.sigui.manager.ManagerUtil;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

/**
 * Created by Florian on 08.07.2015.
 */
public class TestMain {

    public static final void main(String[] args) throws Exception {
      /*  Path target = Paths.get("L:\\4.2.1.1\\program\\soffice.exe"), targetFolder = Paths.get("D:\\Desktop\\test");
        Optional<ShortcutProvider> optional = ShortcutProviders.SHORTCUT_FACTORY.getValue(OS.Win);
        System.out.println("Shortcut possible: " + optional.isPresent());
        if (!optional.isPresent())
            return;
        ShortcutProvider sp = optional.get();
        Files.createDirectories(targetFolder);
        sp.createShortcut(targetFolder, "LibO 4.2.1.1 Parallel", target, "Parallel installation of LibreOffice!");
        System.out.println("Shortcut created!");*/
        Path target = Paths.get("L:\\");
        String base = "%s -> %s%n";
        Collection<CollectionHashMap.KeyValuePair<String, Path>> collection = ManagerUtil.scanForsoffice(target);
        System.out.println("Found: " + collection.stream().map(a -> String.format(base, a.getKey(), a.getValue())).peek(System.out::print).count());
    }
}
