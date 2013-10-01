package experimental.incubator;

import static java.awt.BorderLayout.EAST;
import static java.awt.BorderLayout.NORTH;
import static javax.swing.BorderFactory.createTitledBorder;
import static javax.swing.BoxLayout.Y_AXIS;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;


public class SwingViewsBuilder {

    public static JPanel createTitledPanel(String panelTitle) {
        JPanel panel = new JPanel();
        panel.setBorder(createTitledBorder(panelTitle));
        panel.setLayout(new BorderLayout());

        return panel;
    }

    public static JPanel createStandardTableWithButtonsView(JComponent mainTable, JButton... buttons) {
        JPanel tableWithButtonsPanel = new JPanel(new BorderLayout());
        tableWithButtonsPanel.add(mainTable);
        tableWithButtonsPanel.add(centerBorderLayoutPanel(createVerticalButtonPanel(buttons)), EAST);

        return tableWithButtonsPanel;
    }

    public static JComponent createVerticalButtonPanel(JComponent... components) {
        Box panel = new Box(Y_AXIS);

        for (JComponent component : components) {
            setCenterXAligment(component);
            setNoRestrictedMaximumSize(component);
            panel.add(component);
        }

        return panel;
    }

    public static JComponent createHorizontalEqualSizePanel(JComponent... components) {
        Box panel = new Box(BoxLayout.X_AXIS);

        for (JComponent component : components) {
            setCenterYAligment(component);
            setNoRestrictedMaximumSize(component);
            panel.add(component);
        }

        return panel;
    }

    public static void setCenterYAligment(JComponent button) {
        button.setAlignmentY(JComponent.CENTER_ALIGNMENT);
    }

    public static void setCenterXAligment(JComponent button) {
        button.setAlignmentX(JComponent.CENTER_ALIGNMENT);
    }

    public static void setNoRestrictedMaximumSize(JComponent button) {
        button.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
    }

    public static JComponent centerBorderLayoutPanel(JComponent component) {
        JPanel panel = new JPanel();
        panel.add(component, NORTH);

        return panel;
    }

    public static void setRowLayout(JPanel toConfigure, int rows) {
        toConfigure.setLayout(new GridLayout(rows, 0, 0, 0));
    }
    public static JComponent configureAsWindowsTable(JTable table2) {
        JPanel wrappingPanel = new JPanel(new BorderLayout());
        table2.setGridColor(new Color(227,227,227));
        JScrollPane fakeScrollPane = new JScrollPane(
                table2,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
                );
        Border border = fakeScrollPane.getBorder();
        fakeScrollPane.setBorder(BorderFactory.createEmptyBorder());

        wrappingPanel.setBorder(border);
        wrappingPanel.add(fakeScrollPane, BorderLayout.CENTER);
        wrappingPanel.add(fakeScrollPane.getVerticalScrollBar(), BorderLayout.EAST);

        return wrappingPanel;
    }
    public static void setDemoModelOn(JTable table2) {
        table2.setModel(new DefaultTableModel(new String[][]{
                new String[]{"a","b","c","b","c","b","c","b","c","b","c","b","c"},
                new String[]{"a","b","c","b","c","b","c","b","c","b","c","b","c"},
                new String[]{"a","b","c","b","c","b","c","b","c","b","c","b","c"},
                new String[]{"a","b","c","b","c","b","c","b","c","b","c","b","c"},
                new String[]{"a","b","c","b","c","b","c","b","c","b","c","b","c"},
                new String[]{"a","b","c","b","c","b","c","b","c","b","c","b","c"},
                new String[]{"a","b","c","b","c","b","c","b","c","b","c","b","c"},
                new String[]{"a","b","c","b","c","b","c","b","c","b","c","b","c"},
                new String[]{"a","b","c","b","c","b","c","b","c","b","c","b","c"},
                new String[]{"a","b","c","b","c","b","c","b","c","b","c","b","c"},
                new String[]{"a","b","c","b","c","b","c","b","c","b","c","b","c"},
                new String[]{"a","b","c","b","c","b","c","b","c","b","c","b","c"},
                new String[]{"a","b","c","b","c","b","c","b","c","b","c","b","c"},
                new String[]{"a","b","c","b","c","b","c","b","c","b","c","b","c"},
                new String[]{"a","b","c","b","c","b","c","b","c","b","c","b","c"},
                new String[]{"a","b","c","b","c","b","c","b","c","b","c","b","c"},
                new String[]{"a","b","c","b","c","b","c","b","c","b","c","b","c"},
                new String[]{"a","b","c","b","c","b","c","b","c","b","c","b","c"},
                new String[]{"a","b","c","b","c","b","c","b","c","b","c","b","c"},
                new String[]{"a","b","c","b","c","b","c","b","c","b","c","b","c"},
                new String[]{"a","b","c","b","c","b","c","b","c","b","c","b","c"},
                new String[]{"a","b","c","b","c","b","c","b","c","b","c","b","c"},
                new String[]{"a","b","c","b","c","b","c","b","c","b","c","b","c"},
                new String[]{"a","b","c","b","c","b","c","b","c","b","c","b","c"},
                new String[]{"a","b","c","b","c","b","c","b","c","b","c","b","c"},
                new String[]{"a","b","c","b","c","b","c","b","c","b","c","b","c"},
                new String[]{"a","b","c","b","c","b","c","b","c","b","c","b","c"},
                new String[]{"a","b","c","b","c","b","c","b","c","b","c","b","c"},
                new String[]{"a","b","c","b","c","b","c","b","c","b","c","b","c"},
                new String[]{"a","b","c","b","c","b","c","b","c","b","c","b","c"},
        }, new String[]{"column a","column b","column c","b","c","b","c","b","c","b","c","b","c"}));
    }

    public static void showInDemoFrame(JComponent view) {
        JFrame frame = new ResizableJFrameDemo();
        frame.add(view);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setVisible(true);
    }

    public static void setSystemLAF() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showInDemoFrameEDTsafe(final JComponent view) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                showInDemoFrame(view);
            }
        });
    }

    public static JComponent wrapInNorthBorderLayout(JComponent towrap) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(towrap, BorderLayout.NORTH);
        return wrapper;
    }
}
