package pl.eurekin.experimental.swing;

import pl.eurekin.experimental.state.ObservableInterpreterAdapter;
import pl.eurekin.experimental.state.StateObservableAdapter;

import javax.swing.*;

public class SingleTableItemSelectedState
        extends StateObservableAdapter {

    public SingleTableItemSelectedState(JTable table) {
        super(new ObservableInterpreterAdapter<>(
                new SelectionModelAdapter(table),
                new SingleItemArrayInterpreter()),
                false);
    }

}
