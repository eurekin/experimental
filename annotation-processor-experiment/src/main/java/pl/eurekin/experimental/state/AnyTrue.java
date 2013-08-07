package pl.eurekin.experimental.state;

import pl.eurekin.experimental.Observable;

import java.util.List;

/**
 * @author greg.matoga@gmail.com
 */
public class AnyTrue extends DerivedState {
    int trueCount = 0;

    public AnyTrue(Observable<Boolean>... baseStates) {
        super(baseStates);
    }

    public AnyTrue(List<Observable<Boolean>> observables) {
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
        System.out.print(""+ hashCode() + " AnyTrue count = " + trueCount + " states = ");
        for (Observable<Boolean> state : baseStates) {
            System.out.print(" " + state.get().toString());
        }
        System.out.println();
        return trueCount != 0;
//        return false;
    }
}
