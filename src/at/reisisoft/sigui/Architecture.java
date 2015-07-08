package at.reisisoft.sigui;

/**
 * Created by Florian on 22.06.2015.
 */
public enum Architecture {
    X86, X86_64;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }

    public String getInFilenameArchitecture() {
        switch (this) {
            case X86_64:
                return "X64";
            default:
                return super.toString();
        }
    }

    public String getUserReadableString() {
        switch (this) {
            case X86:
                return "32-bit";
            case X86_64:
                return "64-bit";
            default:
                return toString();
        }
    }

    public static Architecture detect() {
        String arch = System.getProperty("os.arch").trim();
        if ("amd64".equalsIgnoreCase(arch))
            return X86_64;
        return X86;
    }
}
