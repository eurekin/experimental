package pl.eurekin.experimental.state;

import pl.eurekin.experimental.ChangedPropertyListener;
import pl.eurekin.experimental.Observable;
import pl.eurekin.experimental.StatefulPropertyChangeSupport;

public class StatefulObservable<T> implements Observable<T> {
    private final StatefulPropertyChangeSupport<T> changeSupport;

    public StatefulObservable(T initialValue) {
        this.changeSupport = new StatefulPropertyChangeSupport<T>(initialValue);
    }

    public void registerChangeListener(ChangedPropertyListener<T> listener) {
        changeSupport.registerNewListener(listener);
    }

    public void unregisterChangeListener(ChangedPropertyListener<T> listener) {
        changeSupport.unregisterListener(listener);
    }

    public void notifyOfStateChange(T newValue) {
        changeSupport.firePropertyChangeEvent(newValue);
    }

    @Override
    public T get() {
        return changeSupport.getValue();
    }
}
