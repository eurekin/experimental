package experimental.guireloader;

import javax.swing.*;

/**
 * @author gmatoga
 */
public class ReloadablePanel extends JPanel {
    public ReloadablePanel() {
        super();
        add(new JLabel("Reloaded"));
        toString();
    }

    @Override
    public String toString() {
        System.out.println("invoked2");
        return "Version 8";

    }
}
