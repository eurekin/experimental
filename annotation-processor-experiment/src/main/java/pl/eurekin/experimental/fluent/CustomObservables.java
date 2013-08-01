package pl.eurekin.experimental.fluent;

import pl.eurekin.experimental.ArrayContains;
import pl.eurekin.experimental.Constant;
import pl.eurekin.experimental.Observable;
import pl.eurekin.experimental.state.*;
import pl.eurekin.experimental.swing.JTextComponentBinder;

import javax.swing.*;
import java.util.List;

/**
 * @author greg.matoga@gmail.com
 */
public class CustomObservables {

    public static JTextComponentBinder bind(JTextField textField1) {
        return new JTextComponentBinder(textField1);
    }

    public static <T> Observable<Integer> sizeOf(Observable<List<T>> list) {
        return derive(list,
                CustomObservables.<T>getInterpreter());
    }

    public static <T> Interpreter<List<T>, Integer> getInterpreter() {
        return new Interpreter<List<T>, Integer>() {
            @Override
            public Integer interpret(List<T> input) {
                return input.size();
            }
        };
    }

    public static <T, E> Observable<T> derive(Observable<E> base, Interpreter<E, T> interpreter) {
        return new InterpretingObservable<E, T>(base, interpreter);
    }

    public static <T> ObservableState does(Observable<T[]> observableArray, Interpreter<T[], Boolean> interpreter) {
        return new ObservableStateInterpreterAdapter<T[]>(observableArray, interpreter);
    }

    public static <T> ArrayContains<T> contain(Observable<T> constant) {
        return new ArrayContains<T>(constant);
    }

    public static <T> Constant<T> constant(T constant) {
        return new Constant<T>(constant);
    }

    public static Observable<Integer> subtract(Observable<Integer> operandA, Observable<Integer> operandB) {
        return new DerivedObservable<Integer>(operandA, operandB) {
            @Override
            protected Integer value(Observable<Integer>... baseStates) {
                return baseStates[0].get() - baseStates[1].get();
            }
        };
    }
}
