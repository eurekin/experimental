package pl.eurekin.experimental;

public interface Observable<T> extends Getter<T> {
    void registerChangeListener(ChangedPropertyListener<T> listener);

    void unregisterChangeListener(ChangedPropertyListener<T> listener);

}
