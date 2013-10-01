package experimental.incubator;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;

import javax.swing.AbstractButton;
import javax.swing.JToolBar;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.UIResource;
import javax.swing.text.JTextComponent;

/**
 *
 * Does not honor the background color.
 *
 * But, for the white fields, looks a lot better than the Swing default for Windows 7.
 *
 * @author Rekin
 *
 */
public class CorrectedWindowsTextAreaBorder extends AbstractBorder implements UIResource {
    private static final long serialVersionUID = 1L;
    private  FocusedWinBrdDef focused = new FocusedWinBrdDef();
    private  UnfocusedWinBrdDef unfocused = new UnfocusedWinBrdDef();

    private interface WinBrdDef {
        public  Color t();
        public  Color tm();
        public  Color tl();
        public  Color l();
        public  Color lb();
        public  Color b();
        public  Color m();
    }

    private static class UnfocusedWinBrdDef implements WinBrdDef{
        public  Color T = new Color(171,173,179);
        public  Color TM = new Color(187,189,194);
        public  Color TL = new Color(214,215,217);
        public  Color L = new Color(226,227,234);
        public  Color LB = new Color(235,235,238);
        public  Color B = new Color(227,233,239);
        public  Color M = new Color(233,236,240);
        @Override
        public Color t() {
            return T;
        }
        @Override
        public Color tm() {
            return TM;
        }
        @Override
        public Color tl() {
            return TL;
        }
        @Override
        public Color l() {
            return L;
        }
        @Override
        public Color lb() {
            return LB;
        }
        @Override
        public Color b() {
            return B;
        }
        @Override
        public Color m() {
            return M;
        }

    }
    private static class FocusedWinBrdDef implements WinBrdDef {
        public  Color T = new Color(61,123,173);
        public  Color TM = new Color(92,147,188);
        public  Color TL = new Color(182,205,224);
        public  Color L = new Color(181,207,231);
        public  Color LB = new Color(227,237,246);
        public  Color B = new Color(183,217,237);
        public  Color M = new Color(198,222,238);
        @Override
        public Color t() {
            return T;
        }
        @Override
        public Color tm() {
            return TM;
        }
        @Override
        public Color tl() {
            return TL;
        }
        @Override
        public Color l() {
            return L;
        }
        @Override
        public Color lb() {
            return LB;
        }
        @Override
        public Color b() {
            return B;
        }
        @Override
        public Color m() {
            return M;
        }

    }


    CorrectedWindowsTextAreaBorder() {
        super();
    }

    public Insets getBorderInsets(Component c, Insets insets) {
        Insets margin = null;
        //
        // Ideally we'd have an interface defined for classes which
        // support margins (to avoid this hackery), but we've
        // decided against it for simplicity
        //
        if (c instanceof AbstractButton) {
            margin = ((AbstractButton) c).getMargin();
        } else if (c instanceof JToolBar) {
            margin = ((JToolBar) c).getMargin();
        } else if (c instanceof JTextComponent) {
            margin = ((JTextComponent) c).getMargin();
        }
        insets.top = (margin != null ? margin.top : 0);
        insets.left = (margin != null ? margin.left : 0);
        insets.bottom = (margin != null ? margin.bottom : 0);
        insets.right = (margin != null ? margin.right : 0);

        return insets;
    }

    /**
     * Paints the border for the specified component with the
     * specified position and size.
     * @param c the component for which this border is being painted
     * @param g the paint graphics
     * @param x the x position of the painted border
     * @param y the y position of the painted border
     * @param width the width of the painted border
     * @param height the height of the painted border
     */
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        if ((g instanceof Graphics2D)) {
            Graphics2D g2d = (Graphics2D) g;


            WinBrdDef f = c.hasFocus() ? focused : unfocused;

            Color oldColor = g2d.getColor();
            g2d.setColor(f.t());
            g2d.drawLine(x+2, y, x + width - 3, y);
            g2d.setColor(f.tm());
            g2d.fillRect(x+1, y, 1, 1);
            g2d.fillRect(width -2, y, 1, 1);
            g2d.setColor(f.tl());
            g2d.fillRect(x, y, 1, 1);
            g2d.fillRect(width -1, y, 1, 1);

            g2d.setColor(f.m());
            g2d.fillRect(x+1, y+1, 1, 1);
            g2d.fillRect(width-2, y+1, 1, 1);
            g2d.fillRect(x+1, y+height-2, 1, 1);
            g2d.fillRect(width-2, y+height-2, 1, 1);

            g2d.setColor(f.l());
            g2d.drawLine(x, y+1, x, y + height-1);
            g2d.drawLine(x+width-1, y+1, x+width-1, y + height-1);

            g2d.setColor(f.lb());
            g2d.fillRect(x, y+width-1, 1, 1);
            g2d.fillRect(x + width -1, y+width-1, 1, 1);

            g2d.setColor(f.b());
            g2d.drawLine(x+1, y+height-1, x + width - 2, y+height-1);
            g2d.setColor(oldColor);

        }
    }
}
