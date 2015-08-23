package at.reisisoft.sigui.installation;

/**
 * Created by Florian on 09.07.2015.
 */
public class InstallationProviders {
    public static AbstractInstallationTqElement getInstallationFactory(){
        AbstractInstallationTqElement base = new WinMsiInstallationTqElement();
        base.setNext(new WinExeInstallationTqElement()).setNext(new LinuxDebInstallationTqElement()).setNext(new LinuxRpmInstallationTqElement()).setNext(new MacInstallationTqElement());
        return base;
    }
}
