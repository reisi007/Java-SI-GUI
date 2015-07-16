package at.reisisoft.sigui;

import java.util.Optional;

/**
 * Created by Florian on 15.07.2015.
 */
public class ObjectProperty<T> {

    private T val = null;

    public Optional<T> get() {
        return Optional.ofNullable(val);
    }

    public void set(T value) {
        val = value;
    }

}
