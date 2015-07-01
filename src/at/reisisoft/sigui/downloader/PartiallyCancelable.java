package at.reisisoft.sigui.downloader;

/**
 * Created by Florian on 01.07.2015.
 */
public interface PartiallyCancelable<T> extends Cancelable {
    public void cancel(T toCancel);
}
