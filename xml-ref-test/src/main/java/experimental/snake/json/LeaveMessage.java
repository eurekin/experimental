package experimental.snake.json;

import experimental.snake.SnakeController;
import org.eclipse.persistence.oxm.annotations.XmlDiscriminatorValue;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * @author greg.matoga@gmail.com
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlDiscriminatorValue("leave")
public class LeaveMessage extends AbstractMessage {
    int id;

    @Override
    public void updateController(SnakeController snakeController) {
        snakeController.snakeLeft(id);
    }
}
