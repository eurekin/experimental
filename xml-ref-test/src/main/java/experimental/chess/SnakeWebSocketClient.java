package experimental.chess;

import experimental.chess.json.Person;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.java_websocket.handshake.ServerHandshake;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author greg.matoga@gmail.com
 */
public class SnakeWebSocketClient extends org.java_websocket.client.WebSocketClient {

    public static final String SERVER_URL = "ws://wsdemo-eurekin.rhcloud.com:8000/examples/websocket/snake";
    private final JAXBContext jc;
    private Unmarshaller unmarshaller;

    public SnakeWebSocketClient() throws JAXBException {
        super(URI.create(SERVER_URL));

        Map<String, Object> properties = new HashMap<String, Object>(2);
        properties.put(JAXBContextProperties.MEDIA_TYPE, "application/json");
        jc = JAXBContext.newInstance(new Class[]{Message.class}, properties);
        unmarshaller = jc.createUnmarshaller();
        unmarshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("handshakedata = " + handshakedata);
    }

    @Override
    public void onMessage(String message) {
        System.out.println("message = " + message);
        String fixedMessage = fix(message);
        System.out.println("fixedMessage = " + fixedMessage);
        deserialize(fixedMessage);
    }

    private String fix(String message) {
        String fixedMessage;
        fixedMessage = replace(message, "'", "\"");
        fixedMessage = quoteAttribute(fixedMessage, "id");
        fixedMessage = quoteAttribute(fixedMessage, "color");
        fixedMessage = quoteAttribute(fixedMessage, "x");
        fixedMessage = quoteAttribute(fixedMessage, "y");
        return fixedMessage;
    }

    private String quoteAttribute(String fixedMessage, String attributeToQuote) {
        fixedMessage = replace(fixedMessage, attributeToQuote+":", "\""+attributeToQuote+"\":");
        return fixedMessage;
    }

    private String replace(String message, String from, String to) {
        return message.replaceAll(
                    Pattern.quote(from),
                    Matcher.quoteReplacement(to)
            );
    }

    private void deserialize(String message) {
        try {
            JAXBElement<Message> unmarshalResult = unmarshaller.unmarshal(new StreamSource(new StringReader(message)), Message.class);
            Message unmarshalledMessage
                    = unmarshalResult.getValue();
            System.out.println("unmarshalResult = " + unmarshalResult);
            System.out.println("unmarshalledMessage = " + unmarshalledMessage);

        } catch(Exception e) {
            System.out.println("No luck yet");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("code = " + code);
        System.out.println("reason = " + reason);
        System.out.println("remote = " + remote);
    }

    @Override
    public void onError(Exception ex) {
        System.out.println("ex = " + ex);
    }

    public static void main(String ... args) throws Exception {

        final SnakeWebSocketClient client = new SnakeWebSocketClient();

        client.connect();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                    client.close();
            }
        }, 1000);

    }

}
