import org.junit.Before;
import org.junit.Test;
import pl.eurekin.experimental.ChangedPropertyListener;
import pl.eurekin.experimental.Observable;
import pl.eurekin.experimental.state.DerivedState;
import pl.eurekin.experimental.state.ObservableState;
import pl.eurekin.experimental.state.SimpleState;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItems;

/**
 * @author gmatoga
 */
public class PropertyTest {

    private ArrayList<String> called;

    @Before
    public void setUp() {
        called = new ArrayList<String>();
    }

    @Test
    public void delegatingTest() {
        final boolean initialState = false;
        SimpleState simpleState = new SimpleState(initialState);

        DerivedState derived = new SameState(simpleState);

        registerTestListenerOn(derived);

        final boolean newState = !initialState;
        simpleState.set(newState);

        assertThat(derived.get(), is(newState));
        assertThat(called, hasItems("changed"));
    }

    @Test
    public void intiialTest() {
        final boolean initialState = false;
        SimpleState simpleState = new SimpleState(initialState);

        registerTestListenerOn(simpleState);

        final boolean newState = !initialState;
        simpleState.set(newState);

        final boolean expectedNewState = newState;
        assertThat(simpleState.get(), is(expectedNewState));
        assertThat(called, hasItems("changed"));
    }

    private void registerTestListenerOn(ObservableState state) {
        state.registerChangeListener(new ChangedPropertyListener<Boolean>() {
            @Override
            public void beginNotifying() {
                called.add("begin");
            }

            @Override
            public void propertyChanged(Boolean oldValue, Boolean newValue) {
                called.add("changed");
            }

            @Override
            public void finishNotifying() {
                called.add("finish");
            }
        });
    }

    private static class SameState extends DerivedState {

        public SameState(SimpleState simpleState) {
            super(simpleState);
        }

        @Override
        protected Boolean value(Observable<Boolean>... baseStates) {
            return baseStates[0].get();
        }
    }
}
