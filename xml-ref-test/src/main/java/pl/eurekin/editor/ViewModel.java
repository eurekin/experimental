package pl.eurekin.editor;

import pl.eurekin.experimental.Observable;

/**
 * @author greg.matoga@gmail.com
 */
public interface ViewModel<T> {


    Observable[] allProperties();
}
