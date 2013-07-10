package pl.eurekin.experimental.state;

public class NegateState extends DerivedState {

    public NegateState(ObservableState base) {
        super(base);
    }

    public boolean value(ObservableState... states) {
        return !states[0].value();
    }
}
