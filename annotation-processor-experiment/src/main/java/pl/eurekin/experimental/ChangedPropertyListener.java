package pl.eurekin.experimental;

public interface ChangedPropertyListener<T> {
    void propertyChanged(T oldValue, T newValue);
}
