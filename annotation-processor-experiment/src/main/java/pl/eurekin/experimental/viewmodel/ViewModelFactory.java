package pl.eurekin.experimental.viewmodel;

import pl.eurekin.experimental.Observable;

/**
 * @author greg.matoga@gmail.com
 */
public interface ViewModelFactory<T, E extends ViewModel<T>> {


    public E newValueModel(T base);

    public E newObservingValueModel(Observable<T> observableBase);

}
