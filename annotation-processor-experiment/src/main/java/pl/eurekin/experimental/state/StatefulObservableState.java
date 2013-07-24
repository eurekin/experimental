package pl.eurekin.experimental.state;

import pl.eurekin.experimental.ChangedPropertyListener;
import pl.eurekin.experimental.StatefulPropertyChangeSupport;

public class StatefulObservableState implements ObservableState {
    private final StatefulPropertyChangeSupport<Boolean> changeSupport;

    public StatefulObservableState(Boolean initialValue) {
        this.changeSupport = new StatefulPropertyChangeSupport<>(initialValue);
    }

    public void registerChangeListener(ChangedPropertyListener<Boolean> listener) {
        changeSupport.registerNewListener(listener);
    }

    public void unregisterChangeListener(ChangedPropertyListener<Boolean> listener) {
        changeSupport.unregisterListener(listener);
    }

    void notifyOfStateChange(boolean newValue) {
        changeSupport.firePropertyChangeEvent(newValue);
    }

    @Override
    public Boolean get() {
        return changeSupport.getValue();
    }
}
