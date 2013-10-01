package experimental.incubator;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;

public class ResizableJFrameDemo extends JFrame {

    private static final long serialVersionUID = 1L;

    private final static int width = 580, height = 350;

    private java.awt.geom.GeneralPath gp;

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        int w = getWidth();
        int h = getHeight();
        g.setColor(new Color(150, 150, 150, 200));
        g.drawLine(w - 7, h, w, h - 7);
        g.drawLine(w - 11, h, w, h - 11);
        g.drawLine(w - 15, h, w, h - 15);

        gp = new java.awt.geom.GeneralPath();
        gp.moveTo(w - 23, h);
        gp.lineTo(w, h - 23);
        gp.lineTo(w, h);
        gp.closePath();
    }

    public ResizableJFrameDemo() {
        super("Resizable frame demo");

        MouseInputListener resizeHook = new MouseInputAdapter() {

            private Point startPos = null;

            public void mousePressed(MouseEvent e) {
                if (gp.contains(e.getPoint())) startPos = new Point(getWidth() - e.getX(), getHeight() - e.getY());
            }

            @Override
            public void mouseReleased(@SuppressWarnings("unused") MouseEvent mouseEvent) {
                startPos = null;
            }

            public void mouseMoved(MouseEvent e) {
                if (gp.contains(e.getPoint())) setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
                else
                    setCursor(Cursor.getDefaultCursor());
            }

            public void mouseDragged(MouseEvent e) {
                if (startPos != null) {

                    int dx = e.getX() + startPos.x;
                    int dy = e.getY() + startPos.y;

                    setSize(dx, dy);
                    repaint();
                }
            }
        };

        addMouseMotionListener(resizeHook);
        addMouseListener(resizeHook);
        ((JComponent)getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 20));
        //add(fc);

        setResizable(false);

        setMinimumSize(new Dimension(width, height));
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public static void main(String args[]) {
        System.out.println("Starting demo...");
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                new ResizableJFrameDemo().setVisible(true);
            }
        });
    }
}