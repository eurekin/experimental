package pl.eurekin.experimental.fluent;

import pl.eurekin.experimental.SafePropertyListener;
import pl.eurekin.experimental.state.ObservableState;

import javax.swing.*;
import javax.swing.text.JTextComponent;

/**
 * @author greg.matoga@gmail.com
 */
public class FieldEnabler {
    private final ObservableState state;
    private final JTextComponent textField1;

    public FieldEnabler(ObservableState state, JTextComponent textField1) {
        this.state = state;
        this.textField1 = textField1;
        state.registerChangeListener(new SafePropertyListener<Boolean>(new SafePropertyListener.ChangeListener() {
            @Override
            public void act() {
                onAct();
            }
        }));
        onAct();
    }

    private void onAct() {
        textField1.setEnabled(state.get());
        textField1.setEditable(state.get());
    }
}
