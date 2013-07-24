package pl.eurekin.experimental;

public class Property<T> implements Observable<T> {

    private final Getter<T> getter;
    private final Setter<T> setter;

    public Property(Getter<T> getter, Setter<T> setter) {
        this.getter = getter;
        this.setter = setter;
        backingModel = new PropertyModel();
    }

    private PropertyModel backingModel;

    public T get() {
        return backingModel.getValue();
    }

    public void set(T newValue) {
        backingModel.setValue(get(), newValue);
    }

    @Override
    public void registerChangeListener(ChangedPropertyListener<T> listener) {
        propertyChangeSupport.registerNewListener(listener);
    }

    @Override
    public void unregisterChangeListener(ChangedPropertyListener<T> listener) {
        propertyChangeSupport.unregisterListener(listener);
    }

    StatelessPropertyChangeSupport<T> propertyChangeSupport = new StatelessPropertyChangeSupport<T>();

    private class PropertyModel {
        T value;

        public T getValue() {
            return getter.get();
        }

        public void setValue(T oldValue, T newValue) {
            setter.set(newValue);
            propertyChangeSupport.beginNotifying();
            propertyChangeSupport.firePropertyChangeEvent(oldValue, newValue);
            propertyChangeSupport.finishNotifying();
        }
    }
}
