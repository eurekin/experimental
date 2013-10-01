package experimental.incubator;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 *  A <code>ListModel</code> decorator, to allow it to be used within <code>JComboBox</code>.
 *
 *  Designed around the shared model concept. Provides the necessary
 *  selected item support - what the <code>JComboBox</code> expects from it's model.
 *
 *  A single instance of this class shouldn't be used as a model of
 *  two <code>JComboBox</code>es, unless the selection is to be synchronized between
 *  them (normally, a bug).
 *
 * @author gmatoga
 *
 */
public final class DelegatingComboBoxModel
    extends AbstractListModel // used only to capture JComboBox as listener, so that
    // it can be notified, when the selected item was removed from the delegate list.
    implements ComboBoxModel {

    private static final long serialVersionUID = 1L;

    private final ListModel localListModel;

    private Object selectedItem;

    public DelegatingComboBoxModel(ListModel localListModel) {
        this.localListModel = localListModel;
        localListModel.addListDataListener(new DelegateListeningSupport());
    }


    public int getSize() {
        return localListModel.getSize();
    }

    public Object getElementAt(int index) {
        return localListModel.getElementAt(index);
    }

    @Override
    public void addListDataListener(ListDataListener l) {
        super.addListDataListener(l); // grab the JComboBox to address it later
        localListModel.addListDataListener(l);
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        super.removeListDataListener(l);
        localListModel.removeListDataListener(l);
    }

    @Override
    public void setSelectedItem(Object anItem) {
        this.selectedItem = anItem;
        fireContentsChanged(this, -1, -1); // same what DefaultComboBoxModel does in this situation
    }

    @Override
    public Object getSelectedItem() {
        return selectedItem;
    }

    private void updateSelected() {
        if(!delegateStillContains(selectedItem))
            setSelectedItem(null);
    }

    private boolean delegateStillContains(Object anItem) {
        for (int i = 0; i < localListModel.getSize(); i++)
            if (localListModel.getElementAt(i).equals(anItem))
                return true;

        return false;
    }

    private final class DelegateListeningSupport implements ListDataListener {

        @Override
        public void intervalRemoved(ListDataEvent e) {
            updateSelected();
        }

        @Override
        public void intervalAdded(ListDataEvent e) {
            updateSelected();
        }

        @Override
        public void contentsChanged(ListDataEvent e) {
            updateSelected();
        }
    }
}