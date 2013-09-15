package experimental.snake.json;

import experimental.snake.SnakeController;
import org.eclipse.persistence.oxm.annotations.XmlDiscriminatorValue;

/**
 * @author greg.matoga@gmail.com
 */
@XmlDiscriminatorValue("dead")
public class DeadMessage extends AbstractMessage {
    @Override
    public void updateController(SnakeController snakeController) {
        snakeController.end();
    }
}
