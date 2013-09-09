package pl.eurekin.mdb.client;

import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.jms.HornetQJMSClient;
import org.hornetq.api.jms.JMSFactoryType;
import org.hornetq.core.remoting.impl.netty.TransportConstants;
import org.hornetq.jms.client.HornetQConnectionFactory;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author greg.matoga@gmail.com
 */
public class ReceiverClientTutorialFromIBM implements MessageListener {
    public static final String FACTORY_NAME = "jms/RemoteConnectionFactory";
    public static final String DESTINATION_NAME = "jms/queue/HELLOWORLDMDBQueue";
    public static final String PROVIDER_URL = "remote://localhost:4447";
    public static final String USER_NAME = "remote";
    public static final String USER_PASSWORD = "remoteJMS";

    private boolean stop = false;

    public static void main(String[] args) {
        new ReceiverClientTutorialFromIBM().receive();
    }

    public void receive() {


        try {
            final Properties env = new Properties();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");



            env.put(Context.PROVIDER_URL, PROVIDER_URL);

            // maybe the guide could help in getting rid of the credentials:
            // http://docs.jboss.org/xnio/2.0/api/org/jboss/xnio/Options.html
            env.put(Context.SECURITY_PRINCIPAL, USER_NAME);
            env.put(Context.SECURITY_CREDENTIALS, USER_PASSWORD);


            // client connector configuration:
            Map<String, Object> connectionParams = new HashMap<String, Object>();
            connectionParams.put(TransportConstants.PORT_PROP_NAME, 5445);
            connectionParams.put(TransportConstants.HOST_PROP_NAME, "localhost");

            TransportConfiguration transportConfiguration =
                    new TransportConfiguration(
                            "org.hornetq.core.remoting.impl.netty.NettyConnectorFactory",
                            connectionParams);

            HornetQConnectionFactory connectionFactory2 = HornetQJMSClient.createConnectionFactoryWithoutHA(JMSFactoryType.CF, transportConfiguration);

            Connection connection = connectionFactory2.createConnection(USER_NAME, USER_PASSWORD);

            // found answer in: https://community.jboss.org/message/721362#721362
            //
            // >>Yes, it is possible to disable this security.  Simply remove the
            // "security-realm" from the "remoting-connector" <connector>.
            // Here is what the default looks like:
            //
            //        <subsystem xmlns="urn:jboss:domain:remoting:1.1">
            //        <connector name="remoting-connector" socket-binding="remoting" security-realm="ApplicationRealm"/>
            //        </subsystem>
            //
            //         Change it to be like this:
            //
            //        <subsystem xmlns = "urn:jboss:domain:remoting:1.1" >
            //        <connector name = "remoting-connector" socket - binding = "remoting" / >
            //        </subsystem >
            // <<
            env.put("jboss.naming.client.jms.context", Boolean.TRUE);
            final Context context = new InitialContext(env);
//
//        final Hashtable<String, String> jndiProperties = new Hashtable<String, String>();
//        jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.jms.client.naming");
//        final Context context = new InitialContext(jndiProperties);

            //get reference to JMS destination
//        javax.jms.Queue destination =
//                (javax.jms.Queue) context.lookup("java:/queue/HELLOWORLDMDBQueue");

            //get reference to the ConnectionFactory

            // this won't work, supposedly because it's a container-local name. Let's try
            // another option
            // ConnectionFactory connectionFactory =
//                (ConnectionFactory) context.lookup("QueueConnectionFactory");
//        Object lookup = context.lookup("java:/ConnectionFactory");
//        System.out.println("lookup = " + lookup);
            // nope. Not working.
            // Another try: sound some snippet on a blog:
            //ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup("jms/RemoteConnectionFactory");
            System.out.println("connectionFactory = " + connectionFactory2);
            // gotcha!
            // lookup = HornetQConnectionFactory [serverLocator=ServerLocatorImpl
            // [initialConnectors=[org-hornetq-core-remoting-impl-netty-NettyConnectorFactory?port=5445&host=127-0-0-1],
            // discoveryGroupConfiguration=null], clientID=null, dupsOKBatchSize=1048576,
            // transactionBatchSize=1048576, readOnly=false]
            //
            // what to do with it now? It's a ... ConnectionFactory. Make a connection?

            // light in the tunnel: http://docs.jboss.org/hornetq/2.2.14.Final/user-manual/en/html_single/index.html#configuring-transports
            // NETTY http transport


            //Connection connection = connectionFactory2.createConnection(USER_NAME, USER_PASSWORD); // leaved out for later
            // Exception in thread "main" javax.jms.JMSException: Failed to create session factory
            // Caused by: HornetQException[errorCode=3 message=Timed out waiting to receive cluster topology. Group:null]
            // ok, a step forward.
            // Some info here: https://community.jboss.org/message/607846
            // .. but no solution. "After checking, it turned out that broadcasting is disabled.
            //  So I tried it on a server and worked correctly"
            // And what without the broadcasting?
            //
            // I dug into the source code and found some hint: the class StaticConnector
            // seems to connect as well, but does not attempt to "discover cluster topology".
            // Maybe that's somehow configurable?
            // Found some info here: http://stackoverflow.com/questions/13253609/cannot-create-jms-connection-from-standalone-java-application-to-jboss-as-7-mess
            // The configuration looks much like the one here.
            // "I solved the problem with checking and changing the Maven dependencies of my project.
            //  With this Maven dependency the program worked as expected:"
            // Dependency issue? Let's try
            // nothing.
            // More background info: https://community.jboss.org/message/721977#721977#721977
            // not really a solution yet. Still looking.

            // off-topic, trying if anything work at all
            Destination destination = (Destination) context.lookup("jms/queue/HELLOWORLDMDBTopic"); // only necessary if you don't want to use createQueue or createTopic

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);


            MessageConsumer receiver = session.createConsumer(destination);
            receiver.setMessageListener(this);
            connection.start();
            //Wait for stop
            while (!stop) {
                Thread.sleep(1000);
            }

            //Exit
            System.out.println("Exiting...");
            connection.close();
            System.out.println("Goodbye!");

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void onMessage(Message message) {

        try {
            String msgText = ((TextMessage) message).getText();
            System.out.println(msgText);
            if ("stop".equals(msgText))
                stop = true;
        } catch (JMSException e) {
            e.printStackTrace();
            stop = true;
        }
    }
}
