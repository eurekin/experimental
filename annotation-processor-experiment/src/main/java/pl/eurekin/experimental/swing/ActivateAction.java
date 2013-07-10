package pl.eurekin.experimental.swing;

import pl.eurekin.experimental.ChangedPropertyListener;
import pl.eurekin.experimental.state.ObservableState;

import javax.swing.*;

public class ActivateAction {
    public ActivateAction(ObservableState state, Action action) {
        final Action actionToUpdate = action;
        state.registerChangeListener( new ChangedPropertyListener<Boolean>() {
            @Override
            public void propertyChanged(Boolean oldValue, Boolean newValue) {
                actionToUpdate.setEnabled(newValue);
            }
        });
        actionToUpdate.setEnabled(state.value());
    }
}
