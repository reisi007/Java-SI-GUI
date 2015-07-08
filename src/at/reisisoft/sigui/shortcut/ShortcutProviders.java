package at.reisisoft.sigui.shortcut;

/**
 * Created by Florian on 08.07.2015.
 */
public class ShortcutProviders {
    public static final AbstractShortcutTqElement SHORTCUT_FACTORY = new WinShortcutTqElement();

    {
        //Initialize rest of SHORTCUT_FACTORY
        SHORTCUT_FACTORY.setNext(new LinuxShortcutTqElement());
    }
}
