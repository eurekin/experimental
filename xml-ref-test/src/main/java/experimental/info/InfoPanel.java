package experimental.info;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

/**
 * @author gmatoga
 */
public class InfoPanel {

    public static final String PREFIX = "${env";

    public static void main(String... args) throws Exception{

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        final JFrame frame = new JFrame("Properties");
        frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        frame.add(new InfoPanel().getPanel());
        frame.pack();
        frame.setVisible(true);
    }

    public JPanel getPanel() {


        JPanel panel = new JPanel(new BorderLayout());
        final GridBagLayout layout = new GridBagLayout();

        GridBagConstraints c = new GridBagConstraints();

        JPanel innerPanel = new JPanel(layout);
        panel.setBackground(Color.white);
        innerPanel.setBackground(Color.white);
        innerPanel.setBorder(BorderFactory.createLineBorder(Color.white,5));
        int i = 0;

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0;
        c.gridx = 0;
        c.gridy = i;
        c.gridwidth = 2;
        c.insets = new Insets(5, 5, 5, 5);
        final JLabel textArea = new JLabel("<html><body style='width: 100%'>This tab presents the environment properties, " +
                "as of the last build", UIManager.getIcon("OptionPane.informationIcon"), JLabel.LEADING);
        final Font tabFont = new Font("Dialog", Font.PLAIN, 12);
        textArea.setFont(tabFont);
        innerPanel.add(textArea, c);
        i++;

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0;
        c.gridx = 0;
        c.gridy = i;
        c.gridwidth = 2;
        c.insets = new Insets(5, 5, 5, 5);
        innerPanel.add(new JSeparator(), c);
        i++;

        Properties prop = new Properties();
        InputStream in = InfoPanel.class.getResourceAsStream("/about.properties");
        try {

            prop.load(in);
            in.close();
            Map<String, String> filteredProperties = new LinkedHashMap<String, String>();
            for (Map.Entry entry : prop.entrySet()) {
                final String val = entry.getValue().toString();
                final String key = entry.getKey().toString();
                String value = !val.startsWith(PREFIX) ? val : null;
                filteredProperties.put(key, value);
            }

            for (Map.Entry<String, String> entry : filteredProperties.entrySet()) {
                final JLabel keyLabel = new JLabel(entry.getKey() + ":");
                keyLabel.setFont(tabFont);
                final String value = entry.getValue();
                final JTextField valueLabel = new JTextField(value);
                valueLabel.setEditable(false);
                valueLabel.setBackground(Color.white);
                valueLabel.setBorder(null);
                valueLabel.setFont(tabFont);

                if (value == null) {
                    valueLabel.setText("---");
                    valueLabel.setEnabled(false);
                }
                c.fill = GridBagConstraints.HORIZONTAL;
                c.weightx = 0;
                c.gridx = 0;
                c.gridy = i;
                c.gridwidth = 1;
                c.insets = new Insets(5, 5, 0, 0);
                innerPanel.add(keyLabel, c);

                c.fill = GridBagConstraints.HORIZONTAL;
                c.weightx = 1;
                c.gridx = 1;
                c.gridy = i;
                c.gridwidth = 1;
                c.insets = new Insets(5, 15, 0, 0);
                innerPanel.add(valueLabel, c);
                i++;
            }

            JScrollPane scrollPane = new JScrollPane(innerPanel,
                    VERTICAL_SCROLLBAR_AS_NEEDED,
                    HORIZONTAL_SCROLLBAR_AS_NEEDED);
            panel.add(scrollPane);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return panel;
    }
}
