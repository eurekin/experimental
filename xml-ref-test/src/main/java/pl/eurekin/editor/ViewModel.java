package pl.eurekin.editor;

import pl.eurekin.experimental.Property;

/**
 * @author greg.matoga@gmail.com
 */
public interface ViewModel<T> {

    T base();

    void set(T newBase);

    Property<?>[] allProperties();
}
