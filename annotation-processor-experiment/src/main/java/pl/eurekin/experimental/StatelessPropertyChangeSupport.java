package pl.eurekin.experimental;

import java.util.HashSet;
import java.util.Set;

public class StatelessPropertyChangeSupport<T> {
    final Set<ChangedPropertyListener<T>> listeners = new HashSet<ChangedPropertyListener<T>>();

    public void registerNewListener(ChangedPropertyListener<T> propertyListener) {
        listeners.add(propertyListener);
    }

    public void unregisterListener(ChangedPropertyListener<T> propertyListener) {
        listeners.remove(propertyListener);
    }

    public void firePropertyChangeEvent(T oldValue, T newValue) {
        if(oldValue!=newValue)
        for (ChangedPropertyListener<T> listener : listeners)
            listener.propertyChanged(oldValue, newValue);
    }

    public void beginNotifying() {

        for (ChangedPropertyListener<T> listener : listeners)
            listener.beginNotifying();
    }

    public void finishNotifying() {
        for (ChangedPropertyListener<T> listener : listeners)
            listener.finishNotifying();
    }
}
