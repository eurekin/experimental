package pl.eurekin.experimental.swing;

import pl.eurekin.experimental.SafePropertyListener;
import pl.eurekin.experimental.state.ObservableState;

import javax.swing.*;

public class ActivateButton {
    private final AbstractButton buttonToUpdate;
    private final ObservableState observableState;

    public ActivateButton(ObservableState observableState, AbstractButton button) {
        this.observableState = observableState;
        if (button.getAction() != null) throw new ActionOnButtonExistException();
        buttonToUpdate = button;
        observableState.registerChangeListener(new SafePropertyListener<Boolean>(
                new SafePropertyListener.ChangeListener() {
                    @Override
                    public void act() {
                        onAct();
                    }
                }));
        buttonToUpdate.setEnabled(observableState.get());
    }

    private void onAct() {
        this.buttonToUpdate.setEnabled(observableState.get());
    }

    public class ActionOnButtonExistException extends RuntimeException {
        public ActionOnButtonExistException() {
            super("Prefer to use ActivateAction");
        }
    }
}
