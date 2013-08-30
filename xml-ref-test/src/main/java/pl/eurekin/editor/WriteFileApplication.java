package pl.eurekin.editor;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;


public class WriteFileApplication {


        public void start() throws JAXBException {
            Class<ConstantLineWidthTextFileDefinition> clazz = ConstantLineWidthTextFileDefinition.class;
            JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
            Marshaller marshaller = getMarshaller(jaxbContext);


            StringWriter sw = new StringWriter();
            marshaller.marshal(provideXMLContent(), sw);

            Unmarshaller unmarshaller = getUnmarshaller(jaxbContext);
            String marshaled = sw.toString();
            System.out.println(marshaled);

            StringReader sr = new StringReader(marshaled);
            unmarshaller.unmarshal(sr);

        }

    private Unmarshaller getUnmarshaller(JAXBContext jaxbContext) throws JAXBException {
        return jaxbContext.createUnmarshaller();
    }

    private ConstantLineWidthTextFileDefinition provideXMLContent() {
        return new ConstantLineWidthTextFileDefinition();
    }

    private Marshaller getMarshaller(JAXBContext jaxbContext) throws JAXBException {
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        return marshaller;
    }

    public static void main(String ... args) throws Exception {
            new WriteFileApplication().start();
        }
}
