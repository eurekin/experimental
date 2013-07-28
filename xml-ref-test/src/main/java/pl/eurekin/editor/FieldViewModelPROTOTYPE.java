package pl.eurekin.editor;


import pl.eurekin.experimental.*;

public class FieldViewModelPROTOTYPE implements ViewModel<Field> {

    @Override
    public Field base() {
        return base;
    }

    @Override
    public void set(Field newBase) {
        this.base = newBase;
        fireAllPropertyChange();
    }

    private void fireAllPropertyChange() {
        for(Property p : allProperties())
            p.signalExternalUpdate();
    }

    public Field base;

    public FieldViewModelPROTOTYPE(Field base) {
        this.base = base;
    }

    public static ViewModelFactory<Field, FieldViewModelPROTOTYPE> factory() {
        return  new ViewModelFactory<Field, FieldViewModelPROTOTYPE>() {
            @Override
            public FieldViewModelPROTOTYPE newValueModel(Field base) {
                if(base==null) {
                    base = new Field(); // TODO workaround fixme
                    base.length = 0;
                    base.name = "";
                }
                return new FieldViewModelPROTOTYPE(base);
            }
        };
    }


    @Override
    public Property<?>[] allProperties() {
        return new Property<?>[]{lengthProperty, nameProperty, beginProperty, endProperty};
    }

    public Property<Integer> lengthProperty = new Property<Integer>(
        new Getter<Integer>() {@Override public Integer get() { return base.length;}},
        new Setter<Integer>() {@Override public void set(Integer newValue) { base.length = newValue; }});
    public static PropertyAccessor<Integer, Field> LENGTH_PROPERTY = new PropertyAccessor<Integer, Field>(
            new TemplateGetter<Integer, Field>() {@Override public Integer get(Field base) { return base.length;}},
            new TemplateSetter<Integer, Field>() {@Override public void set(Field base, Integer newValue) { base.length = newValue; }});


    public Property<String> nameProperty = new Property<String>(
        new Getter<String>() {@Override public String get() { return base.name;}},
        new Setter<String>() {@Override public void set(String newValue) { base.name = newValue; }});
    public static PropertyAccessor<String, Field> NAME_PROPERTY = new PropertyAccessor<String, Field>(
            new TemplateGetter<String, Field>() {@Override public String get(Field base) { return base.name;}},
            new TemplateSetter<String, Field>() {@Override public void set(Field base, String newValue) { base.name = newValue; }});


    public Property<Integer> beginProperty = new Property<Integer>(
        new Getter<Integer>() {@Override public Integer get() { return base.begin;}},
        new Setter<Integer>() {@Override public void set(Integer newValue) { base.begin = newValue; }});
    public static PropertyAccessor<Integer, Field> BEGIN_PROPERTY = new PropertyAccessor<Integer, Field>(
            new TemplateGetter<Integer, Field>() {@Override public Integer get(Field base) { return base.begin;}},
            new TemplateSetter<Integer, Field>() {@Override public void set(Field base, Integer newValue) { base.begin = newValue; }});


    public Property<Integer> endProperty = new Property<Integer>(
        new Getter<Integer>() {@Override public Integer get() { return base.end;}},
        new Setter<Integer>() {@Override public void set(Integer newValue) { base.end = newValue; }});
    public static PropertyAccessor<Integer, Field> END_PROPERTY = new PropertyAccessor<Integer, Field>(
            new TemplateGetter<Integer, Field>() {@Override public Integer get(Field base) { return base.end;}},
            new TemplateSetter<Integer, Field>() {@Override public void set(Field base, Integer newValue) { base.end = newValue; }});


    public Runnable shrinkAction = new Runnable() {
        @Override public void run() { base.shrink(); }};
    public Runnable actionnnnnnAction = new Runnable() {
        @Override public void run() { base.actionnnnnn(); }};

    public Runnable removeAction = new Runnable() {
        @Override public void run() { base.remove(); }};
    public Runnable moveUpAction = new Runnable() {
        @Override public void run() { base.moveUp(); }};
    public Runnable moveDownAction = new Runnable() {
        @Override public void run() { base.moveDown(); }};

}