package pl.eurekin.experimental.state;

import pl.eurekin.experimental.Observable;

public class OrState extends DerivedState {

    public OrState(Observable<Boolean> lhsState, Observable<Boolean> rhsState) {
        super(lhsState, rhsState);
    }

    @Override
    protected Boolean value(Observable<Boolean>... baseStates) {
        return baseStates[0].get() || baseStates[1].get();
    }
}
