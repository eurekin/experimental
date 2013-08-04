package experimental.sweeper;

import org.junit.Test;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author greg.matoga@gmail.com
 */
public class MineFieldTest {



    @Test
    public void fieldNeighbourhoodContainsTheFieldItself() {
        MineField mineField = new MineField(1,1);
        List<SweeperController.FieldElement> neighbours = mineField.getNeighboursOf(0, 0);

        assertThat(neighbours, is(equalTo(singletonList(mineField.get(0, 0)))));
    }

    @Test
    public void neighbourhoodOfElementInTheCenterOf3x3Field_Contains9Fields() {
        MineField mineField = new MineField(3,3);
        List<SweeperController.FieldElement> neighbours = mineField.getNeighboursOf(1, 1);

        assertThat(neighbours.size(), is(equalTo(9)));
    }

    @Test
    public void neighbourhoodOfElementInTheCornerOf3x3Field_Contains4Fields() {
        MineField mineField = new MineField(3,3);
        List<SweeperController.FieldElement> neighbours = mineField.getNeighboursOf(0, 0);

        assertThat(neighbours.size(), is(equalTo(4)));
    }

    @Test
    public void neighbourhoodOfElementAtTheTopOf3x3Field_Contains6Fields() {
        MineField mineField = new MineField(3,3);
        List<SweeperController.FieldElement> neighbours = mineField.getNeighboursOf(1, 0);

        assertThat(neighbours.size(), is(equalTo(6)));
    }
}
