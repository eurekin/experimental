package pl.eurekin.editor;

/**
 * @author greg.matoga@gmail.com
 */
public interface ViewModelFactory<T, E extends ViewModel<T>> {


    public E newValueModel(T base);

}
