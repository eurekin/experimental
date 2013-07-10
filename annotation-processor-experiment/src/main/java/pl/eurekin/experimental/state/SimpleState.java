package pl.eurekin.experimental.state;

public class SimpleState extends StatelessObservableState {

    private boolean state;

    public SimpleState(boolean initialState) {
        this.state = initialState;
    }

    public void set(boolean newValue) {
        boolean oldValue = state;
        state = newValue;
        super.notifyOfStateChange(oldValue, newValue);
    }

    @Override
    public boolean value() {
        return state;
    }
}
