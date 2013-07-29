package pl.eurekin.experimental;

import pl.eurekin.experimental.state.Interpreter;
import pl.eurekin.experimental.state.ObservableInterpreterAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author greg.matoga@gmail.com
 */
public class SelectedObjectsAdapter<T>
        extends ObservableInterpreterAdapter<Integer[], List<T>>
        implements Observable<List<T>> {

    public SelectedObjectsAdapter(
            Observable<Integer[]> observableSelectionModel,
            ObservableList<T> backingList) {
        super(observableSelectionModel,
                new IntegerToObjectInterpreter<T>(backingList));
    }

    private static class IntegerToObjectInterpreter<T> implements Interpreter<Integer[], List<T>> {
        private final ObservableList<T> observableList;

        public IntegerToObjectInterpreter(ObservableList<T> observableList) {
            this.observableList = observableList;
        }

        @Override
        public List<T> interpret(Integer[] input) {
            List<T> objects = new ArrayList<T>(input.length);

            for (Integer i : input)
                objects.add(observableList.get(i));

            return objects;
        }
    }
}
