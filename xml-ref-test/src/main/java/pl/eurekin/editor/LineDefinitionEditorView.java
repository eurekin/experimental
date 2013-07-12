package pl.eurekin.editor;

import pl.eurekin.experimental.*;
import pl.eurekin.experimental.Observable;
import pl.eurekin.experimental.state.*;
import pl.eurekin.experimental.swing.SelectionModelAdapter;
import pl.eurekin.experimental.swing.SingleTableItemSelectedState;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

import static pl.eurekin.experimental.ExpressionBuilder.not;
import static pl.eurekin.experimental.ExpressionBuilder.when;

/**
 * @author Rekin
 */
public class LineDefinitionEditorView {

    private JTable table1;
    private JPanel panel1;
    private JButton newButton;
    private JButton deleteButton;
    private JButton upButton;
    private JButton downButton;
    private ObservableListWrapper<Field> backingList;
    private ConstantLineWidthTextFileDefinition domainModelObject;


    public LineDefinitionEditorView() {
        newButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                domainModelObject.add(new Field());
                domainObjectChanged();
            }
        });
    }

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("LineDefinitionEditorView");
                LineDefinitionEditorView lineDefinitionEditorView = new LineDefinitionEditorView();
                lineDefinitionEditorView.initialize();
                frame.setContentPane(lineDefinitionEditorView.panel1);
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }

    private void domainObjectChanged() {
        backingList.changed();
    }

    public void initialize() {

        domainModelObject = new ConstantLineWidthTextFileDefinition();

        backingList = new ObservableListWrapper<>(domainModelObject.fields);
        JavaBeanTableModel<Field> tableModel = new JavaBeanTableModel<>(backingList);
        tableModel.addColumn(FieldViewModel.BEGIN_PROPERTY, "begin");
        tableModel.addColumn(FieldViewModel.END_PROPERTY, "end");
        tableModel.addColumn(FieldViewModel.LENGTH_PROPERTY, "length");
        tableModel.addColumn(FieldViewModel.NAME_PROPERTY, "name");
        table1.setModel(tableModel);

        final SingleTableItemSelectedState singleTableItemSelectedState = new SingleTableItemSelectedState(table1);
        final SelectionModelAdapter observableSelectionModel = new SelectionModelAdapter(table1);

        ObservableState firstTableItemSelected = new ObservableStateInterpreterAdapter<>(observableSelectionModel, new Interpreter<Integer[], Boolean>() {
            @Override
            public Boolean interpret(Integer[] selectedRows) {
                return selectedRows != null && selectedRows.length == 1 && selectedRows[0] == 0;
            }
        });
        ObservableState lastTableItemSelected = new ObservableStateInterpreterAdapter<>(observableSelectionModel, new Interpreter<Integer[], Boolean>() {
            @Override
            public Boolean interpret(Integer[] selectedRows) {
                return selectedRows != null && selectedRows.length == 1 && selectedRows[0] == table1.getRowCount() - 1;
            }
        });
        when(singleTableItemSelectedState).activate(deleteButton);
        when(singleTableItemSelectedState).and(not(firstTableItemSelected)).activate(upButton);
        when(singleTableItemSelectedState).and(not(lastTableItemSelected)).activate(downButton);


    }

}
