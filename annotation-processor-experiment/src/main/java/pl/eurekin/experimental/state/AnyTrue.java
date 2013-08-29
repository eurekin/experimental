package pl.eurekin.experimental.state;

import pl.eurekin.experimental.Observable;

import java.util.List;

/**
 * @author greg.matoga@gmail.com
 */
public class AnyTrue extends CountingTruthsState {

    public AnyTrue(List<Observable<Boolean>> observables) {
        super(observables);
    }

    public AnyTrue(Observable<Boolean>... baseStates) {
        super(baseStates);
    }

    @Override
    public boolean condition(int count) {
        return count > 0;
    }
}
