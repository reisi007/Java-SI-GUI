package at.reisisoft.sigui;

/**
 * Created by Florian on 15.07.2015.
 */
public class ObjectProperty<T> {

    private T val;

    public T get() {
        return val;
    }

    public void set(T value) {
        val = value;
    }

}
