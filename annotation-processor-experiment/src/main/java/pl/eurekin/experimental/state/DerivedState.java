package pl.eurekin.experimental.state;

import pl.eurekin.experimental.Observable;

import java.util.List;

public abstract class DerivedState
        extends DerivedObservable<Boolean>
        implements ObservableState {

    public DerivedState(Observable<Boolean>... baseStates) {
        super(baseStates);
    }

    public DerivedState(List<Observable<Boolean>> observables) {
        super(observables);
    }
}
