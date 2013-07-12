package pl.eurekin.experimental.state;

import pl.eurekin.experimental.Observable;

public class ObservableStateInterpreterAdapter<I>
        extends ObservableInterpreterAdapter<I, Boolean>
        implements ObservableState {

    private boolean lastKnownValue;

    public ObservableStateInterpreterAdapter(Observable<I> observableInput, Interpreter<I, Boolean> interpreter) {
        super(observableInput, interpreter);
    }

    @Override
    protected void updateInterpreted(Boolean oldValue, Boolean newValue) {
        lastKnownValue = newValue;
        super.updateInterpreted(oldValue, newValue);
    }

    @Override
    public boolean value() {
        return lastKnownValue;
    }
}
