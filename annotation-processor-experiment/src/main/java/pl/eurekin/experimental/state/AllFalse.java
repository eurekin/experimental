package pl.eurekin.experimental.state;

import pl.eurekin.experimental.Observable;

/**
 * @author greg.matoga@gmail.com
 */
public class AllFalse extends DerivedState {
    public AllFalse(Observable<Boolean>... baseStates) {
        super(baseStates);
    }

    @Override
    public Boolean value(Observable<Boolean>... baseStates) {
        for (Observable<Boolean> state : baseStates) {
            if (state.get()) return false;
        }
        return true;
    }
}
