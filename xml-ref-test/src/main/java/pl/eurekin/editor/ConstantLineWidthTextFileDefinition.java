package pl.eurekin.editor;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

@XmlRootElement
public class ConstantLineWidthTextFileDefinition {

    @XmlElement(name = "field")
    final
    List<Field> fields = new ArrayList<>();

    void recalculateIndices() {
        Field lastField = Field.BEFORE_FIRST;
        for(Field f : fields) {
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
}
