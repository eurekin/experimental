package experimental.sweeper;

import org.junit.Test;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author greg.matoga@gmail.com
 */
public class MineFieldTest {


    @Test
    public void fieldNeighbourhoodContainsTheFieldItself() {
        MineField mineField = new MineField(1, 1);
        List<SweeperController.FieldElement> neighbours = mineField.getNeighboursOf(0, 0);

        List<SweeperController.FieldElement> emptyList = emptyList();
        assertThat(neighbours, is(equalTo(emptyList)));
    }

    @Test
    public void neighbourhoodOfElementInTheCenterOf3x3Field_Contains9Fields() {
        MineField mineField = new MineField(3, 3);
        List<SweeperController.FieldElement> neighbours = mineField.getNeighboursOf(1, 1);

        assertThat(neighbours.size(), is(equalTo(8)));
    }

    @Test
    public void neighbourhoodOfElementInTheCornerOf3x3Field_Contains4Fields() {
        MineField mineField = new MineField(3, 3);
        List<SweeperController.FieldElement> neighbours = mineField.getNeighboursOf(0, 0);

        assertThat(neighbours.size(), is(equalTo(3)));
    }

    @Test
    public void neighbourhoodOfElementAtTheTopOf3x3Field_Contains6Fields() {
        MineField mineField = new MineField(3, 3);
        List<SweeperController.FieldElement> neighbours = mineField.getNeighboursOf(1, 0);

        assertThat(neighbours.size(), is(equalTo(5)));
    }
}
