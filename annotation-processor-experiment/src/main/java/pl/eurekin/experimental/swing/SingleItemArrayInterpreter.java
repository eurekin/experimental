package pl.eurekin.experimental.swing;

import pl.eurekin.experimental.state.Interpreter;

public class SingleItemArrayInterpreter implements Interpreter<Integer[], Boolean> {

    @Override
    public Boolean interpret(Integer[] input) {
        return input != null && input.length == 1;
    }
}
