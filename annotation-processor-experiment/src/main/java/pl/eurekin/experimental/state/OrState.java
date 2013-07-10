package pl.eurekin.experimental.state;

public class OrState extends DerivedState {

    public OrState(ObservableState lhsState, ObservableState rhsState) {
        super(lhsState, rhsState);
    }

    @Override
    protected boolean value(ObservableState... baseStates) {
        return baseStates[0].value() || baseStates[1].value();
    }
}
