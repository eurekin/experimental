package experimental.sweeper;

import com.jhlabs.image.BlurFilter;
import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.jxlayer.plaf.effect.BufferedImageOpEffect;
import org.jdesktop.jxlayer.plaf.effect.LayerEffect;
import org.jdesktop.jxlayer.plaf.ext.LockableUI;
import pl.eurekin.experimental.ChangedPropertyListener;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import static experimental.sweeper.SweeperController.FieldElement;

/**
 * @author greg.matoga@gmail.com
 */
public class Sweeper {

    private final MineField mineField;
    private final SweeperController sweeperController;
    private int rows;
    private int columns;
    private JPanel mainPanel;
    private JXLayer<JComponent> mainPanelLayer;
    private LockableUI blurUI;
    private JButton restartButton;
    private ChangedPropertyListener reactWhenButtonNeedsAnUpdate;
    private JLabel outcomeLabel;
    private String inProgressText = "...game in progress...";
    private String winText = "<html> YOU WIN!";
    private String lostText = "<html> YOU LOST";
    private String finishedText = "...finished!";

    public Sweeper(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;

        mineField = new MineField(rows, columns);
        sweeperController = new SweeperController(mineField);
        restartButton = new JButton("RESTART");
        restartButton.setFont(new Font("Dialog", Font.PLAIN, 50));
        int i = 10;
        int s = 40;
        restartButton.setMargin(new Insets(i,s,i,s));
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restart();
            }
        });
        sweeperController.isLost().registerChangeListener(new ChangedPropertyListener<Boolean>() {
            @Override
            public void beginNotifying() {
            }

            @Override
            public void propertyChanged(Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    System.out.println("YOU LOST!!!");
                    lock(true);
                }
            }

            @Override
            public void finishNotifying() {
            }
        });
        sweeperController.isWon().registerChangeListener(new ChangedPropertyListener<Boolean>() {
            @Override
            public void beginNotifying() {

            }

            @Override
            public void propertyChanged(Boolean oldValue, Boolean newValue) {
                if (newValue == true) {
                    System.out.println("YOU WON!!!");
                    lock(true);
                }
            }

            @Override
            public void finishNotifying() {
            }
        });

        putRandomMines(rows, columns);
    }

    public static void main(String... args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        Sweeper sweeper = new Sweeper(20, 40);
        sweeper.run();
    }

    private void restart() {
        for (FieldElement fe : mineField.allFields()) fe.overrideOn();
        for (FieldElement fe : mineField.allFields()) fe.reset();
        for (FieldElement fe : mineField.allFields()) fe.mine.set(false);
        putRandomMines(rows, columns);
        for (FieldElement fe : mineField.allFields()) fe.overrideOff();

        lock(false);
    }

    private void lock(boolean lock) {
        reactWhenButtonNeedsAnUpdate.propertyChanged(null, null);
        blurUI.setLocked(lock);
    }

    private void putRandomMines(int rows, int columns) {
        for (int i = 0; i < 80; i++) {
            mineField.get(
                    (int) (Math.random() * rows),
                    (int) (Math.random() * columns)
            ).mine.set(true);
        }
    }

    private void run() throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {


        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = constructMainFrame();
                frame.setVisible(true);
            }
        });
    }

    private JFrame constructMainFrame() {
        JFrame frame = new JFrame("MineSweeper");
        JPanel mines = getMainPanel();
        frame.setContentPane(mines);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        try {
            frame.setIconImage(ImageIO.read(this.getClass().getResourceAsStream("/minesweeper.png")));
        } catch (IOException e) {
        }
        return frame;
    }

    private JPanel getMainPanel() {
        JPanel minePanel = getMinePanel();
        JPanel outcomePanel = getControlPanel("<html>YOU<br>LOST");

        mainPanelLayer = new JXLayer<JComponent>(minePanel);

        blurUI = new EnhancedLockableUI(outcomePanel, new BufferedImageOpEffect(new BlurFilter()));
        mainPanelLayer.setUI(blurUI);

        mainPanel = new JPanel(new CardLayout());
        mainPanel.add(mainPanelLayer);
        mainPanel.add(new JPanel(), "lost");
        mainPanel.add(new JPanel(), "won");

        return mainPanel;
    }

    private JPanel getMinePanel() {
        JPanel minePanel = new JPanel();
        GridLayout layout = new GridLayout(0, columns);
        layout.setHgap(0);
        layout.setVgap(0);
        minePanel.setLayout(layout);
        minePanel.setBackground(Color.LIGHT_GRAY);
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                minePanel.add(getButtonAt(row, col));
            }
        }
        return minePanel;
    }

    private JPanel getControlPanel(String labelText) {
        JPanel verticalFlowPanel = new JPanel();
        verticalFlowPanel.setLayout(new BoxLayout(verticalFlowPanel, BoxLayout.Y_AXIS));
        verticalFlowPanel.setOpaque(true);
        final int inset = 30;
        verticalFlowPanel.setBorder(BorderFactory.createEmptyBorder(inset, inset, inset, inset));
        verticalFlowPanel.setBackground(new Color(255, 255, 255, 80));

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        outcomeLabel = new JLabel(labelText);
        outcomeLabel.setFont(outcomeLabel.getFont().deriveFont(50.0f));
        outcomeLabel.setHorizontalAlignment(JLabel.CENTER);
        outcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        outcomeLabel.setForeground(Color.red);

        restartButton.setAlignmentX(Component.CENTER_ALIGNMENT);


        verticalFlowPanel.add(outcomeLabel);
        verticalFlowPanel.add(Box.createVerticalStrut(inset));
        verticalFlowPanel.add(restartButton);

        panel.add(verticalFlowPanel);

        return panel;
    }

    private JComponent getButtonAt(final int row, final int col) {
        final JToggleButton jButton = new JToggleButton();
        jButton.setPreferredSize(new Dimension(20, 20));
//de-spacing:
        jButton.setBorderPainted(false);
        jButton.setMargin(new Insets(0, 0, 0, 0));
        jButton.setBorderPainted(false);
        jButton.setFocusPainted(false);
        jButton.setCursor(new Cursor(Cursor.HAND_CURSOR));


        reactWhenButtonNeedsAnUpdate = new ChangedPropertyListener<Object>() {
            @Override
            public void beginNotifying() {

            }

            @Override
            public void propertyChanged(Object oldValue, final Object newValue) {
                FieldElement fieldElement = mineField.get(row, col);

                int minesInNeighborhood = fieldElement.countMinesInNeighborhood().get();
                boolean uncovered = fieldElement.uncovered().get();
                boolean finished = sweeperController.isFinished().get();
                boolean won = sweeperController.isWon().get();
                boolean lost = sweeperController.isLost().get();
                boolean mine = fieldElement.mine.get();

                final String text = !uncovered && !finished ? " " :
                        mine ? "<html><B>X" :
                                minesInNeighborhood == 0 || (finished && !uncovered) ? " " :
                                        "" + minesInNeighborhood;


                String outcomeText = won ? winText :
                        lost ? lostText :
                                finished ? finishedText : inProgressText;

                outcomeLabel.setText(outcomeText);
                jButton.setToolTipText(fieldElement.debugText());
                jButton.setEnabled(!uncovered);
                jButton.setText(text);
            }

            @Override
            public void finishNotifying() {
            }
        };
        mineField.get(row, col).uncovered().registerChangeListener(reactWhenButtonNeedsAnUpdate);
        mineField.get(row, col).countMinesInNeighborhood().registerChangeListener(reactWhenButtonNeedsAnUpdate);
        sweeperController.isFinished().registerChangeListener(reactWhenButtonNeedsAnUpdate);
        jButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Move on " + row + ", " + col);
                sweeperController.moveOnFieldAt(row, col);
                jButton.setSelected(false);
            }
        });


        return jButton;
    }

    /**
     * Subclass of the {@link LockableUI} which shows a button
     * that allows to unlock the {@link JXLayer} when it is locked
     */
    public static class EnhancedLockableUI extends LockableUI {
        private JButton unlockButton = new JButton("Unlock");
        private JPanel somePanel;

        public EnhancedLockableUI(JPanel somePanel, LayerEffect... layerEffects) {
            super(layerEffects);
            this.somePanel = somePanel;
            somePanel.setVisible(false);
        }

        @Override
        @SuppressWarnings("unchecked")
        public void installUI(JComponent c) {
            super.installUI(c);
            JXLayer<JComponent> l = (JXLayer<JComponent>) c;
            l.getGlassPane().setLayout(new BorderLayout());
            l.getGlassPane().add(aComponentToShowOnUI(), BorderLayout.CENTER);
            aComponentToShowOnUI().setCursor(Cursor.getDefaultCursor());
        }

        private JComponent aComponentToShowOnUI() {
            return somePanel ;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void uninstallUI(JComponent c) {
            super.uninstallUI(c);
            JXLayer<JComponent> l = (JXLayer<JComponent>) c;
            l.getGlassPane().setLayout(new FlowLayout());
            l.getGlassPane().remove(aComponentToShowOnUI());
            aComponentToShowOnUI().setCursor(Cursor.getDefaultCursor());
        }

        public void setLocked(boolean isLocked) {
            super.setLocked(isLocked);
            aComponentToShowOnUI().setVisible(isLocked);

        }
    }
}
