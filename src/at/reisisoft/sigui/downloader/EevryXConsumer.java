package at.reisisoft.sigui.downloader;

import java.util.function.Consumer;

/**
 * Created by Florian on 30.06.2015.
 */
public class EevryXConsumer<T> implements Consumer<T> {

    public EevryXConsumer(int x, Consumer<T> consumer) {
        this.x = x;
        this.consumer = consumer;
    }

    private int cur = 0, x;
    private final Consumer<T> consumer;


    public int getX() {
        return x;
    }

    public boolean setX(int x) {
        if (x < 1)
            return false;
        this.x = x;
        return true;
    }

    @Override
    public void accept(T t) {
        if ((cur = ((cur + 1) % x)) == 0)
            consumer.accept(t);
    }
}
