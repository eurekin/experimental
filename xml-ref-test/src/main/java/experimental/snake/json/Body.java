package experimental.snake.json;

import org.eclipse.persistence.internal.oxm.XMLAccessor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * @author greg.matoga@gmail.com
 */

@XmlAccessorType(XmlAccessType.FIELD)
public class Body {
    private int x;
    private int y;

    @Override
    public String toString() {
        return "Body{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
