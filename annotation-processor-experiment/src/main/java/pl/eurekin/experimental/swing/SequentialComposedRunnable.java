package pl.eurekin.experimental.swing;

/**
 * @author greg.matoga@gmail.com
 */
public class SequentialComposedRunnable implements Runnable {
    private final Runnable first;
    private final Runnable second;

    public SequentialComposedRunnable(Runnable first, Runnable second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public void run() {
        first.run();
        second.run();
    }
}
