package experimental;

import pl.eurekin.mdb.client.ClientApplication;
import pl.eurekin.mdb.client.ClientApplicationHornetOnly;
import pl.eurekin.mdb.client.PortForwarder;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.EAST;
import static java.awt.BorderLayout.WEST;
import static javax.swing.JComponent.WHEN_FOCUSED;
import static javax.swing.KeyStroke.getKeyStroke;

/**
 * @author greg.matoga@gmail.com
 */
public class ChatPanel {

    private JButton goButton;
    private JTextField argumentField;
    private JButton pfButton;
    private JButton sendButton;
    private JTextField messageField;
    private JTextPane conversationPane;
    private ClientApplicationHornetOnly clientApplicationHornetOnly;

    public JPanel constructChatPanel() {
        final JPanel chatTab = new JPanel(new BorderLayout());
        final JPanel connectionPanel = new JPanel(new BorderLayout());
        final JPanel messagePanel = new JPanel(new BorderLayout());

        argumentField = new JTextField(ClientApplication.DEFAULT_PROVIDER_URL);
        messageField = new JTextField("");
        messageField.registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enterPressed();
            }
        }, getKeyStroke(KeyEvent.VK_ENTER, 0), WHEN_FOCUSED);

        conversationPane = new JTextPane();
        conversationPane.setEditable(false);

        pfButton = new JButton("forward");
        goButton = new JButton("Connect");
        sendButton = new JButton("send");

        pfButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onForward();
            }
        });
        goButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onConnect();
            }
        });
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onSend();
            }
        });

        connectionPanel.add(pfButton, WEST);
        connectionPanel.add(argumentField, CENTER);
        connectionPanel.add(goButton, EAST);

        messagePanel.add(messageField, CENTER);
        messagePanel.add(sendButton, EAST);

        chatTab.add(connectionPanel, BorderLayout.NORTH);
        chatTab.add(conversationPane, CENTER);
        chatTab.add(messagePanel, BorderLayout.SOUTH);
        return chatTab;
    }

    private void enterPressed() {
        onSend();
    }

    private void onSend() {
        String messageFieldText = messageField.getText();
        String prefix = System.getProperty("user.name") + ": ";
        String textToSend = prefix + messageFieldText;
        send(textToSend);
        messageField.setText("");
    }

    public void receive(String text) {
        Document document = conversationPane.getDocument();
        int offset = document.getLength();
        try {
            document.insertString(offset, text + "\n", null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void send(final String text) {
        new Thread() {
            @Override
            public void run() {
            clientApplicationHornetOnly.send(text);

            }
        }.start();
    }

    private void onForward() {
        new PortForwarder();
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
            String argumentFieldText = argumentField.getText();
            // ClientApplicationHornetOnly.main(new String[]{argumentFieldText});
            clientApplicationHornetOnly = new ClientApplicationHornetOnly();
            clientApplicationHornetOnly.initialize(argumentFieldText);
            clientApplicationHornetOnly.addListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    try {
                        receive(((TextMessage)message).getText());
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
}
