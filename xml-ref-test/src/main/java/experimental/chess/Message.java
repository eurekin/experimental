package experimental.chess;

import org.eclipse.persistence.oxm.XMLRoot;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * @author greg.matoga@gmail.com
*/
@XmlRootElement
@XmlType(propOrder={"type", "data"})
public class Message {

    private String type;

    private List<Data> data;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Message{" +
                "data=" + data +
                ", type='" + type + '\'' +
                '}';
    }
}
