package pl.eurekin.experimental.swing;

import pl.eurekin.experimental.*;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

/**
 * @author greg.matoga@gmail.com
 */
public class JTextComponentBinder {
    private final JTextComponent textComponent;
    private Property<String> property;
    private boolean notifyingInProgress;

    public JTextComponentBinder(JTextComponent textComponent) {
        this.textComponent = textComponent;
    }

    public <T, E extends Observable<T>> void to(final PropertyAccessor<String, T> accessor,
                                                            final E object) {
        this.property = new Property<String>(new Getter<String>() {
            @Override
            public String get() {
                if (object.get() == null) return null;
                return accessor.get(object.get());
            }
        }, new Setter<String>() {
            @Override
            public void set(String newValue) {
                if (object.get() != null)
                    accessor.set(object.get(), newValue);
            }
        }
        );
        bindToWidget(object);
        bindWidgetToProperty();
        copyFromPropertyToDocument();
    }

    private <T, E extends Observable<T>> void bindToWidget(E object) {

        // TODO make it safe
        // This madness is to properly handle the data race scenario
        object.registerChangeListener( new UnsafePropertyListener<T>(new SafePropertyListener.ChangeListener() {
            @Override
            public void act() {
                basePropertyChanged();
            }
        }));
    }


    public void to(Property<String> property) {
        this.property = property;
        bindPropertyToWidget();
        bindWidgetToProperty();
        copyFromPropertyToDocument();
    }

    private void bindWidgetToProperty() {
        textComponent.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                copyFromDocumentToProperty();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                copyFromDocumentToProperty();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                copyFromDocumentToProperty();
            }
        });
    }

    private void copyFromDocumentToProperty() {
        if (iAmCalledBecauseOfMyself()) return;
        protectFromRecursiveSelfCall();
        property.set(textComponent.getText());
        protectionOff();
    }

    private boolean iAmCalledBecauseOfMyself() {
        return notifyingInProgress;
    }

    private void protectionOff() {
        this.notifyingInProgress = false;
    }

    private void protectFromRecursiveSelfCall() {
        this.notifyingInProgress = true;
    }

    private void copyFromPropertyToDocument() {
        if (iAmCalledBecauseOfMyself()) return;
        protectFromRecursiveSelfCall();
        textComponent.setText(property.get());
        protectionOff();
    }

    private void bindPropertyToWidget() {

        // This madness is to properly handle the data race scenario
        property.registerChangeListener(
                new UnsafePropertyListener<String>(new SafePropertyListener.ChangeListener() {
                    @Override
                    public void act() {
                        basePropertyChanged();
                    }
                }));
    }

    private void basePropertyChanged() {
        copyFromPropertyToDocument();
    }

}
