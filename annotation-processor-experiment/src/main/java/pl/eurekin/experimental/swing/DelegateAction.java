package pl.eurekin.experimental.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author greg.matoga@gmail.com
 */
public class DelegateAction implements ActionListener {
    private final Runnable action;

    public DelegateAction(Runnable action) {
        this.action = action;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        action.run();
    }
}
