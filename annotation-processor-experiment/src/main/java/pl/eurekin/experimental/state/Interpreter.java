package pl.eurekin.experimental.state;

public interface Interpreter<I, O> {
    O interpret(I input);
}
