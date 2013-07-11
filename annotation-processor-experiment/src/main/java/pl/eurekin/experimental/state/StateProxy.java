package pl.eurekin.experimental.state;

import pl.eurekin.experimental.ChangedPropertyListener;
import pl.eurekin.experimental.StatelessPropertyChangeSupport;

public class StateProxy implements ObservableState {

    private final ObservableState base;
    private final GlobalEventCounter counter;
    StatelessPropertyChangeSupport<Boolean> changeSupport;

    public StateProxy(ObservableState base, GlobalEventCounter counter) {
        this.base = base;
        this.counter = counter;
        changeSupport = new StatelessPropertyChangeSupport<>();
        base.registerChangeListener(new ChangedPropertyListener<Boolean>() {
            @Override
            public void propertyChanged(Boolean oldValue, Boolean newValue) {
                update(oldValue, newValue);
            }
        });
    }

    protected void update(Boolean oldValue, Boolean newValue) {
        try {
            counter.increment();
            changeSupport.firePropertyChangeEvent(oldValue, newValue);
        } finally {
            counter.decrement();
        }

    }


    public boolean value() {
        return base.value();
    }

    public void registerChangeListener(ChangedPropertyListener<Boolean> listener) {
        changeSupport.registerNewListener(listener);
    }

    public void unregisterChangeListener(ChangedPropertyListener<Boolean> listener) {
        changeSupport.unregisterListener(listener);
    }
}
