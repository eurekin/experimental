package experimental.snake;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author greg.matoga@gmail.com
 */
public class PaintableSwingPanel
        extends JPanel
        implements Paintable {

    private final int blockSize;
    private final Object lock = new Object[0];
    private final Dimension dimension;
    private List<PaintOp> blocks;
    private List<PaintOp> paintOps;


    public PaintableSwingPanel() {
        this(10);
    }

    public PaintableSwingPanel(int blockSize) {

        this.blockSize = blockSize;
        int w = 64;
        int h = 48;
        dimension = new Dimension(this.blockSize * w, this.blockSize * h);
        synchronized (this.lock) {
            blocks = new ArrayList<PaintOp>(200);
            paintOps = new ArrayList<PaintOp>(200);
        }

        new JPanelKeyboardController().
                registerKeyStrokes(this);
    }

    @Override
    public Dimension getPreferredSize() {
        return dimension;
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
//        g.drawString("Use arrows to move!", 10, 20);
        for (PaintOp paintOp : getPaintOps())
            paintOp.drawOn(g);

    }

    @Override
    public void clear() {
        blocks.clear();
    }

    @Override
    public void paint(Point point, Color color) {
        blocks.add(new PaintOp(point, color));
    }

    @Override
    public void finished() {
        setPaintOps(new ArrayList<PaintOp>(blocks));
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                repaint(0);
            }
        });
    }

    public List<PaintOp> getPaintOps() {
        synchronized (this.lock) {
            return paintOps;
        }
    }

    public void setPaintOps(List<PaintOp> paintOps) {
        synchronized (this.lock) {
            this.paintOps = paintOps;
        }
    }

    private class PaintOp {
        private final Point point;
        private final Color color;

        public PaintOp(Point point, Color color) {
            this.point = point;
            this.color = color;
        }

        public void drawOn(Graphics g) {
            Integer baseX = blockSize * point.x;
            Integer baseY = blockSize * point.y;
            g.setColor(color);
            g.fillRect(baseX, baseY, blockSize, blockSize);
        }
    }

}