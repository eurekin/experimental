package experimental.snake.json;

import experimental.snake.SnakeController;
import org.eclipse.persistence.oxm.annotations.XmlDiscriminatorValue;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * @author greg.matoga@gmail.com
 */
@XmlRootElement
@XmlDiscriminatorValue("update")
@XmlAccessorType(XmlAccessType.FIELD)
public class UpdateMessage extends AbstractMessage {

    private List<Snake> data;

    @Override
    public String toString() {
        return "UpdateMessage{" +
                "data=" + data +
                '}';
    }

    @Override
    public void updateController(SnakeController snakeController) {
        for (Snake snake : data)
            snakeController.updateSnake(snake.id, snake.body);
    }
}
