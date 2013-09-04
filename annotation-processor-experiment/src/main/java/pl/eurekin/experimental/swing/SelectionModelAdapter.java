package pl.eurekin.experimental.swing;

import pl.eurekin.experimental.ChangedPropertyListener;
import pl.eurekin.experimental.Observable;
import pl.eurekin.experimental.StatefulPropertyChangeSupport;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.util.ArrayList;

public class SelectionModelAdapter implements Observable<Integer[]> {


    private final JTable table;
    private final StatefulPropertyChangeSupport<Integer[]> changeSupport;

    public SelectionModelAdapter(JTable table) {
        this.table = table;
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                update();
            }
        });
        changeSupport = new StatefulPropertyChangeSupport<Integer[]>(selection());
    }

    public void update() {
        changeSupport.onBeginNotifying();
        changeSupport.firePropertyChangeEvent(selection());
        changeSupport.onFinishNotifying();
    }

    private Integer[] selection() {
        int[] selectedViewRows = table.getSelectedRows();
        ArrayList<Integer> modelRows = new ArrayList<Integer>(selectedViewRows.length);
        for (Integer viewRow : selectedViewRows)
            modelRows.add(table.convertRowIndexToModel(viewRow));
        return modelRows.toArray(new Integer[modelRows.size()]);
    }

    @Override
    public void registerChangeListener(ChangedPropertyListener<Integer[]> listener) {
        changeSupport.registerNewListener(listener);
    }

    @Override
    public void unregisterChangeListener(ChangedPropertyListener<Integer[]> listener) {
        changeSupport.unregisterListener(listener);
    }

    @Override
    public Integer[] get() {
        return selection();
    }
}
