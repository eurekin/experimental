package pl.eurekin.experimental.state;

import pl.eurekin.experimental.ChangedPropertyListener;
import pl.eurekin.experimental.StatelessPropertyChangeSupport;

public class StateProxy implements ObservableState {

    private final ObservableState base;
    final StatelessPropertyChangeSupport<Boolean> changeSupport;

    public StateProxy(ObservableState base) {
        this.base = base;
        changeSupport = new StatelessPropertyChangeSupport<>();
        base.registerChangeListener(new ChangedPropertyListener<Boolean>() {
            @Override
            public void beginNotifying() {
                onBeginNotifying();
            }

            @Override
            public void propertyChanged(Boolean oldValue, Boolean newValue) {
                update(oldValue, newValue);
            }

            @Override
            public void finishNotifying() {
                onFinishNotifying();
            }
        });
    }

    private void onFinishNotifying() {
        changeSupport.finishNotifying();
    }

    private void onBeginNotifying() {
        changeSupport.beginNotifying();
    }

    protected void update(Boolean oldValue, Boolean newValue) {
        changeSupport.firePropertyChangeEvent(oldValue, newValue);
    }

    public Boolean get() {
        return base.get();
    }

    public void registerChangeListener(ChangedPropertyListener<Boolean> listener) {
        changeSupport.registerNewListener(listener);
    }

    public void unregisterChangeListener(ChangedPropertyListener<Boolean> listener) {
        changeSupport.unregisterListener(listener);
    }
}
