package pl.eurekin.experimental.state;

import pl.eurekin.experimental.ChangedPropertyListener;
import pl.eurekin.experimental.Observable;
import pl.eurekin.experimental.StatelessPropertyChangeSupport;

public abstract class StatelessObservable<T> implements Observable<T> {
    private final StatelessPropertyChangeSupport<T> changeSupport = new StatelessPropertyChangeSupport<T>();

    public void registerChangeListener(ChangedPropertyListener<T> listener) {
        changeSupport.registerNewListener(listener);
    }

    public void unregisterChangeListener(ChangedPropertyListener<T> listener) {
        changeSupport.unregisterListener(listener);
    }

    void notifyOfStateChange(T oldValue, T newValue) {
        changeSupport.firePropertyChangeEvent(oldValue, newValue);
    }

    void onBeginNotifying() {
        changeSupport.beginNotifying();
    }

    void onFinishNotifying() {
        changeSupport.finishNotifying();
    }
}
