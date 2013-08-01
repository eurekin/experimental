package pl.eurekin.experimental;

import pl.eurekin.experimental.state.Interpreter;

import java.util.Arrays;

/**
* @author greg.matoga@gmail.com
*/
public class ArrayContains<T> implements Interpreter<T[], Boolean> {

    private Observable<T> elementIndex;

    public ArrayContains(Observable<T> elementIndex) {
        this.elementIndex = elementIndex;
    }

    @Override
    public Boolean interpret(T[] selectedRows) {
        return Arrays.asList(selectedRows).contains(elementIndex.get());
    }
}
