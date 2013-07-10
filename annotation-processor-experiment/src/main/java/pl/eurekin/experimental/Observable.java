package pl.eurekin.experimental;

public interface Observable<T> {
    void registerChangeListener(ChangedPropertyListener<T> listener);

    void unregisterChangeListener(ChangedPropertyListener<T> listener);
}
