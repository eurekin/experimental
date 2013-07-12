package pl.eurekin.experimental.state;

import pl.eurekin.experimental.ChangedPropertyListener;
import pl.eurekin.experimental.Observable;
import pl.eurekin.experimental.StatefulPropertyChangeSupport;

public class StateObservableAdapter implements ObservableState {

    private final Observable<Boolean> observable;
    private StatefulPropertyChangeSupport<Boolean> changeSupport;

    public StateObservableAdapter(Observable<Boolean> observable, boolean initialValue) {
        this.observable = observable;
        changeSupport = new StatefulPropertyChangeSupport<>(initialValue);
        observable.registerChangeListener(new ChangedPropertyListener<Boolean>() {
            @Override
            public void beginNotifying() {
                onBeginNotifying();
            }

            @Override
            public void propertyChanged(Boolean oldValue, Boolean newValue) {
                update(newValue);
            }

            @Override
            public void finishNotifying() {
                onFinishNotifying();
            }
        });
    }

    private void onBeginNotifying() {
        changeSupport.onBeginNotifying();
    }

    private void onFinishNotifying() {
        changeSupport.onFinishNotifying();
    }

    private void update(Boolean newValue) {
        changeSupport.firePropertyChangeEvent(newValue);
    }

    @Override
    public void registerChangeListener(ChangedPropertyListener<Boolean> listener) {
        observable.registerChangeListener(listener);
    }

    @Override
    public void unregisterChangeListener(ChangedPropertyListener<Boolean> listener) {
        observable.unregisterChangeListener(listener);
    }

    @Override
    public boolean value() {
        return changeSupport.getValue();
    }
}
