package at.reisisoft.sigui.taktikqueue;

import java.util.Optional;

/**
 * Created by Florian on 08.07.2015.
 */
public abstract class AbstractTqElement<T, U extends AbstractTqElement<T, U, R>, R> implements TqElement<T, U, R> {

    protected AbstractTqElement() {
        this(null);
    }

    @Override
    public abstract Optional<R> getElementValue(T from);

    protected AbstractTqElement(U next) {
        this.next = next;
    }

    private U next;

    @Override
    public U setNext(U next) {
        this.next = next;
        return next;
    }

    @Override
    public Optional<U> getNext() {
        return Optional.ofNullable(next);
    }
}
