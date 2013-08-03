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

    @Before
    public void setUp() throws Exception {
        sweeperController = new SweeperController();
    }

    @Test
    public void oneFieldGameNoMinesWins() {
        sweeperController.mineField().mine.set(false);

        sweeperController.mineField().moveOn();

        assertThat(sweeperController.isFinished().get(), is(true));
        assertThat(sweeperController.isWon().get(), is(true));

    }

    @Test
    public void oneFieldGameWithMineLoses() {
        sweeperController.mineField().mine.set(true);

        sweeperController.mineField().moveOn();

        assertThat(sweeperController.isFinished().get(), is(true));
        assertThat(sweeperController.isWon().get(), is(false));
    }
}
