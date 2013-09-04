package pl.eurekin.experimental.state;

import pl.eurekin.experimental.ChangedPropertyListener;
import pl.eurekin.experimental.Observable;
import pl.eurekin.experimental.StatefulPropertyChangeSupport;
import pl.eurekin.experimental.StatelessPropertyChangeSupport;

public class ObservableInterpreterAdapter<I, O>
        implements Observable<O> {

    private final Observable<I> observableInput;
    private final Interpreter<I, O> interpreter;
    private final StatefulPropertyChangeSupport<O> changeSupport;

    public ObservableInterpreterAdapter(Observable<I> observableInput, Interpreter<I, O> interpreter) {
        this.observableInput = observableInput;
        this.interpreter = interpreter;
        registerOnInput();
        changeSupport = new StatefulPropertyChangeSupport<O>(interpreter.interpret(observableInput.get()));
    }

    private void registerOnInput() {
        observableInput.registerChangeListener(new ChangedPropertyListener<I>() {
            @Override
            public void beginNotifying() {
                onBeginNotifying();
            }

            @Override
            public void propertyChanged(I oldValue, I newValue) {
                update(oldValue, newValue);
            }

            @Override
            public void finishNotifying() {
                onFinishNotifying();
            }
        });
    }

    private void onFinishNotifying() {
        changeSupport.onFinishNotifying();
    }

    private void onBeginNotifying() {
        changeSupport.onBeginNotifying();
    }

    private void update(I oldValue, I newValue) {
        O oldInterpretedValue = interpreter.interpret(newValue); // TODO dangerous in case of mutation
        // if the object from table is being removed, it's not going to evaluate
        // to any sensible value during update! Should've used statefulPropertyChange
        // support instead!

        O newInterpretedValue = interpreter.interpret(newValue);
        updateInterpreted(oldInterpretedValue, newInterpretedValue);
    }

    protected void updateInterpreted(O oldValue, O newValue) {
        changeSupport.firePropertyChangeEvent(newValue);
    }

    @Override
    public void registerChangeListener(ChangedPropertyListener<O> listener) {
        changeSupport.registerNewListener(listener);
    }

    @Override
    public void unregisterChangeListener(ChangedPropertyListener<O> listener) {
        changeSupport.unregisterListener(listener);
    }

    @Override
    public O get() {
        return interpreter.interpret(observableInput.get());
    }
}
