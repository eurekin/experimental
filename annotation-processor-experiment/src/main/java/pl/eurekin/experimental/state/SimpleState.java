package pl.eurekin.experimental.state;

public class SimpleState extends StatelessObservableState {

    private boolean state;

    public SimpleState(boolean initialState) {
        this.state = initialState;
    }

    public void beginNotifying() {
        super.onBeginNotifying();
    }
    public void finishNotifying() {
        super.onFinishNotifying();
    }

    public void set(boolean newValue) {
        boolean oldValue = state;
        state = newValue;
        //super.onBeginNotifying();
        super.notifyOfStateChange(oldValue, newValue);
        //super.onFinishNotifying();
    }

    @Override
    public boolean value() {
        return state;
    }
}
