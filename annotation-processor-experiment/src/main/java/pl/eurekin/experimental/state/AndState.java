package pl.eurekin.experimental.state;

public class AndState extends DerivedState {

    public AndState(ObservableState lhsState, ObservableState rhsState) {
        super(lhsState, rhsState);
    }

    @Override
    public boolean value(ObservableState ... states) {
        return states[0].value() && states[1].value();
    }

}
