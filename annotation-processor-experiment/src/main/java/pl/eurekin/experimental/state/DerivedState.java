package pl.eurekin.experimental.state;

import pl.eurekin.experimental.ChangedPropertyListener;

public abstract class DerivedState extends StatelessObservableState {
    private final ObservableState[] baseStates;
    private boolean lastValue;


    public DerivedState(ObservableState... baseStates) {
        this.baseStates = baseStates;
        ChangedPropertyListener<Boolean> listener = new ChangedPropertyListener<Boolean>() {
            @Override
            public void beginNotifying() {
                onBeginNotifying();
            }

            @Override
            public void propertyChanged(Boolean oldValue, Boolean newValue) {
                update();
            }

            @Override
            public void finishNotifying() {
                onFinishNotifying();
            }
        };
        for (ObservableState state : baseStates)
            state.registerChangeListener(listener);
        initializeStartValue();
    }

    protected abstract boolean value(ObservableState... baseStates);

    void initializeStartValue() {
        lastValue = get();
    }

    @Override
    public Boolean get() {
        return value(baseStates);
    }

    void update() {
        boolean oldValue = lastValue;
        boolean newValue = get();
        lastValue = newValue;
        notifyOfStateChange(oldValue, newValue);
    }
}
