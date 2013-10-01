package experimental.incubator;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public class ComboBoxJListExperiment {

    JPanel viewMain = new JPanel();

    JPanel viewCenter = new JPanel();

    JComboBox combo = new JComboBox();

    JComboBox comboCopy = new JComboBox();

    JList list = new JList();

    JButton buttonAddLast = new JButton("add last");

    JButton buttonAddFirst = new JButton("add first");

    JButton buttonDeleteCombo = new JButton("delete from: combo ");

    JButton buttonDeleteList = new JButton("delete from: list ");

    JTable table = new JTable();

    DefaultListModel listModel = new DefaultListModel();

    public ComboBoxJListExperiment() {
        putControlsOnMainView();
        bindButtonActions();
        bindModelToCombo();
        bindModelToList();
        bindButtonEnableWhenComboItemSelected();
        bindButtonEnableWhenListItemSelected();
        updateButtonDeleteComboState();
        listSelectionChangedTo(-1);
        tableBindModel();

        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        FormUtils.tryToFix(combo);
        FormUtils.tryToFix(comboCopy);
    }

    private void tableBindModel() {
        table.setModel(new ListTableModel(listModel,
                new ListTableModel.ReflectionColumn("toLowerCase", "toLowerCase", String.class),
                new ListTableModel.ReflectionColumn("hashCode", "hashCode", String.class),
                new ListTableModel.ReflectionColumn("isEmpty", "isEmpty", String.class)


                ));
    }

    private void bindButtonEnableWhenListItemSelected() {
        list.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                int selectedIndex = ((JList) e.getSource()).getSelectedIndex();
                listSelectionChangedTo(selectedIndex);
            }
        });
    }

    protected void listSelectionChangedTo(int selectedIndex) {
        buttonDeleteList.setEnabled(selectedIndex != -1);
    }

    private void bindButtonEnableWhenComboItemSelected() {
        combo.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                listModelSelectionChanged();
            }
        });
    }

    protected void listModelSelectionChanged() {
        updateButtonDeleteComboState();
    }

    private void updateButtonDeleteComboState() {
        buttonDeleteCombo.setEnabled(combo.getSelectedItem() != null);
    }

    private void bindModelToList() {
        list.setModel(listModel);
    }

    private void bindModelToCombo() {
        DefaultListModel localListModel = listModel;
        combo.setModel(comboBoxModelOf(localListModel));
        comboCopy.setModel(comboBoxModelOf(localListModel));
    }

    public ComboBoxModel comboBoxModelOf(final DefaultListModel localListModel) {
        return new DelegatingComboBoxModel(localListModel);
    }

    private void bindButtonActions() {
        buttonAddLast.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                buttonAddAsLastItemPressed();
            }
        });
        buttonAddFirst.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                buttonAddAsFirstItemPressed();
            }
        });
        buttonDeleteCombo.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                buttonDeleteComboItemPressed();
            }
        });
        buttonDeleteList.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                buttonDeleteListItemPressed();
            }
        });

    }

    protected void buttonAddAsFirstItemPressed() {
        appendAsFirstItem(getNextItem());
    }

    private void appendAsFirstItem(String nextItem) {
        listModel.insertElementAt(nextItem, 0);
    }

    protected void buttonDeleteListItemPressed() {
        listModel.removeElementAt(selectedIndexFromList());
    }

    private int selectedIndexFromList() {
        return list.getSelectedIndex();
    }

    protected void buttonDeleteComboItemPressed() {
        listModel.removeElementAt(selectedIndexFromCombo());
    }

    private int selectedIndexFromCombo() {
        return combo.getSelectedIndex();
    }

    protected void buttonAddAsLastItemPressed() {
        appendAsLastItem(getNextItem());
    }

    private void appendAsLastItem(String nextItem) {
        listModel.addElement(nextItem);
    }

    private int itemNamingCounter = 1;

    private String getNextItem() {
        return "Item (" + (itemNamingCounter++) + ")";
    }

    private void putControlsOnMainView() {

        viewMain.setLayout(new BorderLayout());
        viewCenter.setLayout(new BoxLayout(viewCenter, BoxLayout.X_AXIS));

        JPanel controlpanel = new JPanel(new FlowLayout());
        controlpanel.add(combo);
        controlpanel.add(comboCopy);
        controlpanel.add(buttonAddLast);
        controlpanel.add(buttonAddFirst);
        controlpanel.add(buttonDeleteCombo);
        controlpanel.add(buttonDeleteList);

        viewCenter.add(list);
        viewCenter.add(SwingViewsBuilder.configureAsWindowsTable(table));
        SwingViewsBuilder.setDemoModelOn(table);

        viewMain.add(controlpanel, BorderLayout.NORTH);
        viewMain.add(viewCenter, BorderLayout.CENTER);
    }



    public static void main(String[] args) {
        SwingViewsBuilder.setSystemLAF();
        SwingViewsBuilder.showInDemoFrame(new ComboBoxJListExperiment().viewMain);
    }
}
