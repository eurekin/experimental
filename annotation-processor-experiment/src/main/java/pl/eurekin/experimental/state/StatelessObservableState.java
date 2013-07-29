package pl.eurekin.experimental.state;

import pl.eurekin.experimental.ChangedPropertyListener;
import pl.eurekin.experimental.StatelessPropertyChangeSupport;

public abstract class StatelessObservableState implements ObservableState {
    private final StatelessPropertyChangeSupport<Boolean> changeSupport = new StatelessPropertyChangeSupport<Boolean>();

    public void registerChangeListener(ChangedPropertyListener<Boolean> listener) {
        changeSupport.registerNewListener(listener);
    }

    public void unregisterChangeListener(ChangedPropertyListener<Boolean> listener) {
        changeSupport.unregisterListener(listener);
    }

    void notifyOfStateChange(boolean oldValue, boolean newValue) {
        changeSupport.firePropertyChangeEvent(oldValue, newValue);
    }

    void onBeginNotifying() {
        changeSupport.beginNotifying();
    }

    void onFinishNotifying() {
        changeSupport.finishNotifying();
    }
}
