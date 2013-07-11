package pl.eurekin.experimental.fluent;

import pl.eurekin.experimental.state.*;
import pl.eurekin.experimental.swing.ActivateButton;

import javax.swing.*;

public class StateBuilder {

    private ObservableState state;
    private GlobalEventCounter counter;

    public StateBuilder(ObservableState state) {
        this.counter = new GlobalEventCounter();
        this.state = wrap(state);
    }

    private StateBuilder(ObservableState state, GlobalEventCounter counter) {
        this.counter = counter;
        this.state = wrap(state);
    }

    private ObservableState wrap(ObservableState state) {
        return new StateProxy(state, counter);
    }

    public void activate(JButton deleteButton) {
        new ActivateButton(new FilteringObservable(state, counter), deleteButton);
    }

    public StateBuilder and(ObservableState anotherState) {
        return new StateBuilder(wrap(new AndState(state, wrap(anotherState))), counter);
    }

    public StateBuilder or(ObservableState anotherState) {
        return new StateBuilder(wrap(new OrState(state, wrap(anotherState))), counter);
    }
}
