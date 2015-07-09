package at.reisisoft.sigui.installation;

import at.reisisoft.sigui.OS;
import at.reisisoft.sigui.taktikqueue.AbstractTqElement;

import java.util.Optional;

/**
 * Created by Florian on 09.07.2015.
 */
public abstract class AbstractInstallationTqElement extends AbstractTqElement<OS, AbstractInstallationTqElement, InstallationProvider> {
    private AbstractInstallationTqElement prev = null;

    /**
     * Sets the next element of the current one as well as the previous of the next element
     *
     * @param next The next element
     * @return {@param next} for chaining
     */
    @Override
    public AbstractInstallationTqElement setNext(AbstractInstallationTqElement next) {
        if (next.getPrevious().orElse(null) != this)
            next.setPrevious(this);
        return super.setNext(next);
    }

    @Override
    public Optional<AbstractInstallationTqElement> getNext() {
        return super.getNext();
    }

    public AbstractInstallationTqElement setPrevious(AbstractInstallationTqElement prev) {
        return this.prev = prev;
    }

    public Optional<AbstractInstallationTqElement> getPrevious() {
        return Optional.ofNullable(prev);
    }

    public AbstractInstallationTqElement getHeadOfQueue() {
        AbstractInstallationTqElement cur = this;
        Optional<AbstractInstallationTqElement> prev = getPrevious();
        while (prev.isPresent()) {
            cur = prev.get();
            prev = cur.getPrevious();
        }
        return cur;
    }
}
