package experimental;

import experimental.chess.Board;
import experimental.console.MessageConsole;
import experimental.info.InfoPanel;
import experimental.snake.SnakeDemo;
import experimental.sweeper.Sweeper;
import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;
import pl.eurekin.editor.LineDefinitionEditorView;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.lang.reflect.Method;

import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

/**
 * @author gmatoga
 */
public class ShowOffApplication {

    private JFrame frame;
    private JPanel mainPanel;
    private JTabbedPane tabbedPane;
    private final static MessageConsole mc = new MessageConsole();
    static {
//        mc.redirectOut();
//        mc.redirectErr(Color.RED, null);
    }

    public static void main(String... args) throws Exception {
// enable anti-aliased text:
//        System.setProperty("awt.useSystemAAFontSettings","on");
//        System.setProperty("swing.aatext", "true");
//        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        setupFonts();
        System.setProperty("awt.useSystemAAFontSettings", "lcd");
        UIManager.put("RootPane.setupButtonVisible", false);
        BeautyEyeLNFHelper.frameBorderStyle =  BeautyEyeLNFHelper.FrameBorderStyle.osLookAndFeelDecorated;
        BeautyEyeLNFHelper.translucencyAtFrameInactive = false;
        BeautyEyeLNFHelper.launchBeautyEyeLNF();

//        UIManager.getDefaults().put("TabbedPane.contentBorderInsets", new Insets(-2, 1, 1, 1));
//        UIManager.getDefaults().put("TabbedPane.selectedTabPadInsets", new Insets(-2,1,1,1));
//        UIManager.getDefaults().put("TabbedPane.contentBorderInsets", new Insets(10,10,10,10));
//        UIManager.getDefaults().remove("TabbedPane.contentAreaColor");
//        UIManager.getDefaults().put("TabbedPane.contentAreaColor", Color.YELLOW);


        final ShowOffApplication showOffApplication = new ShowOffApplication();
        showOffApplication.safelyShowGUIfromAnyThread();

        if (System.getProperty("os.version").equalsIgnoreCase("6.1")) try {
            JNAtest.tryReallyHardToSetWindowsTaskbarProperties(showOffApplication.frame);
        } catch (Exception e) {
            System.err.println("Exception prevented IPropertyStorage configuration. Continuing despite error.");
            e.printStackTrace();
        }

    }

    private static void setupFonts() {
        /**The key be related to font of UIManager's UI */
        String[] DEFAULT_FONT  = new String[]{
                "Table.font"
                ,"TableHeader.font"
                ,"CheckBox.font"
                ,"Tree.font"
                ,"Viewport.font"
                ,"ProgressBar.font"
                ,"RadioButtonMenuItem.font"
                ,"ToolBar.font"
                ,"ColorChooser.font"
                ,"ToggleButton.font"
                ,"Panel.font"
                ,"TextArea.font"
                ,"Menu.font"
                ,"TableHeader.font"
                // ,"TextField.font"
                ,"OptionPane.font"
                ,"MenuBar.font"
                ,"Button.font"
                ,"Label.font"
                ,"PasswordField.font"
                ,"ScrollPane.font"
                ,"MenuItem.font"
                ,"ToolTip.font"
                ,"List.font"
                ,"EditorPane.font"
                ,"Table.font"
                ,"TabbedPane.font"
                ,"RadioButton.font"
                ,"CheckBoxMenuItem.font"
                ,"TextPane.font"
                ,"PopupMenu.font"
                ,"TitledBorder.font"
                ,"ComboBox.font"
        };

        // Change default font.
        for (int i = 0; i < DEFAULT_FONT.length; i++)
            UIManager.put(DEFAULT_FONT[i],new Font("Segoe UI", Font.PLAIN,12));
    }

