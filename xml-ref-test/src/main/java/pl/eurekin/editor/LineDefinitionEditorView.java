package pl.eurekin.editor;

import pl.eurekin.experimental.*;
import pl.eurekin.experimental.state.Interpreter;
import pl.eurekin.experimental.state.ObservableInterpreterAdapter;
import pl.eurekin.experimental.state.ObservableState;
import pl.eurekin.experimental.state.ObservableStateInterpreterAdapter;
import pl.eurekin.experimental.swing.JTextComponentBinder;
import pl.eurekin.experimental.swing.SelectionModelAdapter;
import pl.eurekin.experimental.swing.SingleTableItemSelectedState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
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
    private JTextField textField1;
    private JButton editButton;
    private ObservableListAdapter<Field> backingList;
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

    private static JTextComponentBinder bind(JTextField textField1) {
        return new JTextComponentBinder(textField1);
    }

    private void domainObjectChanged() {
        backingList.changed();
    }

    public void initialize() {

        domainModelObject = new ConstantLineWidthTextFileDefinition();

        backingList = new ObservableListAdapter<>(domainModelObject.fields);
        JavaBeanTableModel<Field> tableModel = new JavaBeanTableModel<>(backingList);
        tableModel.addColumn(FieldViewModel.BEGIN_PROPERTY, "begin");
        tableModel.addColumn(FieldViewModel.END_PROPERTY, "end");
        tableModel.addColumn(FieldViewModel.LENGTH_PROPERTY, "length");
        tableModel.addColumn(FieldViewModel.NAME_PROPERTY, "name");
        table1.setModel(tableModel);

        final SelectionModelAdapter observableSelectionModel = new SelectionModelAdapter(table1);
        final ObservableState singleTableItemSelectedState = new SingleTableItemSelectedState(observableSelectionModel);
        SelectedObjectsAdapter<Field> selectedObjectsAdapter = new SelectedObjectsAdapter<>(observableSelectionModel, backingList);
        Interpreter<List<Field>, Field> selectedFieldInterpreter = new FirstItemFromList<>();
        ObservableInterpreterAdapter<List<Field>, Field> selectedObject = new ObservableInterpreterAdapter<>(selectedObjectsAdapter, selectedFieldInterpreter);


        ObservableState firstTableItemSelected = new ObservableStateInterpreterAdapter<>(observableSelectionModel, new Interpreter<Integer[], Boolean>() {
            @Override
            public Boolean interpret(Integer[] selectedRows) {
                return Arrays.asList(selectedRows).contains(0);
            }
        });
        ObservableState lastTableItemSelected = new ObservableStateInterpreterAdapter<>(observableSelectionModel, new Interpreter<Integer[], Boolean>() {
            @Override
            public Boolean interpret(Integer[] selectedRows) {
                return Arrays.asList(selectedRows).contains(table1.getRowCount() - 1);
            }
        });

        bind(textField1).to(FieldViewModel.NAME_PROPERTY, selectedObject);
        when(singleTableItemSelectedState).activate(deleteButton);
        when(singleTableItemSelectedState).and(not(firstTableItemSelected)).activate(upButton);
        when(singleTableItemSelectedState).and(not(lastTableItemSelected)).activate(downButton);

        upButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getFieldViewModel().run();
                backingList.changed();
            }
        });
        // TODO manual mock, implement declarative version
        editButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FieldViewModel model = new FieldViewModel(backingList.get(table1.getSelectedRow()));
                bind(textField1).to(model.nameProperty);

                // Since there is no real connection between the selected object and the one edited
                // in the text field, the table has to be notified of any change
                model.nameProperty.registerChangeListener(new SimpleChangedPropertyListener(new AnythingHappenedListener() {
                    @Override
                    public void onPropertyChanged() {
                        backingList.changed();
                    }
                }));
            }
        });

    }

    private Runnable getFieldViewModel() {
        // TODO implement a delegating fieldViewModel, which can be used as selected table item
        return new FieldViewModel(backingList.get(table1.getSelectedRow())).actionAction;
    }

}
