package pl.eurekin.experimental.state;

import pl.eurekin.experimental.Observable;

import java.util.List;

/**
 * @author greg.matoga@gmail.com
 */
public abstract class CountingTruthsState extends DerivedState {
    int trueCount = 0;

    public CountingTruthsState(Observable<Boolean>... baseStates) {
        super(baseStates);
    }

    public CountingTruthsState(List<Observable<Boolean>> observables) {
        super(observables);
    }

    @Override
    protected void update(Boolean newValueFromSingleSource) {
        int factor = newValueFromSingleSource ? 1 : -1;
        trueCount += factor;
        super.update(newValueFromSingleSource);
    }

    @Override
    public Boolean value(Observable<Boolean>... baseStates) {
        int count = trueCount;
        return condition(count);
    }

    public abstract boolean condition(int count);
}
