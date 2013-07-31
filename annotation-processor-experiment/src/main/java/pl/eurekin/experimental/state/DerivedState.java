package pl.eurekin.experimental.state;

import pl.eurekin.experimental.Observable;

public abstract class DerivedState
        extends DerivedObservable<Boolean>
        implements ObservableState {

    protected DerivedState(Observable<Boolean>... baseStates) {
        super(baseStates);
    }
}
