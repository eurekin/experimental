package pl.eurekin.experimental.state;

import pl.eurekin.experimental.Observable;

public class NegateState extends DerivedState {

    public NegateState(Observable<Boolean> base) {
        super(base);
    }


    public Boolean value(Observable<Boolean>... states) {
        return !states[0].get();
    }
}
