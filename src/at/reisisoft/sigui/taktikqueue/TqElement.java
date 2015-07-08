package at.reisisoft.sigui.taktikqueue;

import java.util.Optional;

/**
 * Created by Florian on 08.07.2015.
 */
public interface TqElement<T, U extends TqElement<T, U, R>, R> {

    default Optional<R> getElementValue(T from) {
        return Optional.empty();
    }

    default Optional<R> getValue(T from) {
        Optional<R> result = getElementValue(from);
        if (result.isPresent())
            return result;
        Optional<U> next = getNext();
        if (next.isPresent())
            return next.get().getValue(from);
        return Optional.empty();
    }

    /**
     * @param next The next element
     * @return Returns the value of {@param next}
     */
    U setNext(U next);

    Optional<U> getNext();
}
