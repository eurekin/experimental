package experimental.snake.json;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.awt.*;

/**
 * @author greg.matoga@gmail.com
 */

@XmlAccessorType(XmlAccessType.FIELD)
public class Body {
    int x;
    int y;

    public Point asPoint() {
        // server already scales to the block size of 10
        return new Point(x / 10, y / 10);
    }

    @Override
    public String toString() {
        return "Body{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
