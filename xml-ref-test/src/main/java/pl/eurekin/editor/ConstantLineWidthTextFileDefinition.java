package pl.eurekin.editor;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@XmlRootElement
public class ConstantLineWidthTextFileDefinition {

    @XmlElement(name = "field")
    List<Field> fields = new ArrayList<Field>();
    private JAXBContext jaxbContext;
    private Marshaller marshaller;
    private Unmarshaller unmarshaller;

    void recalculateIndices() {
        Field lastField = Field.BEFORE_FIRST;
        for (Field f : fields) {
            f.isAfter(lastField);
            lastField = f;
        }
    }

    public void add(Field newField) {
        fields.add(newField);
        newField.isContainedIn(this);
        recalculateIndices();
    }

    @SuppressWarnings("unused")
    void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
        recalculateIndices();
    }

    void moveUp(Field field) {
        int index = fields.indexOf(field);
        Collections.rotate(fields.subList(index - 1, index + 1), -1);
        recalculateIndices();
    }

    void moveDown(Field field) {
        int index = fields.indexOf(field);
        Collections.rotate(fields.subList(index, index + 2), 1);
        recalculateIndices();
    }

    public void remove(Field field) {
        fields.remove(field);
    }

    public String toXml() {
        try {
            if (this.jaxbContext == null)
                this.jaxbContext = JAXBContext.newInstance(this.getClass());
            JAXBContext jaxbContext = this.jaxbContext;

            if (this.marshaller == null)
                this.marshaller = jaxbContext.createMarshaller();
            Marshaller marshaller = this.marshaller;
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);


            StringWriter sw = new StringWriter();
            marshaller.marshal(this, sw);

            return sw.toString();
        } catch (JAXBException e) {
            throw new CouldNotWriteXMLException(e);
        }
    }

    public void fromXml(String maybeXML) {
        try {
            if (this.jaxbContext == null)
                this.jaxbContext = JAXBContext.newInstance(this.getClass());
            JAXBContext jaxbContext = this.jaxbContext;

            if (this.unmarshaller == null)
                this.unmarshaller = jaxbContext.createUnmarshaller();
            Unmarshaller unmarshaller = this.unmarshaller;

            ConstantLineWidthTextFileDefinition newVal =
                    (ConstantLineWidthTextFileDefinition) unmarshaller.unmarshal(new StringReader(maybeXML));

            this.fields = newVal.fields;

        } catch (JAXBException e) {
            throw new CouldNotWriteXMLException(e);
        }
    }

    public class CouldNotWriteXMLException extends RuntimeException {
        public CouldNotWriteXMLException(Throwable throwable) {
            super(throwable);
        }
    }
}
