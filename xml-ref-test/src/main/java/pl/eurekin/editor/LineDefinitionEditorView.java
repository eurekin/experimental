package pl.eurekin.editor;

import org.oxbow.swingbits.dialog.task.TaskDialogs;
import pl.eurekin.experimental.*;
import pl.eurekin.experimental.state.Interpreter;
import pl.eurekin.experimental.state.ObservableInterpreterAdapter;
import pl.eurekin.experimental.state.ObservableState;
import pl.eurekin.experimental.state.SimpleState;
import pl.eurekin.experimental.swing.DelegateAction;
import pl.eurekin.experimental.swing.SelectionModelAdapter;
import pl.eurekin.experimental.swing.SequentialComposedRunnable;
import pl.eurekin.experimental.swing.SingleTableItemSelectedState;
import pl.eurekin.experimental.swing.selection.SelectionRestore;
import pl.eurekin.experimental.swing.selection.SelectionStore;
import pl.eurekin.experimental.viewmodel.ViewModel;
import pl.eurekin.experimental.viewmodel.ViewModelFactory;
import xmleditorkit.XMLEditorKit;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import static pl.eurekin.experimental.ExpressionBuilder.not;
import static pl.eurekin.experimental.ExpressionBuilder.when;
import static pl.eurekin.experimental.fluent.CustomObservables.*;

/**
 * @author Rekin
 */
public class LineDefinitionEditorView {

    public JPanel panel1;
    private JTable table1;
    private JButton newButton;
    private JButton deleteButton;
    private JButton growButton;
    private JButton downButton;
    private JTextField textField;
    private JPanel cardPanel;
    private JButton saveButton;
    private JButton loadButton;
    private JButton upButton;
    private JButton shrinkButton;
    private ObservableListAdapter<Field> backingList;
    private ConstantLineWidthTextFileDefinition domainModelObject;
    private Runnable storeSelection;
    private Runnable restoreSelection;
    private JDialog dialog;
    private JEditorPane jEditorPane;

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

        backingList = new ObservableListAdapter<Field>(domainModelObject.fields);
        JavaBeanTableModel<Field> tableModel = new JavaBeanTableModel<Field>(backingList);
        tableModel.addColumn(FieldViewModel.BEGIN_PROPERTY, "begin");
        tableModel.addColumn(FieldViewModel.END_PROPERTY, "end");
        tableModel.addColumn(FieldViewModel.LENGTH_PROPERTY, "length");
        tableModel.addColumn(FieldViewModel.NAME_PROPERTY, "name");
        table1.setModel(tableModel);

        final SelectionModelAdapter selectedIndices = new SelectionModelAdapter(table1);
        final ObservableState singleTableItemSelected = new SingleTableItemSelectedState(selectedIndices);
        final SelectedObjectsAdapter<Field> selectedObjectsAdapter = new SelectedObjectsAdapter<Field>(selectedIndices, backingList);
        Interpreter<List<Field>, Field> selectedFieldInterpreter = new ItemFromSingularList<Field>();
        final ObservableInterpreterAdapter<List<Field>, Field> selectedObject = new ObservableInterpreterAdapter<List<Field>, Field>(selectedObjectsAdapter, selectedFieldInterpreter);

        Observable<Integer> lastIndex = subtract(sizeOf(backingList), constant(1));
        Constant<Integer> firstIndex = constant(0);

        ObservableState firstTableItemSelected = does(selectedIndices, contain(firstIndex)); // ?
        ObservableState lastTableItemSelected = does(selectedIndices, contain(lastIndex)); // ?

        // prototype for automatic binding
        ViewModelFactory<Field, FieldViewModel> factory = new FieldViewModelFactory();
        final FieldViewModel selectedObjectFromTable =
                standardViewModelConverterFor(factory, selectedObject, tableModel);
        // works!!!


        bind(textField).to(selectedObjectFromTable.nameProperty);
        when(singleTableItemSelected).enable(textField);
        when(singleTableItemSelected).enable(textField);

        // visual null value handling
        when(singleTableItemSelected).showCard(cardPanel, "edit");
        when(not(singleTableItemSelected)).showCard(cardPanel, "null");
        String initialCardName = "null";
        showCard(initialCardName);
        // prototype end

        when(singleTableItemSelected).activate(deleteButton).activate(growButton).activate(deleteButton);
        when(singleTableItemSelected).and(new SimpleState(false) {
            @Override
            public Boolean get() {
                try {
                    return selectedObjectFromTable.canShrink.call();
                } catch (Exception e) {
                    return false;
                }
            }
        }).activate(shrinkButton);
        when(singleTableItemSelected).and(not(firstTableItemSelected)).activate(upButton);
        when(singleTableItemSelected).and(not(lastTableItemSelected)).activate(downButton);


        Runnable refreshList = new Runnable() {

            @Override
            public void run() {
                domainObjectChanged();
            }
        };
        List<WeakReference<Field>> selectedFields = new ArrayList<WeakReference<Field>>();
        storeSelection = new SelectionStore<Field>(selectedObjectsAdapter, selectedFields);
        restoreSelection = new SelectionRestore<Field>(backingList, table1.getSelectionModel(), selectedFields);

