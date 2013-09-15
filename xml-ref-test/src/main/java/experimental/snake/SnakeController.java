package experimental.snake;

import experimental.snake.json.AbstractMessage;
import experimental.snake.json.Body;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author greg.matoga@gmail.com
 */
public class SnakeController {

    private HashMap<Integer, String> snakeColors = new HashMap<Integer, String>();
    private HashMap<Integer, List<Body>> snakeBodies = new HashMap<Integer, List<Body>>();
    private int score = 0;

    public void acceptMessage(AbstractMessage message) {
        message.updateController(this);
    }

    public void snakeLeft(int id) {
        snakeColors.remove(id);
        snakeBodies.remove(id);
    }

    public void snakeJoined(int id, String color) {
        snakeColors.put(id, color);
    }

    public void updateSnake(int id, List<Body> body) {
        snakeBodies.put(id, body);
    }

    public void paint(Paintable paintable) {
        paintable.clear();
        for (Map.Entry<Integer, List<Body>> snake : snakeBodies.entrySet()) {
            Color color = parseColorString(snakeColors.get(snake.getKey()));
            for (Body body : snake.getValue()) {
                paintable.paint(body.asPoint(), color);
            }
        }
        paintable.finished();
    }

    private Color parseColorString(String color) {
        return new Color(
                Integer.parseInt(color.substring(1, 3), 16),
                Integer.parseInt(color.substring(3, 5), 16),
                Integer.parseInt(color.substring(5, 7), 16)
        );
    }

    public void end() {
        score = 0;
    }

    public void killed() {
        score++;
    }
}
