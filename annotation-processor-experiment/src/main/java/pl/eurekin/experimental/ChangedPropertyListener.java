package pl.eurekin.experimental;

public interface ChangedPropertyListener<T> {
    void beginNotifying();
    void propertyChanged(T oldValue, T newValue);
    void finishNotifying();
}
