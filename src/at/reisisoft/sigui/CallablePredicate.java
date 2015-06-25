package at.reisisoft.sigui;

import java.util.concurrent.Callable;
import java.util.function.Predicate;

/**
 * Created by Florian on 23.06.2015.
 */
public class CallablePredicate<T> implements Callable<Boolean> {

    public CallablePredicate(Predicate<T> pred, T toTest) {
        this.pred = pred;
        this.toTest = toTest;
    }

    private Predicate<T> pred;
    private T toTest;

    @Override
    public Boolean call() throws Exception {
        return pred.test(toTest);
    }
}
