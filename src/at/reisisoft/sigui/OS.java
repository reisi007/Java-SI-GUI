package at.reisisoft.sigui;

/**
 * Created by Florian on 22.06.2015.
 */
public enum OS {
    WinExe, WinMsi, Mac, LinuxDeb, LinuxRPM;

    public String getFileExtension() {
        switch (this) {
            case WinMsi:
                return "msi";
            case WinExe:
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
            case WinExe:
            case WinMsi:
                return "Win";
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
            case WinExe:
            case WinMsi:
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
            case WinExe:
                return WinMsi.toString();
            default:
                return toString();
        }
    }

    public static OS[] detect() {
        if (isaMacVM()) {
            return new OS[]{Mac};
        }
        if (isWindowsVM())
            return new OS[]{WinMsi, WinExe};
        if (isLinuxVM())
            return new OS[]{LinuxDeb, LinuxRPM};
        return new OS[0];
    }

    public String getLinuxPackagingSystem() {
        switch (this) {
            case LinuxDeb:
                return "deb";
            case LinuxRPM:
                return "rpm";
            default:
                return null;
        }
    }

    public static boolean isWindowsVM() {
        return getOSName().startsWith("wind");
    }

    public static boolean isLinuxVM() {
        return getOSName().startsWith("lin");
    }

    public static boolean isaMacVM() {
        return getOSName().startsWith("mac");
    }

    private static String getOSName() {
        return System.getProperty("os.name").toLowerCase();
    }

    public static OS fromFileName(String fileName) {
        for (OS os : OS.detect()) {
            if (fileName.endsWith(os.getFileExtension()))
                return os;
        }
        throw new IllegalArgumentException("No OS known");
    }
}
