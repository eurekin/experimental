package pl.eurekin.experimental.viewmodel;

import pl.eurekin.experimental.Property;
import pl.eurekin.experimental.state.ObservableState;

/**
 * @author greg.matoga@gmail.com
 */
public interface ViewModel<T> {

    T base();

    Property<T> baseProperty();

    void set(T newBase);

    Property<?>[] allProperties();

    ObservableState baseNotNullState();
}
