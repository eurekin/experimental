package experimental.snake.json;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.util.List;

/**
 * @author greg.matoga@gmail.com
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Snake {
    int id;
    List<Body> body;

    @Override
    public String toString() {
        return "Snake{" +
                "body=" + body +
                ", id=" + id +
                '}';
    }
}
