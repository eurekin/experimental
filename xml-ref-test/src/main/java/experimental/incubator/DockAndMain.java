package experimental.incubator;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;


import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;


/**
 *
 * Dock View is on the side. It can be either on the side or top
 * bottom. One axis is of fixed size and the other takes the size of
 * the parent component.
 *
 * Main View takes rest of the space - as much as possible.
 *
 * Can be nested.
 *
 * @author gmatoga
 *
 */
public class DockAndMain {

    public static void main(String[] args) {

        constructUsing(new SwingViewLayer());
        constructUsing(new SwingViewLayer2());
        //constructUsing(new JavaFXViewLayer());
    }

    public static <T extends ViewLayer<?>> void constructUsing(ViewLayer<T> viewLayer) {
        constructUsing(viewLayer.composer(), viewLayer.factory(), viewLayer.exhibitor());
    }

    private static <T> void constructUsing(ViewComposer<T> composer, ViewFactory<T> factory, ViewExhibitor<T> exhibitor) {
        View<T> wholeView = new APIDemo().construct(composer, factory);
        exhibitor.show(wholeView);
    }

}

class APIDemo {

    <T> View<T> construct(ViewComposer<T> composer, ViewFactory<T> factory) {
        Button<T> viewMain = factory.createButton("main");
        Button<T> viewLeft = factory.createButton("left");
        Button<T> viewInner = factory.createButton("inner");

        Button<T> viewSecondTab = factory.createButton("tab2");

        View<T> dockInner = composer.dock(viewMain, viewInner);
        View<T> dockOuter = composer.dock(dockInner, viewLeft);

        List<View<T>> tabs = new ArrayList<View<T>>();
        tabs.add(dockOuter);
        tabs.add(viewSecondTab);

        View<T> viewTab = composer.tab(tabs);

        return viewTab;
    }
}

interface ViewLayer<T extends ViewLayer<?>> {

    ViewFactory<T> factory();

    ViewComposer<T> composer();

    ViewExhibitor<T> exhibitor();
}

interface ViewExhibitor<T> {

    void show(View<T> view);
}

interface ViewComposer<T> {

    View<T> dock(View<T> main, View<T> dock);

    View<T> tab(List<View<T>> main);
}

interface ViewFactory<T> {

    Button<T> createButton(String text);
}

interface Component<T> extends View<T>, Model<T> {

}

interface Button<T> extends Component<T> {

}

interface View<T> {

    Object getView();
}

interface Model<T> {

    Object getModel();
}

// Helper abstract base

class Caster {

    @SuppressWarnings("unchecked")
    public static <B, T> CommonClassView<B,T> castAsB(View<T> toCast, Class<B> type) {
        if (toCast instanceof CommonClassView<?, ?>) {
            CommonClassView<?, ?> ccDock = (CommonClassView<?, ?>) toCast;
            if(type.isInstance(ccDock.getView())) {
                return (CommonClassView<B, T>) ccDock;
            }

        }
        throw new IllegalArgumentException("Expected " + type + ", got "+ toCast.getView().getClass());
    }
}

abstract class AbstractBoilerplateViewLayer<T extends ViewLayer<T>, B> implements ViewLayer<T> {

    @Override
    final public ViewFactory<T> factory() {
        return commonClassViewFactory();
    }

    @Override
    final public ViewComposer<T> composer() {
        return commonClassComposer();
    }

    @Override
    final public ViewExhibitor<T> exhibitor() {
        return commonClassExhibitor();
    }

    protected abstract CommonClassViewComposer<B, T> commonClassComposer();

    protected abstract CommonClassViewFactory<B, T> commonClassViewFactory();

    protected abstract CommonClassViewExhibitor<B, T> commonClassExhibitor();

}

abstract class CommonClassViewExhibitor<B, T> implements ViewExhibitor<T> {

    Class<B> type;

    public CommonClassViewExhibitor(Class<B> type) {
        this.type = type;
    }



    @Override
    final public void show(View<T> view) {
        show(Caster.castAsB(view, type).getCView());
    }

    protected abstract void show(B view);
}

abstract class CommonClassViewComposer<B, T> implements ViewComposer<T> {

