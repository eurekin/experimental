package experimental.snake;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

/**
 * @author greg.matoga@gmail.com
 */
public class SnakeDemo {

    private PaintableSwingPanel paintableSwingPanel;
    private SnakeWebSocketClient snakeWebSocketClient;
    private JPanelKeyboardController keyboardController;

    public static void main(String[] args) throws Exception {
        SnakeDemo snakeDemoFrame = new SnakeDemo();
        snakeDemoFrame.initialize();
        snakeDemoFrame.showDemoFrame();
        snakeDemoFrame.run();
    }

    public JComponent getView() {
        return paintableSwingPanel;
    }

    public void initialize() {
        try {
            runOnEDT(createUIAsRunnable());
            snakeWebSocketClient = new SnakeWebSocketClient(paintableSwingPanel);
            keyboardController = new JPanelKeyboardController();
            keyboardController.setDirectionListener(snakeWebSocketClient);
            runOnEDT(registerKeyStrokesAsRunnable());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Runnable registerKeyStrokesAsRunnable() {
        return new Runnable() {
            public void run() {
                keyboardController.registerKeyStrokes(paintableSwingPanel);
            }
        };
    }

    private Runnable createUIAsRunnable() {
        return new Runnable() {
            public void run() {
                createUI();
            }
        };
    }

    private void runOnEDT(Runnable runnable) throws InterruptedException, InvocationTargetException {
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        } else {
            SwingUtilities.invokeAndWait(runnable);
        }
    }

    public void run() {
        snakeWebSocketClient.connect();
    }

    private void createUI() {
        paintableSwingPanel = new PaintableSwingPanel();
    }

    private void showDemoFrame() {
        JFrame f = new JFrame("Snake Demo");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(getView());
        f.pack();
        f.setVisible(true);
    }
}