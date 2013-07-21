package pl.eurekin.experimental.swing;

import pl.eurekin.experimental.state.Interpreter;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author greg.matoga@gmail.com
 */
public class LoggingInterpreter<T> implements Interpreter<T, T> {
    private static final Logger LOGGER = Logger.getLogger(LoggingInterpreter.class.getName());

    @Override
    public T interpret(T input) {
        LOGGER.log(Level.FINE, String.format("Interpreting input: [%s]", Objects.toString(input)));
        return input;
    }
}
