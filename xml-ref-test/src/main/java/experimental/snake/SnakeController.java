package experimental.snake;

import experimental.snake.json.AbstractMessage;
import experimental.snake.json.Body;

import java.util.HashMap;
import java.util.List;

/**
 * @author greg.matoga@gmail.com
 */
public class SnakeController {

    private HashMap<Integer, Object> snakeIds = new HashMap<Integer, Object>();
    private HashMap<Integer, Object> snakeBodies = new HashMap<Integer, Object>();

    public void acceptMessage(AbstractMessage message) {
        message.updateController(this);
    }

    public void snakeLeft(int id) {
        snakeIds.remove(id);
    }

    public void snakeJoined(int id, String color) {
        snakeIds.put(id, color);
    }

    public void updateSnake(int id, List<Body> body) {
        snakeBodies.put(id, body);
        System.out.println("toString() = " + toString());
    }

    @Override
    public String toString() {
        return "SnakeController{" +
                "snakeIds=" + snakeIds +
                ", snakeBodies=" + snakeBodies +
                "} ";
    }
}
