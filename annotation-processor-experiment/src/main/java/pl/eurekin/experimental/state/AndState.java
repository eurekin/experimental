package pl.eurekin.experimental.state;

import pl.eurekin.experimental.Observable;

public class AndState extends DerivedState {

    public AndState(Observable<Boolean> lhsState, Observable<Boolean> rhsState) {
        super(lhsState, rhsState);
    }

    @Override
    public Boolean value(Observable<Boolean> ... states) {
        return states[0].get() && states[1].get();
    }

}
