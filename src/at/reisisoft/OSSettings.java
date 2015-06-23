package at.reisisoft;

/**
 * Created by Florian on 23.06.2015.
 */
public class OSSettings {

    private Architecture arch;
    private OS os;

    private OSSettings(Architecture arch, OS os) {
        this.arch = arch;
        this.os = os;
    }

    public static OSSettings of(Architecture arch, OS os) {
        if (arch == null || os == null)
            throw new IllegalArgumentException("Arch and os must not be null!");
        return new OSSettings(arch, os);
    }

    public Architecture getArch() {
        return arch;
    }

    public OS getOs() {
        return os;
    }
}
