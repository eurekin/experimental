package pl.eurekin.experimental.swing;

import pl.eurekin.experimental.state.ObservableInterpreterAdapter;
import pl.eurekin.experimental.state.StateObservableAdapter;

import javax.swing.*;

public class SingleTableItemSelectedState
        extends StateObservableAdapter {

    public SingleTableItemSelectedState(SelectionModelAdapter selectionModelAdapter) {
        super(new ObservableInterpreterAdapter<>(
                selectionModelAdapter,
                new SingleItemArrayInterpreter()),
                false);
    }

}
