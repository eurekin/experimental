package pl.eurekin.experimental;

/**
 * @author Rekin
 */
public interface TemplateSetter<T, B> {

    public void set(B base, T value);
}
