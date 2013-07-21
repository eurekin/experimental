package pl.eurekin.experimental.swing;

import pl.eurekin.experimental.ChangedPropertyListener;
import pl.eurekin.experimental.state.ObservableState;

import javax.swing.*;

public class ActivateButton {
    private final JButton buttonToUpdate;
    private final ObservableState observableState;
    private int counter = 0;

    public ActivateButton(ObservableState observableState, JButton button) {
        this.observableState = observableState;
        if (button.getAction() != null) throw new ActionOnButtonExistException();
        buttonToUpdate = button;
        observableState.registerChangeListener(new ChangedPropertyListener<Boolean>() {
            @Override
            public void beginNotifying() {
                onBeginNotifying();
            }

            @Override
            public void propertyChanged(Boolean oldValue, Boolean newValue) {
                // silently ignore
                //
                // the data provided here is susceptible to data races
            }

            @Override
            public void finishNotifying() {
                onFinishNotifying();
            }
        });
        buttonToUpdate.setEnabled(observableState.value());
    }

    private void onFinishNotifying() {
        counter--;
        actIfAllEventsAreFired();
    }

    private void actIfAllEventsAreFired() {
        if (counter == 0)
            this.buttonToUpdate.setEnabled(observableState.value());

    }

    private void onBeginNotifying() {
        counter++;
    }

    public class ActionOnButtonExistException extends RuntimeException {
        public ActionOnButtonExistException() {
            super("Prefer to use ActivateAction");
        }
    }
}
