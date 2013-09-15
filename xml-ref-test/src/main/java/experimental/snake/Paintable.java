package experimental.snake;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * @author greg.matoga@gmail.com
 */
public interface Paintable {
    void clear();

    void paint(Point point, Color color);

    void finished();
}
