package pl.eurekin.experimental;

import pl.eurekin.experimental.fluent.StateBuilder;
import pl.eurekin.experimental.state.Interpreter;
import pl.eurekin.experimental.state.NegateState;
import pl.eurekin.experimental.state.ObservableState;
import pl.eurekin.experimental.state.ObservableStateInterpreterAdapter;

public final class ExpressionBuilder {
    private ExpressionBuilder() {
    }

    public static StateBuilder when(ObservableState state) {
        return new StateBuilder(state);
    }

    public static ObservableState not(ObservableState state) {
        return new NegateState(state);
    }
}