    private void safelyShowGUIfromAnyThread() {
        final Runnable showGUIRunnable = new Runnable() {
            @Override
            public void run() {
                initiateAndShowOnEDT();
            }
        };
        if (SwingUtilities.isEventDispatchThread()) showGUIRunnable.run();
        else try {
            EventQueue.invokeAndWait(showGUIRunnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initiateAndShowOnEDT() {

        constructMainFrame();
        addMainPanel();
        showMainFrame();
    }

    private void addMainPanel() {
        constructMainPanel();
        frame.add(mainPanel);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private void constructMainPanel() {
        mainPanel = new JPanel();
        constructTabbedFrame();
        mainPanel.add(tabbedPane);
    }

    private void constructTabbedFrame() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        tabbedPane.setOpaque(false);

        addAllRegisteredShowOffViewsToJTabbedPane();
    }

    private void addAllRegisteredShowOffViewsToJTabbedPane() {
        final LineDefinitionEditorView lineDefinitionEditorView = new LineDefinitionEditorView();
        lineDefinitionEditorView.initialize();
        Icon icon1 = null;
        try {
            icon1 = new ImageIcon(ImageIO.read(this.getClass().getResourceAsStream("/table.png")));
        } catch (IOException e) {

        }
        tabbedPane.addTab("Table", icon1, lineDefinitionEditorView.panel1);

        Sweeper sweeper = new Sweeper(20, 40);
        final JPanel sweeperFrame = sweeper.getMainPanel();
        Icon sweeperIcon = sweeper.getIcon();
        tabbedPane.addTab("MineSweeper", sweeperIcon, sweeperFrame);


        final JPanel construct = new Board().construct();
        final JPanel outerPanel = new JPanel(new GridBagLayout());
        outerPanel.add(construct);
        Icon icon = null;
        try {
            icon = new ImageIcon(ImageIO.read(this.getClass().getResourceAsStream("/chess.png")));
        } catch (IOException e) {

        }
        tabbedPane.addTab("Chess", icon, outerPanel);



        Icon iconChat = null;
        try {
            iconChat = new ImageIcon(ImageIO.read(this.getClass().getResourceAsStream("/Chat-icon.png")));
        } catch (IOException e) {

        }
        JPanel chatTab = new ChatPanel().constructChatPanel();

        tabbedPane.addTab("Chat",iconChat, chatTab);

        // Snake panel
        JPanel snakePanel = new JPanel(new BorderLayout());
        final SnakeDemo snakeDemo = new SnakeDemo();
        JPanel topPanel = new JPanel(new GridBagLayout());
        JButton runButton = new JButton();
        runButton.setHorizontalAlignment(SwingConstants.RIGHT);
        topPanel.add(runButton);
        snakeDemo.initialize();
        JComponent snakeDemoView = snakeDemo.getView();
        JPanel snakeGamePanel = new JPanel(new GridBagLayout());
        snakeGamePanel.add(snakeDemoView);
        snakeGamePanel.setBorder(BorderFactory.createTitledBorder("Snake"));
        snakePanel.add(snakeGamePanel, BorderLayout.CENTER);
        snakePanel.add(topPanel, BorderLayout.NORTH);
        Icon snakeIcon = null;
        try {
            snakeIcon = new ImageIcon(ImageIO.read(this.getClass().getResourceAsStream("/snake.png")));
        } catch (IOException e) {
        }
        runButton.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                snakeDemo.run();
            }
        });
        runButton.setText("Run");
        tabbedPane.addTab("Snake", snakeIcon, snakePanel);

        // Console tab
        JPanel somePanel = new JPanel(new BorderLayout());
        JTextPane textComponent = new JTextPane();
        textComponent.setBorder(null);
        textComponent.setFont(new Font("Courier New", Font.PLAIN, 11));

        mc.setMessageLines(2000);
        JScrollPane scrollPane = new JScrollPane( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED );
        scrollPane.setViewportView(textComponent);
        scrollPane.setPreferredSize(new Dimension(100, 100));
        mc.setTextComponent(textComponent);

        somePanel.add(scrollPane);
        tabbedPane.addTab("Console", somePanel);

        // About
        final JPanel panel = new InfoPanel().getPanel();
        tabbedPane.addTab("About", panel);

    }

    private void showMainFrame() {
        frame.setVisible(true);
    }

    private JFrame constructMainFrame() {
        frame = new JFrame("Experimental show-off");
        try {
            frame.setIconImage(ImageIO.read(this.getClass().getResource("/ico_lab.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setLocationByPlatform(true);
        return frame;
    }
}
