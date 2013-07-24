package pl.eurekin.experimental;

/**
* @author greg.matoga@gmail.com
*/
public class SimpleChangedPropertyListener<T> implements ChangedPropertyListener<T> {
    public int counter;
    public final AnythingHappenedListener listener;

    public SimpleChangedPropertyListener(AnythingHappenedListener anythingHappenedListener) {
        this.listener = anythingHappenedListener;
    }


    @Override
    public void beginNotifying() {
        counter++;
    }

    @Override
    public void propertyChanged(T oldValue, T newValue) {
    }

    @Override
    public void finishNotifying() {
        counter--;
        if (counter == 0)
            listener.onPropertyChanged();
    }
}
