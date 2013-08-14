package experimental.sweeper;

import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.jxlayer.plaf.effect.LayerEffect;
import org.jdesktop.jxlayer.plaf.ext.LockableUI;

import javax.swing.*;
import java.awt.*;

/**
 * Subclass of the {@link org.jdesktop.jxlayer.plaf.ext.LockableUI} which shows a button
 * that allows to unlock the {@link org.jdesktop.jxlayer.JXLayer} when it is locked
 */
public class EnhancedLockableUI extends LockableUI {
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
