package pl.eurekin.experimental;

import java.util.List;

public interface ObservableList<T>
        extends List<T>, Observable<List<T>>{
}
