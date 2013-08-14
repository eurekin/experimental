package pl.eurekin.experimental.state;

import pl.eurekin.experimental.Observable;

/**
* @author gmatoga
*/
public class OverridableState extends DerivedState {
    private final ObservableState base;
    private boolean valueOverridden = false;
    private boolean valueToReturnInCaseOfOverride = false;


    public OverridableState(ObservableState observableState) {
        super(observableState);
        valueOverridden = false;
        valueToReturnInCaseOfOverride = false;
        base = observableState;
    }

    public void forceToReturn(boolean value) {
        valueOverridden = true;
        valueToReturnInCaseOfOverride = value;
        super.update(false);
    }

    public void returnToNormal() {
        valueOverridden = false;
        super.update(false);
    }


    @Override
    protected Boolean value(Observable<Boolean>... baseStates) {
        return valueOverridden ? valueToReturnInCaseOfOverride : baseStates[0].get();
    }
}
