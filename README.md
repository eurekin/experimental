experimental
============

GUI-related boilerplate code generation + declarative interface description builders

Running example
============

![Swing using the experimental project](http://i.imgur.com/zl7qy4P.gif?1 "Preview")

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
		}    
	  }
```