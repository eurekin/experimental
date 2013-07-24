package pl.eurekin.experimentaltest;

import pl.eurekin.experimental.ChangedPropertyListener;
import pl.eurekin.experimental.Getter;
import pl.eurekin.experimental.Property;
import pl.eurekin.experimental.Setter;

public class Application {

    Property<String> aStringProperty;
    String backingObject;
    private void testPropertyConstruction() {
        aStringProperty = new Property<>(
                new Getter<String>() {@Override public String get() {
                return Application.this.backingObject;}},
                new Setter<String>() {@Override public void set(String newValue) {
                Application.this.backingObject = newValue; }});

        aStringProperty.set("egege");
        System.out.println(aStringProperty.get().equals(backingObject));

        aStringProperty.registerChangeListener(new ChangedPropertyListener<String>() {
            @Override
            public void beginNotifying() {
                // TODO
            }

            @Override
            public void propertyChanged(String oldValue, String newValue) {
                System.out.println("Listener called oldValue = " + oldValue + " new Value = " + newValue);
            }

            @Override
            public void finishNotifying() {
                // TODO
            }
        });

        aStringProperty.set("Tada!");
    }
    public static void main(String ... args) {
        new Application().testPropertyConstruction();
    }

}
