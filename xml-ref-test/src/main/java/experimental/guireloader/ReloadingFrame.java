package experimental.guireloader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

/**
 * @author gmatoga
 */
public class ReloadingFrame {

    private JFrame frame;
    private JPanel reloadingPanel;
    private JButton reloadButton;
    private JPanel topButtonBar;

    public static void main(String... args) {
        new ReloadingFrame().start();
    }

    private void start() {
        initiateGUI();
        showGUI();
    }

    private void showGUI() {
        frame.setVisible(true);
    }

    private void initiateGUI() {
        buildFrame();
        buildReloadingPane();
        buildTopButtonBar();
        buildReloadingButton();
    }

    private void buildReloadingButton() {
        reloadButton = new JButton("reload");
        reloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reloadAction();
            }
        });
        topButtonBar.add(reloadButton);
    }

    private void reloadAction() {
        reload();
    }

    private void reload() {
        Component reloadedComponent = reloadComponent();
        reloadingPanel.removeAll();
        if (reloadedComponent != null)
            reloadingPanel.add(reloadedComponent);
        else
            reloadingPanel.add(new JLabel("Error"));
    }

    private Component reloadComponent() {
        return null;
    }

    private void buildTopButtonBar() {
        topButtonBar = new JPanel(new FlowLayout());
        reloadingPanel.add(topButtonBar, BorderLayout.NORTH);
    }

    private void buildReloadingPane() {
        reloadingPanel = new JPanel(new BorderLayout());
        frame.add(reloadingPanel);
    }

    private void buildFrame() {
        frame = new JFrame("dynamic reloader");
        frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
    }
}
