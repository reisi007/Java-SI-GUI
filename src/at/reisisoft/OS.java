package at.reisisoft;

/**
 * Created by Florian on 22.06.2015.
 */
public enum OS {
    Win_EXE, Win, Mac, LinuxDeb, LinuxRPM;

    public String getFileExtension() {
        switch (this) {
            case Win:
                return "msi";
            case Win_EXE:
                return "exe";
            case LinuxDeb:
            case LinuxRPM:
                return "tar.gz";
            case Mac:
                return "dmg";
            default:
                throw new UnsupportedOperationException(this + " has no default extension");
        }
    }

    @Override
    public String toString() {
        switch (this) {
            case LinuxRPM:
                return "RPM";
            case LinuxDeb:
                return "DEB";
            default:
                return super.toString();
        }
    }
}
