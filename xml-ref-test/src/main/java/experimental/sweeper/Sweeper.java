package experimental.sweeper;

import pl.eurekin.experimental.ChangedPropertyListener;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * @author greg.matoga@gmail.com
 */
public class Sweeper {

    private final MineField mineField;
    private final SweeperController sweeperController;
    private int rows;
    private int columns;
    private JPanel mainPanel;

    public Sweeper(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;

        mineField = new MineField(rows, columns);
        sweeperController = new SweeperController(mineField);
        sweeperController.isLost().registerChangeListener(new ChangedPropertyListener<Boolean>() {
            @Override
            public void beginNotifying() {
            }

            @Override
            public void propertyChanged(Boolean oldValue, Boolean newValue) {
                if(newValue) {
                    System.out.println("YOU LOST!!!");
                    ((CardLayout) mainPanel.getLayout()).show(mainPanel, "lost");
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
                if(newValue==true) {
                    System.out.println("YOU WON!!!");
                    ((CardLayout) mainPanel.getLayout()).show(mainPanel, "won");

                }
            }

            @Override
            public void finishNotifying() {
            }
        });

        putRandomMines(rows, columns);
    }

    private void putRandomMines(int rows, int columns) {
        for (int i = 0; i < 10; i++) {
            mineField.get(
                    (int) (Math.random() * rows),
                    (int) (Math.random() * columns)
            ).mine.set(true);
        }
    }

    public static void main(String... args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        Sweeper sweeper = new Sweeper(10, 20);
        sweeper.run();
    }

    private void run() throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

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
            frame.setIconImage(ImageIO.read(
                    this.getClass().getResourceAsStream("/minesweeper.png")));
        } catch (IOException e) {

        };
        return frame;
    }

    private JPanel getMainPanel() {
        JPanel minePanel = getMinePanel();
        JPanel lostPanel = getSingleTextPanel("<html>YOU<br>LOST");
        JPanel wonPanel = getSingleTextPanel("<html>YOU<br>WON!!!");

        mainPanel = new JPanel(new CardLayout());
        mainPanel.add(minePanel);
        mainPanel.add(lostPanel, "lost");
        mainPanel.add(wonPanel, "won");

        return mainPanel;
    }

    private JPanel getMinePanel() {
        JPanel minePanel = new JPanel();
        GridLayout layout = new GridLayout(0, columns);
        layout.setHgap(-4);
        layout.setVgap(-4);
        minePanel.setLayout(layout);
        minePanel.setBackground(Color.LIGHT_GRAY);
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                minePanel.add(getButtonAt(row, col));
            }
        }
        return minePanel;
    }

    private JPanel getSingleTextPanel(String labelText) {
        JPanel lostPanel = new JPanel(new BorderLayout());
        JLabel lostLabel = new JLabel(labelText);
        lostLabel.setFont(lostLabel.getFont().deriveFont(50.0f));
        lostLabel.setHorizontalAlignment(JLabel.CENTER);
        lostLabel.setForeground(Color.red);
        lostPanel.add(lostLabel, BorderLayout.CENTER);
        return lostPanel;
    }

    private JComponent getButtonAt(final int row, final int col) {
        final JToggleButton  jButton = new JToggleButton();
        jButton.setPreferredSize(new Dimension(30, 30));
        SweeperController.FieldElement fieldElement = mineField.get(row, col);
        Integer minesInNeib = fieldElement.countMinesInNeighborhood().get();
        jButton.setMargin(new Insets(0, 0, 0, 0));
        jButton.setBorderPainted(false);
        jButton.setFocusPainted(false);
        final String text = fieldElement.mine.get() ? "<html><B>X" : minesInNeib == 0 ? " " : minesInNeib.toString();
//        jButton.setText(                text        );
        mineField.get(row, col).uncovered().registerChangeListener(new ChangedPropertyListener<Boolean>() {
            @Override
            public void beginNotifying() {

            }

            @Override
            public void propertyChanged(Boolean oldValue, final Boolean newValue) {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        jButton.setEnabled(!mineField.get(row, col).uncovered().get());
                        jButton.setText(text);
                    }
                });
            }

            @Override
            public void finishNotifying() {
                //To change body of implemented methods use File | S    ettings | File Templates.
            }
        });
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
}
