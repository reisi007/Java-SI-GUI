package at.reisisoft.sigui.installation;

/**
 * Created by Florian on 09.07.2015.
 */
public class InstallationProviders {
    public static final AbstractInstallationTqElement INSTALLATION_FACTORY = new WinMsiInstallationTqElement();

    {
        //Initialize rest of INSTALLATION_FACTORY
        INSTALLATION_FACTORY.setNext(new WinExeInstallationTqElement()).setNext(new LinuxDebInstallationTqElement()).setNext(new LinuxRpmInstallationTqElement()).setNext(new MacInstallationTqElement());
    }
}
