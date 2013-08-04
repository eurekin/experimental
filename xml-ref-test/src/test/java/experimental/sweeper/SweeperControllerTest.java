package experimental.sweeper;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author greg.matoga@gmail.com
 */
public class SweeperControllerTest {

    private SweeperController sweeperController;
    private MineField mineField;

    @Before
    public void setUp() throws Exception {
        mineField = new MineField(1, 1);
        sweeperController = new SweeperController(mineField);
    }

    @Test
    public void oneFieldGameNoMinesWins() {
        mineField.mineAt(0, 0).set(false);
        sweeperController.moveOnFieldAt(0, 0);

        assertWon();
    }

    @Test
    public void oneFieldGameWithMineLoses() {
        mineField.mineAt(0, 0).set(true);
        sweeperController.moveOnFieldAt(0, 0);

        assertLost();
    }

    private void assertLost() {
        assertFinished();
        assertThat(sweeperController.isWon().get(), is(false));
    }

    private void assertWon() {
        assertFinished();
        assertThat(sweeperController.isWon().get(), is(true));
    }

    private void assertFinished() {
        assertThat(sweeperController.isFinished().get(), is(true));
    }
}
