package pl.eurekin.experimental.state;

public class StatefulObservableState
        extends StatefulObservable<Boolean>
        implements ObservableState {


    public StatefulObservableState(Boolean initialValue) {
        super(initialValue);
    }
}
