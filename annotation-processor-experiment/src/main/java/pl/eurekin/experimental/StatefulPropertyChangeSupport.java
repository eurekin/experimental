package pl.eurekin.experimental;

import java.util.HashSet;
import java.util.Set;

public class StatefulPropertyChangeSupport<T> {
    private T value;
    Set<ChangedPropertyListener<T>> listeners = new HashSet<>();

    public StatefulPropertyChangeSupport(T initialValue) {
        this.value = initialValue;
    }

    public T getValue() {
        return value;
    }

    public void registerNewListener(ChangedPropertyListener<T> propertyListener) {
        listeners.add(propertyListener);
    }

    public void unregisterListener(ChangedPropertyListener<T> propertyListener) {
        listeners.remove(propertyListener);
    }

    public void firePropertyChangeEvent(T newValue) {
        T oldValue = value;
        value = newValue;
        for (ChangedPropertyListener<T> listener : listeners)
            listener.propertyChanged(oldValue, newValue);
    }

    public void onBeginNotifying() {
        for (ChangedPropertyListener<T> listener : listeners)
            listener.beginNotifying();
    }

    public void onFinishNotifying() {
        for (ChangedPropertyListener<T> listener : listeners)
            listener.finishNotifying();
    }
}