    Class<B> type;

    public CommonClassViewComposer(Class<B> type) {
        this.type = type;
    }

    @Override
    final public View<T> dock(View<T> main, View<T> dock) {
        final B rdocks = dock(Caster.castAsB(main, type).getCView(), Caster.castAsB(dock, type).getCView());

        return new CommonClassView<B, T>() {

            @Override
            protected B getCView() {
                return rdocks;
            }
        };
    }

    protected abstract B dock(B main, B dock);

    @Override
    final public View<T> tab(List<View<T>> tabs) {
        List<B> ctabs = new ArrayList<B>(tabs.size());
        for(View<T> tab : tabs)
            ctabs.add(Caster.castAsB(tab, type).getCView());


        final B rctabs = ctab(ctabs);

        return new CommonClassView<B, T>() {

            @Override
            protected B getCView() {
                return rctabs;
            }
        };
    }

    protected abstract B ctab(List<B> tabs);
}

abstract class CommonClassView<B, T> implements View<T> {

    @Override
    final public Object getView() {
        return getCView();
    }

    protected abstract B getCView();
}

abstract class CommonClassViewFactory<B, T> implements ViewFactory<T> {

    @Override
    public Button<T> createButton(String text) {
        return createCButton(text);
    }

    protected abstract CommonClassButton<B, T> createCButton(String text);
}

abstract class CommonClassButton<B, V> extends CommonClassView<B, V> implements Button<V> {

    protected final B getCView() {
        return buttonView();
    }

    protected abstract B buttonView();
}

// Helper base - impl test

class SwingViewLayer2 extends AbstractBoilerplateViewLayer<SwingViewLayer2, JComponent> {

    @Override
    protected CommonClassViewFactory<JComponent, SwingViewLayer2> commonClassViewFactory() {
        return new CommonClassViewFactory<JComponent, SwingViewLayer2>() {

            @Override
            protected CommonClassButton<JComponent, SwingViewLayer2> createCButton(final String text) {
                return new CommonClassButton<JComponent, SwingViewLayer2>() {

                    @Override
                    public Object getModel() {
                        return null;
                    }

                    @Override
                    protected JButton buttonView() {
                        return new JButton(text);
                    }
                };
            }
        };
    }

    @Override
    protected CommonClassViewComposer<JComponent, SwingViewLayer2> commonClassComposer() {
        return new CommonClassViewComposer<JComponent, SwingViewLayer2>(JComponent.class) {

            @Override
            protected JComponent dock(JComponent main, JComponent dock) {
                JPanel panel = new JPanel(new BorderLayout());
                panel.add(main, BorderLayout.CENTER);
                panel.add(dock, BorderLayout.WEST);
                return panel;
            }


            @Override
            protected JComponent ctab(List<JComponent> tabs) {
                final JTabbedPane tabPane = new JTabbedPane();
                for (JComponent tab : tabs) {
                    tabPane.add(tab);
                }

                return tabPane;
            }

        };
    }

    @Override
    protected CommonClassViewExhibitor<JComponent, SwingViewLayer2> commonClassExhibitor() {
        return new CommonClassViewExhibitor<JComponent, SwingViewLayer2>(JComponent.class) {

            @Override
            protected void show(JComponent  view) {
                SwingViewsBuilder.showInDemoFrameEDTsafe(view);
            }
        };
    }

}

// Swing

class SwingViewLayer implements ViewLayer<SwingViewLayer> {

    SwingViewLayer() {
        SwingViewsBuilder.setSystemLAF();
    }

    @Override
    public ViewFactory<SwingViewLayer> factory() {
        return new ViewFactory<SwingViewLayer>() {

            @Override
            public Button<SwingViewLayer> createButton(String text) {
                final JButton button = new JButton(text);
                button.setOpaque(false);
                return new Button<SwingViewLayer>() {

                    @Override
                    public Object getModel() {
                        return null;
                    }

                    @Override
                    public Object getView() {
                        return button;
                    }
                };
            }
        };
    }

