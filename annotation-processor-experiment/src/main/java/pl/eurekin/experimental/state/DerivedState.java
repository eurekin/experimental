package pl.eurekin.experimental.state;

import pl.eurekin.experimental.ChangedPropertyListener;

public abstract class DerivedState extends StatelessObservableState {
    private final ChangedPropertyListener<Boolean> listener = new ChangedPropertyListener<Boolean>() {
        @Override
        public void propertyChanged(Boolean oldValue, Boolean newValue) {
            update();
        }
    };
    private final ObservableState[] baseStates;
    private boolean lastValue;

    public DerivedState(ObservableState... baseStates) {
        this.baseStates = baseStates;
        for (ObservableState state : baseStates)
            state.registerChangeListener(listener);
        initializeStartValue();
    }

    protected abstract boolean value(ObservableState... baseStates);

    void initializeStartValue() {
        lastValue = value();
    }

    @Override
    public boolean value() {
        return value(baseStates);
    }

    void update() {
        boolean oldValue = lastValue;
        boolean newValue = value();
        lastValue = newValue;
        notifyOfStateChange(oldValue, newValue);
    }
}
