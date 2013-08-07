package pl.eurekin.experimental.state;

import pl.eurekin.experimental.ChangedPropertyListener;
import pl.eurekin.experimental.Observable;

import java.lang.reflect.Array;
import java.util.List;

public abstract class DerivedObservable<T> extends StatelessObservable<T> {
    private final Observable<T>[] observables;
    private T lastValue;


    public DerivedObservable(Observable<T>... observables) {
        this.observables = observables;
        ChangedPropertyListener<T> listener = new ChangedPropertyListener<T>() {
            @Override
            public void beginNotifying() {
                onBeginNotifying();
            }

            @Override
            public void propertyChanged(T oldValue, T newValue) {
                update(newValue);
            }

            @Override
            public void finishNotifying() {
                onFinishNotifying();
            }
        };
        for (Observable<T> state : observables)
            state.registerChangeListener(listener);
        initializeStartValue();
    }


    // as per http://stackoverflow.com/questions/16530161/how-can-i-return-a-list-of-a-certain-type-when-calling-a-generic-method-that-acc
    // it seems the suppression is inevitable
    @SuppressWarnings("unchecked")
    public DerivedObservable(List<Observable<Boolean>> observables) {
        this(observables.toArray(new Observable[observables.size()]));
    }

    public static <T> T[] newArray(Class<T[]> type, int size) {
        return type.cast(Array.newInstance(type.getComponentType(), size));
    }

    protected abstract T value(Observable<T>... baseStates);

    void initializeStartValue() {
        lastValue = get();
    }

    @Override
    public T get() {
        return value(observables);
    }

    protected void update(T newValueFromSingleSource) {
        T oldValue = lastValue;
        T newValue = get();
        lastValue = newValue;
        notifyOfStateChange(oldValue, newValue);
    }
}
