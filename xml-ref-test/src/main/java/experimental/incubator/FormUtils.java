package experimental.incubator;

import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import com.sun.java.swing.plaf.windows.WindowsComboBoxUI;
import com.sun.java.swing.plaf.windows.WindowsTabbedPaneUI;

/**
 * @author greg.matoga@gmail.com
 */
@SuppressWarnings({"unused", "rawtypes"})
public class FormUtils {

    private static final class RepaintingFocusListener implements FocusListener {

        private final JComponent textarea;

        private RepaintingFocusListener(JComponent textarea) {
            this.textarea = textarea;
        }

        @Override
        public void focusLost(FocusEvent arg0) {
            textarea.repaint();
        }

        @Override
        public void focusGained(FocusEvent arg0) {
            textarea.repaint();
        }
    }
    public static void fixVisualQuirks(JFrame object) {
        visitAndFixRecursively(object.getRootPane());
    }

    public static void visitAndFixRecursively(JComponent jComponent) {
        tryToFix(jComponent);

        for(Component component : jComponent.getComponents())
            if (component instanceof JComponent)
                visitAndFixRecursively((JComponent) component);

    }

    public static void tryToFix(Object fieldObject) {
        if (fieldObject instanceof JComboBox) tryToFix((JComboBox) fieldObject);
        if (fieldObject instanceof JTextField) tryToFix((JTextField) fieldObject);
        if (fieldObject instanceof JTextArea) tryToFix((JTextArea) fieldObject);
    }

    public static void tryToFix(JTabbedPane object) {
        object.setUI(new WindowsTabbedPaneUI() {
            @Override
            protected void installDefaults() {
                super.installDefaults();
                highlight = Color.pink;
                lightHighlight = Color.green;
                shadow = Color.red;
                darkShadow = Color.cyan;
                focus = Color.yellow;
            }

            @Override
            protected Insets getContentBorderInsets(int tabPlacement) {
                return new Insets(0,0,0,0);
            }
        });
    }

    public static void tryToFix(JComboBox comboBox) {
        comboBox.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        comboBox.setUI(new WindowsComboBoxUI());
    }

    public static void tryToFix(JTextArea textarea) {
        textarea.setBorder(new CorrectedWindowsTextAreaBorder());
        textarea.addFocusListener(new RepaintingFocusListener(textarea));
    }

    public static void tryToFix(final JTextField textarea) {
        if(textarea.getBorder() instanceof LineBorder) {
            textarea.setBorder(new CorrectedWindowsTextAreaBorder());
            textarea.addFocusListener(new RepaintingFocusListener(textarea));
        }
    }
}
