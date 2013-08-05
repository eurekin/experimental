[![Build Status](https://travis-ci.org/eurekin/experimental.png?branch=master)](https://travis-ci.org/eurekin/experimental) Experimental 
============

GUI-related boilerplate code generation + declarative interface description builders

Running example
============

Simple master-detail table view application implemented using this project is illustrated below.

![Swing using the experimental project](http://i.imgur.com/zl7qy4P.gif?1 "Preview")

You can running the example directly using Java WebStart - click on the icon below:

[![JNLP run](http://www.fileinfo.com/images/icons/files/128/jnlp-1858.png)](https://dl-web.dropbox.com/spa/5e9kbyw5dxyp93u/experimental/public/app.jnlp)

Example code
============

Domain object 1. (row):
```java
public class Field {

  // for each field a property will be generated
  Integer length = 1;
  String name = "name";
  Integer begin = 0;
  Integer end;

  // for the method there will be a Runnable generated
  public void actionnnnnn() {
    this.length += 1;
    parent.recalculateIndices();
  }
  ...
}    
```

Domain object 2. (table):

```java

public class ConstantLineWidthTextFileDefinition {

  // a property for the list will be generated
  List<Field> fields = new ArrayList<>();

}
```

the GUI

```java

public class LineDefinitionEditorView {

  // can be generated in any WYSYWIG editor
  // or manually defined - that's not relevant
  // here
  private JTable table1;
  private JPanel panel1;
  private JButton newButton;
  private JButton deleteButton;
  private JButton upButton;

  public void initialize() {

    domainModelObject = new ConstantLineWidthTextFileDefinition();

    // TABLE

    // Describe table model based on the generated Properties
    // All methods are type-safe
    backingList = new ObservableListWrapper<>(domainModelObject.fields);
    JavaBeanTableModel<Field> tableModel = new JavaBeanTableModel<>(backingList);
    tableModel.addColumn(FieldViewModel.BEGIN_PROPERTY, "begin");
    tableModel.addColumn(FieldViewModel.END_PROPERTY, "end");
    tableModel.addColumn(FieldViewModel.LENGTH_PROPERTY, "length");
    tableModel.addColumn(FieldViewModel.NAME_PROPERTY, "name");
    table1.setModel(tableModel);
    ...

    // STATES
    final SingleTableItemSelectedState singleTableItemSelectedState = new SingleTableItemSelectedState(table1);
    final SelectionModelAdapter observableSelectionModel = new SelectionModelAdapter(table1);
    ...

    // BUTTONS
    when(singleTableItemSelectedState).activate(deleteButton);
    when(singleTableItemSelectedState).and(not(firstTableItemSelected)).activate(upButton);
    when(singleTableItemSelectedState).and(not(lastTableItemSelected)).activate(downButton);
    
    // EDIT field on the bottom
    when(singleTableItemSelected).showCard(cardPanel, "edit");
    when(not(singleTableItemSelected)).showCard(cardPanel, "null");
    
    // Observable abuse - not very useful, but fluent API allows to read itself quite easily
    Observable<Integer> lastIndex = subtract(sizeOf(backingList), constant(1));
    Constant<Integer> firstIndex = constant(0);

    ObservableState firstTableItemSelected = does(selectedIndices, contain(firstIndex)); // ?
    ObservableState lastTableItemSelected = does(selectedIndices, contain(lastIndex)); // ?

  }    
}
```
