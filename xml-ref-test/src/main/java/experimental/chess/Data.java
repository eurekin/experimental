package experimental.chess;

/**
 * @author greg.matoga@gmail.com
 */

public class Data {
    private int id;
    private String color;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Data{" +
                "color='" + color + '\'' +
                ", id=" + id +
                '}';
    }
}
