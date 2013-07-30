package pl.eurekin.editor;

import pl.eurekin.experimental.GenerateJavaBeanInterface;

import javax.xml.bind.annotation.XmlAttribute;

@GenerateJavaBeanInterface()
public class Field {

    @XmlAttribute
    Integer length = 1;

    @XmlAttribute
    String name = "name";

    Integer begin = 0;
    Integer end;
    private ConstantLineWidthTextFileDefinition parent;

    public void shrink() {
        this.length -= 1;
        parent.recalculateIndices();
    }

    public void grow() {
        this.length += 1;
        parent.recalculateIndices();
    }

    public void action(int Input) {
        System.out.println("Action fired!!!");
    }

    public void remove() {
        parent.remove(this);
    }

    public void moveUp() {
        parent.moveUp(this);
    }

    public void moveDown() {
        parent.moveDown(this);
    }

    public int action(int input1, int input2) {
        System.out.println("Action fired!!!");
        return 4;
    }

    public boolean canShrink() {
        return this.length >=2;
    }

    public void isAfter(Field f) {
        begin = f.end;
        end = begin + length;
    }

    public static final Field BEFORE_FIRST = new BeforeFirst();

    public void isContainedIn(ConstantLineWidthTextFileDefinition parent) {
        this.parent = parent;
    }

    public static class BeforeFirst extends Field {
        public BeforeFirst() {
            begin = 0;
            length = 0;
            end = 0;
        }
    }
}
