package pl.eurekin.experimental.swing.selection;

import pl.eurekin.experimental.Observable;

import java.lang.ref.WeakReference;
import java.util.List;

/**
* @author greg.matoga@gmail.com
*/
public class SelectionStore<T> implements Runnable {
    private final Observable<List<T>> selectedObjectsAdapter;
    private List<WeakReference<T>> selectedFields;

    public SelectionStore(Observable<List<T>> selectedObjectsAdapter,
                          List<WeakReference<T>> selectedFields) {
        this.selectedObjectsAdapter = selectedObjectsAdapter;
        this.selectedFields = selectedFields;
    }

    @Override
    public void run() {
        selectedFields.clear();
        for(T field : selectedObjectsAdapter.get()) {
            selectedFields.add(new WeakReference<T>(field));
        }
    }
}
