package pl.eurekin.experimental.state;

import java.util.ArrayList;
import java.util.List;

public class GlobalEventCounter {
    private final List<Callback> callbacks;
    int count = 0;

    public GlobalEventCounter() {
        callbacks = new ArrayList<>();
    }

    public void addListener(Callback listener) {
        callbacks.add(listener);
    }

    public void increment() {
        count++;
        System.out.println("count=" + count);
    }

    public void decrement() {
        count--;
        System.out.println("count=" + count);
        if (count == 0)
            notifyListeners();
    }

    private void notifyListeners() {
        for (Callback listener : callbacks)
            listener.stableStateReached();
    }
}
