package pl.eurekin.experimental.swing;

import pl.eurekin.experimental.ChangedPropertyListener;
import pl.eurekin.experimental.state.ObservableState;

import javax.swing.*;

public class ActivateButton {
    public ActivateButton(ObservableState observableState, JButton button) {
        if(button.getAction() != null) throw new ActionOnButtonExist();
        final JButton buttonToUpdate = button;
        observableState.registerChangeListener(new ChangedPropertyListener<Boolean>() {
            @Override
            public void propertyChanged(Boolean oldValue, Boolean newValue) {
                buttonToUpdate.setEnabled(newValue);
            }
        });
        buttonToUpdate.setEnabled(observableState.value());
    }

    public class ActionOnButtonExist extends RuntimeException {
        public ActionOnButtonExist() {
            super("Prefer to use ActivateAction");
        }
    }
}
