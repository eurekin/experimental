package experimental;

import experimental.chess.Board;
import experimental.sweeper.Sweeper;
import pl.eurekin.editor.LineDefinitionEditorView;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

/**
 * @author gmatoga
 */
public class ShowOffApplication {

    private JFrame frame;
    private JPanel mainPanel;
    private JTabbedPane tabbedPane;

    public static void main(String... args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        new ShowOffApplication().showGUI();
    }

    private void showGUI() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                initiateAndShowOnEDT();
            }
        });
    }

    private void initiateAndShowOnEDT() {
        constructMainFrame();
        addMainPanel();
        showMainFrame();
    }

    private void addMainPanel() {
        constructMainPanel();
        frame.add(mainPanel);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
    }

    private void constructMainPanel() {
        mainPanel = new JPanel();
        constructTabbedFrame();
        mainPanel.add(tabbedPane);
    }

    private void constructTabbedFrame() {
        tabbedPane = new JTabbedPane();
        addAllRegisteredShowOffViewsToJTabbedPane();
    }

    private void addAllRegisteredShowOffViewsToJTabbedPane() {
        final LineDefinitionEditorView lineDefinitionEditorView = new LineDefinitionEditorView();
        lineDefinitionEditorView.initialize();
        Icon icon1 = null;
        try {
            icon1 = new ImageIcon(ImageIO.read(this.getClass().getResourceAsStream("/table.png")));
        } catch (IOException e) {

        }
        tabbedPane.addTab("Table", icon1, lineDefinitionEditorView.panel1);

        Sweeper sweeper = new Sweeper(20, 40);
        final JPanel sweeperFrame = sweeper.getMainPanel();
        Icon sweeperIcon = sweeper.getIcon();
        tabbedPane.addTab("MineSweeper", sweeperIcon,sweeperFrame);


        final JPanel construct = new Board().construct();
        final JPanel outerPanel = new JPanel(new GridBagLayout());
        outerPanel.add(construct);
        Icon icon = null;
        try {
            icon = new ImageIcon(ImageIO.read(this.getClass().getResourceAsStream("/chess.png")));
        } catch (IOException e) {

        }
        tabbedPane.addTab("Chess",icon, outerPanel);
    }

    private void showMainFrame() {
        frame.setVisible(true);
    }

    private JFrame constructMainFrame() {
        frame = new JFrame("Experimental show-off");
        try {
            frame.setIconImage(ImageIO.read(this.getClass().getResource("/ico_lab.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        frame.pack();
        return  frame;
    }
}
