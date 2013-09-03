package pl.eurekin.experimental;

/**
 * @author greg.matoga@gmail.com
 */
public class UnsafePropertyListener<T> implements ChangedPropertyListener<T> {
    private final SafePropertyListener.ChangeListener listener;

    public UnsafePropertyListener(SafePropertyListener.ChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public void beginNotifying() {
    }

    @Override
    public void propertyChanged(T oldValue, T newValue) {

        listener.act();
    }

    @Override
    public void finishNotifying() {
    }
}
