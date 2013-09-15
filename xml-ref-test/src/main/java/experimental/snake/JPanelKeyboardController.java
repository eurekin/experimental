package experimental.snake;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
* @author greg.matoga@gmail.com
*/
class JPanelKeyboardController {
    private DirectionListener directionListener;
    public void registerKeyStrokes(JComponent component) {
        InputMap inputmap = component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "up");
        inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "down");
        inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "left");
        inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "right");

        ActionMap actionmap = component.getActionMap();
        actionmap.put("up", new KeyboardAction("up"));
        actionmap.put("down", new KeyboardAction("down"));
        actionmap.put("left", new KeyboardAction("left"));
        actionmap.put("right", new KeyboardAction("right"));
    }

    private class KeyboardAction extends AbstractAction {
        private final String code;

        public KeyboardAction(String code) {
            this.code = code;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if(directionListener!=null)
                directionListener.direction(code);
        }
    }

    public void setDirectionListener(DirectionListener directionListener) {
        this.directionListener = directionListener;
    }

    public DirectionListener getDirectionListener() {
        return directionListener;
    }
}
