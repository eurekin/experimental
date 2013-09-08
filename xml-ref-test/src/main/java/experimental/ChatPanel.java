package experimental;

import pl.eurekin.mdb.client.ClientApplication;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author greg.matoga@gmail.com
 */
public class ChatPanel {

    private JButton goButton;
    private JTextField argumentField;

    public JPanel constructChatPanel() {
        final JPanel chatTab = new JPanel(new BorderLayout());
        final JPanel chatPanel = new JPanel(new BorderLayout());
        argumentField = new JTextField(ClientApplication.DEFAULT_PROVIDER_URL);
        goButton = new JButton("Connect");

        goButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onConnect();
            }
        });
        chatPanel.add(argumentField, BorderLayout.CENTER);
        chatPanel.add(goButton, BorderLayout.EAST);
        chatTab.add(chatPanel, BorderLayout.NORTH);
        return chatTab;
    }

    private void onConnect() {
        goButton.setEnabled(false);
        new SwingWorker<Void, Void>() {
            @Override
            protected void done() {
                goButton.setEnabled(true);
            }

            @Override
            protected Void doInBackground() throws Exception {
                connect();
                return null;
            }


        }.execute();

    }

    private void connect() {
        try {
            ClientApplication.main(new String[]{argumentField.getText()});
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
}
