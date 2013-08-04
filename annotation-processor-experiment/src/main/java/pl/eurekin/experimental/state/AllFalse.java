package pl.eurekin.experimental.state;

import pl.eurekin.experimental.Observable;

import java.util.List;

/**
 * @author greg.matoga@gmail.com
 */
public class AllFalse extends DerivedState {
    public AllFalse(Observable<Boolean>... baseStates) {
        super(baseStates);
    }

    public AllFalse(List<Observable<Boolean>> observables) {
        super(observables);
    }

    @Override
    public Boolean value(Observable<Boolean>... baseStates) {
        for (Observable<Boolean> state : baseStates) {
            if (state.get()) return false;
        }
        return true;
    }
}
