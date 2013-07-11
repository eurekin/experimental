package pl.eurekin.experimental.state;

public class FilteringObservable extends StateProxy {

    private Boolean lastNewValue;
    private Boolean lastOldValue;

    public FilteringObservable(ObservableState base, GlobalEventCounter counter) {
        super(base, counter);
        counter.addListener(new Callback() {
            @Override
            public void stableStateReached() {
                onStableStateReached();
            }
        });
    }

    private void onStableStateReached() {
        changeSupport.firePropertyChangeEvent(lastOldValue, lastNewValue);
    }

    @Override
    protected void update(Boolean oldValue, Boolean newValue) {
        lastOldValue = oldValue;
        lastNewValue = newValue;
    }
}
