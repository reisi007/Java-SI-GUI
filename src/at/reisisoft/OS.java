package at.reisisoft;

/**
 * Created by Florian on 22.06.2015.
 */
public enum OS {
    Win, Mac, Linux;

    public String getFileExtension() {
        switch (this) {
            case Win:
                return "msi";
            case Linux:
                return "tar.gz";
            case Mac:
                return "dmg";
            default:
                throw new UnsupportedOperationException(this + " has no default extension");
        }
    }
}
