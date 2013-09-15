package experimental.chess.json;

import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.MarshallerProperties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;

public class UnmarshalDemo {
 
    public static void main(String[] args) throws Exception {
            Map<String, Object> properties = new HashMap<String, Object>(2);
            properties.put(JAXBContextProperties.MEDIA_TYPE, "application/json");
            JAXBContext jc = JAXBContext.newInstance(new Class[] {Person.class}, properties);

            Person john = new Person();
            john.setId(2);
            john.setType("John");

            Person jane = new Person();
            jane.setId(100);
            jane.setType("Jane");
//        jane.setData(new ArrayList<Person>(Arrays.asList(john)));


            Marshaller marshaller = jc.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(jane, System.out);


        StringWriter stringWriter = new StringWriter();
        marshaller.marshal(jane, stringWriter);

        Unmarshaller unmarshaller = jc.createUnmarshaller();
//        unmarshaller.setProperty("eclipselink.media-type", "application/json");
        unmarshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);

        StringReader reader = new StringReader(stringWriter.toString());
        Source source = new StreamSource(reader);
        Person valuelast = unmarshaller.unmarshal(source, Person.class).getValue();
        System.out.println("valuelast = " + valuelast);
    }
 
}