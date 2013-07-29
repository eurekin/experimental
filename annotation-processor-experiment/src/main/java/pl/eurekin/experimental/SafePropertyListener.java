package pl.eurekin.experimental;

/**
 * @author greg.matoga@gmail.com
 */
public class SafePropertyListener<T> implements ChangedPropertyListener<T> {
    private final ChangeListener listener;
    private int counter = 0;

    public SafePropertyListener(ChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public void beginNotifying() {
        onBeginNotifying();
    }

    @Override
    public void propertyChanged(T oldValue, T newValue) {
        // silently ignore
        //
        // the data provided here is susceptible to data races
    }

    @Override
    public void finishNotifying() {
        onFinishNotifying();
    }

    private void onBeginNotifying() {
        counter++;
    }

    private void onFinishNotifying() {
        counter--;
        actIfAllEventsAreFired();
    }

    private void actIfAllEventsAreFired() {
        if (counter == 0)
            listener.act();
    }

    public interface ChangeListener {
        void act();
    }
}
