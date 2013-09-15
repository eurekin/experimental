package experimental.snake.json;

import experimental.chess.Data;
import experimental.snake.SnakeController;
import org.eclipse.persistence.oxm.annotations.XmlDiscriminatorValue;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * @author greg.matoga@gmail.com
*/
@XmlRootElement
@XmlDiscriminatorValue("join")
@XmlAccessorType(XmlAccessType.FIELD)
public class JoinMessage extends AbstractMessage {

    private List<Data> data;

    @Override
    public String toString() {
        return "JoinMessage{" +
                "data=" + data +
                '}';
    }

    @Override
    public void updateController(SnakeController snakeController) {
        for(Data snakeData : data)
            snakeController.snakeJoined(snakeData.getId(), snakeData.getColor());
    }
}
