package pl.eurekin.experimental;

/**
 * @author Rekin
 */
public interface TemplateGetter<T, B> {
    public T get(B base);
}
