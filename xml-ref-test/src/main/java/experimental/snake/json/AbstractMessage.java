package experimental.snake.json;

import experimental.snake.SnakeController;
import org.eclipse.persistence.oxm.annotations.XmlDiscriminatorNode;

import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * @author greg.matoga@gmail.com
 */
@XmlDiscriminatorNode("@type")
@XmlSeeAlso({
        UpdateMessage.class,
        JoinMessage.class,
        LeaveMessage.class,
        DeadMessage.class,
        KillMessage.class
})
public abstract class AbstractMessage {
    public abstract void updateController(SnakeController snakeController);
}
