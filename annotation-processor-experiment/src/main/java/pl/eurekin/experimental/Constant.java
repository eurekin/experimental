package pl.eurekin.experimental;

/**
* @author greg.matoga@gmail.com
*/
public class Constant<T> implements Observable<T> {

    private T constantValue;

    public Constant(T constantValue) {
        this.constantValue = constantValue;
    }

    @Override
    public void registerChangeListener(ChangedPropertyListener<T> listener) {
    }

    @Override
    public void unregisterChangeListener(ChangedPropertyListener<T> listener) {
    }

    @Override
    public T get() {
        return constantValue;
    }
}
