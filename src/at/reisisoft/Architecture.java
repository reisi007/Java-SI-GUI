package at.reisisoft;

/**
 * Created by Florian on 22.06.2015.
 */
public enum Architecture {
    X86, X86_64;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
