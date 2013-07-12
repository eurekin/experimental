package pl.eurekin.experimental;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class JavaBeanTableModel<T> extends AbstractTableModel {

    private List<T> backingList;
    private List<JavaBeanColumnModel> columnModel;

    public JavaBeanTableModel(ObservableList<T> test) {
        columnModel = new ArrayList<>();
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
    public Object getValueAt(int rowIndex, int columnIndex) {
        return columnModel.get(columnIndex).getFrom(backingList.get(rowIndex));
    }

    public void addColumn(PropertyAccessor<?, T> length_property, String name) {
        columnModel.add(new JavaBeanColumnModel<T>(length_property, name));
    }

    private class JavaBeanColumnModel<T> {


        private final PropertyAccessor<?, T> propertyAccessor;
        private final String name;

        public JavaBeanColumnModel(PropertyAccessor<?, T> propertyAccessor, String name) {
            this.propertyAccessor = propertyAccessor;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public Object getFrom(T o) {
            return propertyAccessor.get(o);
        }
    }
}
