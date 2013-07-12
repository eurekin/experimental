package pl.eurekin.experimental.swing;

import pl.eurekin.experimental.ChangedPropertyListener;
import pl.eurekin.experimental.state.ObservableState;

import javax.swing.*;

public class ActivateAction {
    public ActivateAction(ObservableState state, Action action) {
        final Action actionToUpdate = action;
        state.registerChangeListener(  new ChangedPropertyListener<Boolean>() {
            @Override
            public void beginNotifying() {
                throw new UnsupportedOperationException("not implemented");
            }

            @Override
            public void propertyChanged(Boolean oldValue, Boolean newValue) {
                actionToUpdate.setEnabled(newValue);
            }

            @Override
            public void finishNotifying() {
                throw new UnsupportedOperationException("not implemented");
            }
        });
        actionToUpdate.setEnabled(state.value());
    }
}
