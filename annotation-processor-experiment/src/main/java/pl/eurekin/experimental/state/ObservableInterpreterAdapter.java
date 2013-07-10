package pl.eurekin.experimental.state;

import pl.eurekin.experimental.ChangedPropertyListener;
import pl.eurekin.experimental.Observable;
import pl.eurekin.experimental.StatelessPropertyChangeSupport;

public class ObservableInterpreterAdapter<I, O> implements Observable<O> {

    private final Observable<I> observableInput;
    private final Interpreter<I, O> interpreter;
    private final StatelessPropertyChangeSupport<O> changeSupport = new StatelessPropertyChangeSupport<>();

    public ObservableInterpreterAdapter(Observable<I> observableInput, Interpreter<I, O> interpreter) {
        this.observableInput = observableInput;
        this.interpreter = interpreter;
        registerOnInput();
    }

    private void registerOnInput() {
        observableInput.registerChangeListener(new ChangedPropertyListener<I>() {
            @Override
            public void propertyChanged(I oldValue, I newValue) {
                update(oldValue, newValue);
            }
        });
    }

    private void update(I oldValue, I newValue) {
        O oldInterpretedValue = interpreter.interpret(oldValue);
        O newInterpretedValue = interpreter.interpret(newValue);
        changeSupport.firePropertyChangeEvent(oldInterpretedValue, newInterpretedValue);
    }

    @Override
    public void registerChangeListener(ChangedPropertyListener<O> listener) {
        changeSupport.registerNewListener(listener);
    }

    @Override
    public void unregisterChangeListener(ChangedPropertyListener<O> listener) {
        changeSupport.unregisterListener(listener);
    }
}
