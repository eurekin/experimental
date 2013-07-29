package pl.eurekin.experimental;

import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class JavaBeanTableModel<T> extends AbstractTableModel {

    private final List<T> backingList;
    private final List<JavaBeanColumnModel<?, T>> columnModel;

    public JavaBeanTableModel(ObservableList<T> test) {
        columnModel = new ArrayList<JavaBeanColumnModel<?, T>>();
        backingList = test;
        test.registerChangeListener(new ChangedPropertyListener<List<T>>() {
            @Override
            public void beginNotifying() {
                // todo
            }

            @Override
            public void propertyChanged(List<T> oldValue, List<T> newValue) {
                changed();
            }

            @Override
            public void finishNotifying() {
                // todo
            }
        });
    }

    private void changed() {

        super.fireTableDataChanged();
    }

    @Override
    public String getColumnName(int column) {
        return columnModel.get(column).getName();
    }

    @Override
    public int getRowCount() {
        return backingList.size();
    }

    @Override
    public int getColumnCount() {
        return columnModel.size();
    }

    @Override
    public void fireTableDataChanged() {
        fireTableChanged(new TableModelEvent(this, //tableModel
                0, //firstRow
                getRowCount() - 1, //lastRow
                TableModelEvent.ALL_COLUMNS, //column
                TableModelEvent.UPDATE)); //changeType
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return columnModel.get(columnIndex).getFrom(backingList.get(rowIndex));
    }

    public <E> void addColumn(PropertyAccessor<E, T> length_property, String name) {
        columnModel.add(new JavaBeanColumnModel<E, T>(length_property, name));
    }

    private class JavaBeanColumnModel<E, T> {


        private final PropertyAccessor<E, T> propertyAccessor;
        private final String name;

        public JavaBeanColumnModel(PropertyAccessor<E, T> propertyAccessor, String name) {
            this.propertyAccessor = propertyAccessor;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public E getFrom(T o) {
            return propertyAccessor.get(o);
        }
    }
}
