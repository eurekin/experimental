package pl.eurekin.editor;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@XmlRootElement
public class ConstantLineWidthTextFileDefinition {

    @XmlElement(name = "field")
    final
    List<Field> fields = new ArrayList<Field>();

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

    void moveUp(Field field) {
        int index = fields.indexOf(field);
        Collections.rotate(fields.subList(index-1,index + 1), -1);
        recalculateIndices();
    }

    void moveDown(Field field) {
        int index = fields.indexOf(field);
        Collections.rotate(fields.subList(index,index + 2), 1);
        recalculateIndices();
    }

    public void remove(Field field) {
        fields.remove(field);
    }
}
