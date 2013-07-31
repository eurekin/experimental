package pl.eurekin.experimental.state;

import pl.eurekin.experimental.ChangedPropertyListener;
import pl.eurekin.experimental.Observable;

public abstract class DerivedObservable<T> extends StatelessObservable<T> {
    private final Observable<T>[] observables;
    private T lastValue;


    public DerivedObservable(Observable<T> ... observables) {
        this.observables = observables;
        ChangedPropertyListener<T> listener = new ChangedPropertyListener<T>() {
            @Override
            public void beginNotifying() {
                onBeginNotifying();
            }

            @Override
            public void propertyChanged(T oldValue, T newValue) {
                update();
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

    protected abstract T value(Observable<T>... baseStates);

    void initializeStartValue() {
        lastValue = get();
    }

    @Override
    public T get() {
        return value(observables);
    }

    void update() {
        T oldValue = lastValue;
        T newValue = get();
        lastValue = newValue;
        notifyOfStateChange(oldValue, newValue);
    }
}
