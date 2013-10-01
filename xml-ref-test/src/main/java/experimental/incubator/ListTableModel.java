package experimental.incubator;

import java.lang.reflect.Method;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.table.AbstractTableModel;

public final class ListTableModel extends AbstractTableModel {

    private static final class ListToTableEventConverter implements ListDataListener {

        final AbstractTableModel model;

        public ListToTableEventConverter(AbstractTableModel model) {
            this.model = model;
        }

        @Override
        public void intervalRemoved(ListDataEvent event) {
            model.fireTableRowsDeleted(event.getIndex0(), event.getIndex1());
        }

        @Override
        public void intervalAdded(ListDataEvent event) {
            model.fireTableRowsInserted(event.getIndex0(), event.getIndex1());
        }

        @Override
        public void contentsChanged(ListDataEvent event) {
            model.fireTableRowsUpdated(event.getIndex0(), event.getIndex1());
        }
    }

    public interface Column {
        public String headerText();
        public Object displayValueOf(Object object);
    }

    public static class ReflectionColumn implements Column {

        Method method;
        String headerText;

        public ReflectionColumn(String headerText, String methodName, Class<?> clazz) {
            this.headerText = headerText;
            try {
                method = clazz.getDeclaredMethod(methodName);
            } catch (Exception e) {
                throw new RuntimeException("Wrong column definition", e);
            }
        }

        @Override
        public String headerText() {
            return headerText;
        }

        @Override
        public Object displayValueOf(Object object) {
            try {
                return method.invoke(object);
            } catch (Exception e) {
                return "ERROR";
            }
        }

    }

    private static final long serialVersionUID = 1L;
    private final ListModel model;
    private Column[] columns = {};

    public ListTableModel(ListModel model, Column ... columns) {
        this.model = model;
        if(columns!=null)
        this.columns = columns;
        model.addListDataListener(new ListToTableEventConverter(this));
    }
    @Override
    public String getColumnName(int index) {
        return columns[index].headerText();
    }
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object listElement = model.getElementAt(rowIndex);
        Object columnValue = columns[columnIndex].displayValueOf(listElement);
        return columnValue;
    }

    @Override
    public int getRowCount() {
        return model.getSize();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }
}