package pl.eurekin.experimental.state;

import pl.eurekin.experimental.Observable;

public interface ObservableState extends Observable<Boolean> {

    public boolean value();
}
