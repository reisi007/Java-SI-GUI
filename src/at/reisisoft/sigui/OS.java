package at.reisisoft.sigui;

import org.apache.commons.lang.SystemUtils;

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
        if (SystemUtils.IS_OS_MAC_OSX) {
            return new OS[]{Mac};
        }
        if (SystemUtils.IS_OS_LINUX)
            return new OS[]{LinuxDeb, LinuxRPM};
        if (SystemUtils.IS_OS_WINDOWS)
            return new OS[]{Win, Win_EXE};
        return new OS[0];
    }

    ;
}