    @Override
    public ViewComposer<SwingViewLayer> composer() {
        return new ViewComposer<SwingViewLayer>() {

            @Override
            public View<SwingViewLayer> dock(View<SwingViewLayer> main, View<SwingViewLayer> dock) {
                final JComponent mainComponent = (JComponent) main.getView();
                final JComponent dockComponent = (JComponent) dock.getView();
                return new View<SwingViewLayer>() {

                    @Override
                    public Object getView() {
                        JPanel panel = new JPanel(new BorderLayout());
                        panel.add(mainComponent, BorderLayout.CENTER);
                        panel.add(dockComponent, BorderLayout.WEST);
                        return panel;
                    }
                };
            }

            @Override
            public View<SwingViewLayer> tab(List<View<SwingViewLayer>> main) {
                List<JComponent> tabs = new ArrayList<JComponent>(main.size());
                for (View<SwingViewLayer> toAdd : main)
                    tabs.add((JComponent) toAdd.getView());

                final JTabbedPane tabPane = new JTabbedPane();
                for (JComponent component : tabs)
                    tabPane.add(component);
                return new View<SwingViewLayer>() {

                    @Override
                    public Object getView() {
                        return tabPane;
                    }
                };
            }
        };
    }

    @Override
    public ViewExhibitor<SwingViewLayer> exhibitor() {
        return new ViewExhibitor<SwingViewLayer>() {

            @Override
            public void show(View<SwingViewLayer> view) {
                SwingViewsBuilder.showInDemoFrameEDTsafe((JComponent) view.getView());
            }
        };
    }
}

// JavaFX
/**
class JavaFXViewLayer implements ViewLayer<JavaFXViewLayer> {

    public static View<JavaFXViewLayer> mainView;

    @Override
    public ViewFactory<JavaFXViewLayer> factory() {
        return new ViewFactory<JavaFXViewLayer>() {

            @Override
            public Button<JavaFXViewLayer> createButton(String text) {
                final javafx.scene.control.Button button = new javafx.scene.control.Button(text);
                button.setMaxWidth(Double.MAX_VALUE);
                button.setMaxHeight(Double.MAX_VALUE);
                return new Button<JavaFXViewLayer>() {

                    @Override
                    public Object getView() {
                        return button;
                    }

                    @Override
                    public Object getModel() {
                        return null;
                    }
                };
            }
        };
    }

    @Override
    public ViewComposer<JavaFXViewLayer> composer() {
        return new ViewComposer<JavaFXViewLayer>() {

            @Override
            public View<JavaFXViewLayer> dock(View<JavaFXViewLayer> main, View<JavaFXViewLayer> dock) {
                final BorderPane border = new BorderPane();
                border.setCenter((Node) main.getView());
                border.setLeft((Node) dock.getView());
                return new View<JavaFXViewLayer>() {

                    @Override
                    public Object getView() {
                        return border;
                    }
                };
            }

            @Override
            public View<JavaFXViewLayer> tab(List<View<JavaFXViewLayer>> main) {
                final TabPane tabPane = new TabPane();
                int i = 0;
                for (View<JavaFXViewLayer> view : main) {
                    Tab tab = new Tab("Tab (" + i++ + ")");
                    tab.setContent((Node) view.getView());
                    tabPane.getTabs().add(tab);

                }
                return new View<JavaFXViewLayer>() {

                    @Override
                    public Object getView() {
                        return tabPane;
                    }

                };
            }
        };
    }

    @Override
    public ViewExhibitor<JavaFXViewLayer> exhibitor() {
        return new ViewExhibitor<JavaFXViewLayer>() {

            @Override
            public void show(View<JavaFXViewLayer> view) {
                mainView = view;
                view.getView();
                javafx.application.Application.launch(JavaFXLauncherApp.class);

            }
        };
    }

    public static final class JavaFXLauncherApp extends javafx.application.Application {

        public JavaFXLauncherApp() {

        }

        @Override
        public void start(Stage stage) throws Exception {
            stage.setTitle("JavaFX");
            Node view = (Node) JavaFXViewLayer.mainView.getView();
            StackPane root = new StackPane();
            root.getChildren().add(view);
            stage.setScene(new Scene(root, 300, 250));
            stage.show();
        }
    }

}

 */