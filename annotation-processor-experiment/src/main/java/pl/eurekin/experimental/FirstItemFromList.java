package pl.eurekin.experimental;

import pl.eurekin.experimental.state.Interpreter;

import java.util.List;

/**
* @author greg.matoga@gmail.com
*/
public class FirstItemFromList<T> implements Interpreter<List<T>, T> {

    @Override
    public T interpret(List<T> input) {
        return input.size() == 1 ? input.get(0) : null;
    }
}
