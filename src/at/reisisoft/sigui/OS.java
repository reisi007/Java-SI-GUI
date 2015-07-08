package at.reisisoft.sigui;

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
            case Win_EXE:
                return Win.toString();
            default:
                return super.toString();
        }
    }

    public boolean isLinux() {
        switch (this) {
            case LinuxDeb:
            case LinuxRPM:
                return true;
            default:
                return false;
        }
    }

    public boolean isWindows() {
        switch (this) {
            case Win_EXE:
            case Win:
                return true;
            default:
                return false;
        }
    }

    public String getOSLongName() {
        return super.toString();
    }

    /**
     * @return The name used in the daily builds
     */
    public String getOSShortName() {
        switch (this) {
            case LinuxDeb:
            case LinuxRPM:
                return "Linux";
            case Win_EXE:
                return Win.toString();
            default:
                return toString();
        }
    }

    public static OS[] detect() {
        if (isaMacVM()) {
            return new OS[]{Mac};
        }
        if (isWindowsVM())
            return new OS[]{Win, Win_EXE};
        if (isaLinuxVM())
            return new OS[]{LinuxDeb, LinuxRPM};
        return new OS[0];
    }

    public static boolean isWindowsVM() {
        return getOSName().matches("(W|w)indows*");
    }

    public static boolean isaLinuxVM() {
        return !isWindowsVM() && !isaMacVM();
    }

    public static boolean isaMacVM() {
        return getOSName().matches("(M|m)ac*");
    }

    private static String getOSName() {
        return System.getProperty("os.arch");
    }
}
