package at.reisisoft.sigui;

import java.util.Arrays;

/**
 * Created by Florian on 08.07.2015.
 */
public class TestMain {

    public static final void main(String[] args) throws Exception {
      /*  Path target = Paths.get("L:\\4.2.1.1\\program\\soffice.exe"), targetFolder = Paths.get("D:\\Desktop\\test");
        Optional<ShortcutProvider> optional = ShortcutProviders.SHORTCUT_FACTORY.getValue(OS.WinMsi);
        System.out.println("Shortcut possible: " + optional.isPresent());
        if (!optional.isPresent())
            return;
        ShortcutProvider sp = optional.get();
        Files.createDirectories(targetFolder);
        sp.createShortcut(targetFolder, "LibO 4.2.1.1 Parallel", target, "Parallel installation of LibreOffice!");
        System.out.println("Shortcut created!");*/


       /* Path target = Paths.get("L:\\");
        String base = "%s -> %s%n";
        Collection<CollectionHashMap.KeyValuePair<String, Path>> collection = ManagerUtil.scanForsoffice(target);
        System.out.println("Found: " + collection.stream().map(a -> String.format(base, a.getKey(), a.getValue())).peek(System.out::print).count());*/

        OS[] oss = OS.detect();
        String[] ossNames = new String[oss.length];
        for (int i = 0; i < oss.length; i++)
            ossNames[i] = oss[i].getOSLongName();
        Architecture architecture = Architecture.detect();
        System.out.printf("Your OS is one of %s. Your architecture is %s%n", Arrays.toString(ossNames), architecture);
    }
}
