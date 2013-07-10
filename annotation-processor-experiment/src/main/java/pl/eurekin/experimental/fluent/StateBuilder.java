package pl.eurekin.experimental.fluent;

import pl.eurekin.experimental.state.AndState;
import pl.eurekin.experimental.state.ObservableState;
import pl.eurekin.experimental.state.OrState;
import pl.eurekin.experimental.swing.ActivateButton;

import javax.swing.*;

public class StateBuilder {

    private ObservableState state;

    public StateBuilder(ObservableState state) {
        this.state = state;
    }

    public void activate(JButton deleteButton) {
        new ActivateButton(state, deleteButton);
    }

    public StateBuilder and(ObservableState anotherState) {
        return new StateBuilder(new AndState(state, anotherState));
    }

    public StateBuilder or(ObservableState anotherState) {
        return new StateBuilder(new OrState(state, anotherState));
    }
}
