package pl.eurekin.experimental.state;

import pl.eurekin.experimental.Observable;

import java.util.List;

/**
 * @author greg.matoga@gmail.com
 */
public class AllTrue extends DerivedState {

    public AllTrue(Observable<Boolean>... baseStates) {
        super(baseStates);
    }

    public AllTrue(List<Observable<Boolean>> observables) {
        super(observables);
    }

    @Override
    public Boolean value(Observable<Boolean>... baseStates) {
        for (Observable<Boolean> state : baseStates) {
            if (!state.get()) return false;
        }
        return true;
    }
}
