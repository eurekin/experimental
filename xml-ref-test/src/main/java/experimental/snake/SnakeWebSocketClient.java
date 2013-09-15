package experimental.snake;

import experimental.snake.json.AbstractMessage;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.net.URI;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.eclipse.persistence.jaxb.JAXBContextFactory.createContext;

/**
 * @author greg.matoga@gmail.com
 */
public class SnakeWebSocketClient
        extends WebSocketClient
        implements DirectionListener {

    public static final String SERVER_URL = "ws://wsdemo-eurekin.rhcloud.com:8000/examples/websocket/snake";
    private final JAXBContext jc;
    private Unmarshaller unmarshaller;
    private SnakeController snakeController;
    private Paintable paintable;

    public SnakeWebSocketClient() throws  Exception {
        this(null);
    }

    public SnakeWebSocketClient(Paintable paintable) throws JAXBException {
        super(URI.create(SERVER_URL));

        // MOXY
        Map<String, Object> properties = new HashMap<String, Object>(2);
        properties.put(JAXBContextProperties.MEDIA_TYPE, "application/json");

        jc = createContext(new Class[]{AbstractMessage.class}, properties);
        unmarshaller = jc.createUnmarshaller();
        unmarshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);

        // SNAKE
        snakeController = new SnakeController();
        this.paintable = paintable;
    }

    public static void main(String... args) throws Exception {

        final SnakeWebSocketClient client = new SnakeWebSocketClient();

        client.connectBlocking();
        client.send("north");

        Integer delay = 5000;
        //closeClientAfter(client, delay);

    }

    private static void closeClientAfter(final SnakeWebSocketClient client, Integer delay) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                client.close();
            }
        }, delay);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println(
                "handshakedata, status :  " +
                        handshakedata.getHttpStatus() + ", message : \n" +
                        handshakedata.getHttpStatusMessage());
    }

    @Override
    public void onMessage(String message) {
        String fixedMessage = null;
        try {
            fixedMessage = fix(message);
            deserialize(fixedMessage);
            paint();
        } catch (Exception rootCause) {
            throw new AcceptingSnakeMessageException(message, fixedMessage, rootCause);
        }
    }

    private void paint() {
        if(this.paintable!=null)
            snakeController.paint(paintable);
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
        fixedMessage = replace(fixedMessage, attributeToQuote + ":", "\"" + attributeToQuote + "\":");
        return fixedMessage;
    }

    private String replace(String message, String from, String to) {
        return message.replaceAll(
                Pattern.quote(from),
                Matcher.quoteReplacement(to)
        );
    }

    private void deserialize(String message) {
        JAXBElement<AbstractMessage> unmarshalResult = null;
        AbstractMessage unmarshalledBaseMessage = null;
        try {
            unmarshalResult = interpretWithJAXB(message);
            unmarshalledBaseMessage = unmarshalResult.getValue();
            snakeController.acceptMessage(unmarshalledBaseMessage);
        } catch (Exception e) {
            throw new SnakeMessageDeserializeException(unmarshalResult, unmarshalledBaseMessage, e);
        }
    }

    private JAXBElement<AbstractMessage> interpretWithJAXB(String message) throws JAXBException {
        return unmarshaller.unmarshal(
                new StreamSource(new StringReader(message)),
                AbstractMessage.class);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("code = " + code);
        System.out.println("reason = " + reason);
        System.out.println("remote = " + remote);
    }

    @Override
    public void onError(Exception ex) {
        throw new RuntimeException(ex);
    }

    // DirectionListener

    public static final Map<String, String> keyboardToWindDirections = new HashMap<String, String>();

    static  {
        keyboardToWindDirections.put("up", "north");
        keyboardToWindDirections.put("down", "south");
        keyboardToWindDirections.put("left", "west");
        keyboardToWindDirections.put("right", "east");
    }

    @Override
    public void direction(String code) {
        send(keyboardToWindDirections.get(code));
    }

    // Error handling
    //
    // Hold and print all possible details about the cause.
    // That proved to be quite useful - way too many times. Could've
    // make them package local instead to improve stacktrace readability

    private static class SnakeMessageDeserializeException extends RuntimeException {
        public SnakeMessageDeserializeException(
                JAXBElement<AbstractMessage> unmarshalResult,
                AbstractMessage unmarshalledBaseMessage,
                Exception baseException) {
            super(format(unmarshalResult, unmarshalledBaseMessage), baseException);
        }

        private static String format(JAXBElement<AbstractMessage> unmarshalResult, AbstractMessage unmarshalledBaseMessage) {

            return "Error while deserializing message \n" +
                    "unmarshalResult         : " + unmarshalResult + " \n" +
                    "unmarshalledBaseMessage : " + unmarshalledBaseMessage;
        }
    }

    private static class AcceptingSnakeMessageException extends RuntimeException {
        public AcceptingSnakeMessageException(String message, String fixedMessage, Exception rootCause) {
            super(formatMessage(message, fixedMessage), rootCause);
        }

        private static String formatMessage(String message, String fixedMessage) {
            return "Error while receiving following:\n" +
                    "received message : " + message + "\n" +
                    "after fixing     : " + fixedMessage;

        }
    }
}
