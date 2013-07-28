package pl.eurekin.experimental.fluent;

import pl.eurekin.experimental.SafePropertyListener;
import pl.eurekin.experimental.state.AndState;
import pl.eurekin.experimental.state.ObservableState;
import pl.eurekin.experimental.state.OrState;
import pl.eurekin.experimental.swing.ActivateButton;

import javax.swing.*;
import java.awt.*;

public class StateBuilder {

    private final ObservableState state;

    public StateBuilder(ObservableState state) {
        this.state = state;
    }

    public StateBuilder activate(JButton deleteButton) {
        new ActivateButton(state, deleteButton);
        return this;
    }

    public StateBuilder and(ObservableState anotherState) {
        return new StateBuilder(new AndState(state, anotherState));
    }

    public StateBuilder or(ObservableState anotherState) {
        return new StateBuilder(new OrState(state, anotherState));
    }

    public void enable(JTextField textField1) {
        new FieldEnabler(state, textField1);
    }

    public void showCard(final JPanel cardPanel, final String cardName) {
        final CardLayout layout = (CardLayout) cardPanel.getLayout();

        state.registerChangeListener(
                new SafePropertyListener<Boolean>(new SafePropertyListener.ChangeListener() {
                    @Override
                    public void act() {
                        if (state.get())
                            layout.show(cardPanel, cardName);
                    }
                }));
    }
}
