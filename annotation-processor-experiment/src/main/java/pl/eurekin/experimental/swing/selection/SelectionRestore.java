package pl.eurekin.experimental.swing.selection;

import javax.swing.*;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
* @author greg.matoga@gmail.com
*/
public class SelectionRestore<T> implements Runnable {

    private List<T> list;
    private ListSelectionModel selectionModel;
    private List<WeakReference<T>> selectedFields;

    public SelectionRestore(List<T> list,
                            ListSelectionModel selectionModel,
                            List<WeakReference<T>> selectedFields) {
        this.list = list;
        this.selectionModel = selectionModel;
        this.selectedFields = selectedFields;
    }

    @Override
    public void run() {
        List<Integer> ids = new ArrayList<Integer>();
        for(WeakReference<T> field : selectedFields) {
            T fieldOrNull = field.get();
            if(fieldOrNull !=null)
                ids.add(list.indexOf(fieldOrNull));
        }
        selectionModel.clearSelection();
        for(Integer id : ids)
            selectionModel.addSelectionInterval(id, id);
    }
}
