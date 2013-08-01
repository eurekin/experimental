package pl.eurekin.experimental.state;

import pl.eurekin.experimental.ChangedPropertyListener;
import pl.eurekin.experimental.Observable;

public class InterpretingObservable<T, E> extends StatelessObservable<E> {
    private final Observable<T> observable;
    private final Interpreter<T, E> interpreter;
    private T lastValue;


    public InterpretingObservable(Observable<T> observable, Interpreter<T, E> interpreter) {
        this.observable = observable;
        this.interpreter = interpreter;
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
        observable.registerChangeListener(listener);
        initializeStartValue();
    }

    E value(T base) {
        return interpreter.interpret(base);
    }

    void initializeStartValue() {
        lastValue = observable.get();
    }

    @Override
    public E get() {
        return value(observable.get());
    }

    void update() {
        T oldValue = lastValue;
        T newValue = observable.get();
        lastValue = newValue;
        notifyOfStateChange(value(oldValue), value(newValue));
    }
}