        deleteButton.addActionListener(selectionSafeAction(selectedObjectFromTable.removeAction, refreshList));
        shrinkButton.addActionListener(selectionSafeAction(selectedObjectFromTable.shrinkAction, refreshList));
        growButton.addActionListener(selectionSafeAction(selectedObjectFromTable.growAction, refreshList));
        upButton.addActionListener(selectionSafeAction(selectedObjectFromTable.moveUpAction, refreshList));
        downButton.addActionListener(selectionSafeAction(selectedObjectFromTable.moveDownAction, refreshList));

        // name field => xml representation
        selectedObjectFromTable.baseProperty().registerChangeListener(new UnsafePropertyListener<Field>(new SafePropertyListener.ChangeListener() {
            @Override
            public void act() {
                updateXMLOrShowError();
            }
        }));

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showXMLFrame();
            }
        });
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadButtonAction();
            }
        });
        backingList.registerChangeListener(new UnsafePropertyListener<List<Field>>(new SafePropertyListener.ChangeListener() {
            @Override
            public void act() {
                updateXMLOrShowError();
            }
        }));
        // prototype end
    }

    private void loadButtonAction() {
        try {
            JTextArea textArea = new JTextArea("");
            JLabel label = new JLabel("");
            textArea.setFont(label.getFont());
            JScrollPane scrollArea = new JScrollPane(textArea);
            scrollArea.setPreferredSize(new Dimension(600, 400));
            Integer userChoice = JOptionPane.showConfirmDialog(
                    null,
                    new Object[]{"", scrollArea},
                    "XML input dialog",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            String sReturn = textArea.getText();
            if (userChoice == JOptionPane.OK_OPTION) {

                ConstantLineWidthTextFileDefinition newDefinition = new ConstantLineWidthTextFileDefinition();
                newDefinition.fromXml(sReturn);
                backingList.clear();
                backingList.addAll(newDefinition.fields);
                updateXMLOrShowError();
            }
        } catch (Exception e) {
            TaskDialogs.showException(e);
        }
    }

    private void showXMLFrame() {
        if (dialog != null) {
            dialog.setVisible(true);
            resetDialogLocation();
            return;
        }
        dialog = new JDialog((Dialog) null, "XML View");
        jEditorPane = new JEditorPane();
        jEditorPane.setEditorKit(new XMLEditorKit());

        updateXMLOrShowError();
        jEditorPane.setEditable(false);

        JComponent comp = new JScrollPane(jEditorPane);
        dialog.add(comp);
        resetDialogLocation();
        dialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        dialog.setVisible(true);

    }

    private void resetDialogLocation() {
        JFrame topLevelAncestor = (JFrame) this.panel1.getTopLevelAncestor();
        Integer width = 400;
        Integer height = topLevelAncestor.getHeight();
        dialog.setSize(width, height);
        dialog.setLocationRelativeTo(topLevelAncestor);
        Point oldLocation = topLevelAncestor.getLocation();
        oldLocation.translate(topLevelAncestor.getContentPane().getWidth(), 0);
        dialog.setLocation(oldLocation);
    }

    private void updateXMLOrShowError() {
        try {
            if (jEditorPane != null) {
                String xml = domainModelObject.toXml();
                jEditorPane.setEditorKit(new XMLEditorKit());
                jEditorPane.setText(xml);
            }
        } catch (Exception e) {
            TaskDialogs.showException(e);
        }
    }

    // todo this really belongs to either table model, selection model or both
    private DelegateAction selectionSafeAction(Runnable someAction, Runnable refreshList) {
        return new DelegateAction(new SequentialComposedRunnable(new SequentialComposedRunnable(new SequentialComposedRunnable(
                storeSelection,
                someAction),
                refreshList),
                restoreSelection)
        );
    }

    public void showCard(String cardName) {
        CardLayout layout = (CardLayout) cardPanel.getLayout();
        layout.show(cardPanel, cardName);
    }

    @SuppressWarnings("unchecked")
    private <T, E extends ViewModel<T>, O extends Observable<T>> E standardViewModelConverterFor(
            ViewModelFactory<T, E> factory, final O base, final JavaBeanTableModel<?> toChange) {

        final E viewModel = factory.newValueModel(base.get());
        final T initialBaseValue = viewModel.base();


        // TODO handle unchecked here
        for (Property observable : viewModel.allProperties())
            observable.registerChangeListener(
                    new UnsafePropertyListener<Object>(
                            new SafePropertyListener.ChangeListener() {
                                @Override
                                public void act() {
                                    toChange.fireTableDataChanged();
                                }
                            }
                    ));


        // Selected object => ViewModel ( => JTextArea.document )
        base.registerChangeListener(new UnsafePropertyListener<T>(new SafePropertyListener.ChangeListener() {
            @Override
            public void act() {
                if (base.get() != null)
                    viewModel.set(base.get());
                else
                    viewModel.set(initialBaseValue);

                // base.get() = null
                //
                // This is a very interesting case.
                // This cannot happen in the initialization phase and
                // thus the dummy NullObject from Factory won't be used.
                //
            }
        }));

        return viewModel;
    }

}
