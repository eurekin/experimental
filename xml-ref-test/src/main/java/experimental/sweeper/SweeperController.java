package experimental.sweeper;

import pl.eurekin.experimental.Observable;
import pl.eurekin.experimental.state.*;

/**
 * @author greg.matoga@gmail.com
 */
public class SweeperController {


    private FieldElement mineField = new FieldElement();
    private ObservableState gameFinished =
            new OrState(
                    new AnyTrue(allBooms()),
                    new AllTrue(allVisited()));
    private ObservableState won =
            new AndState(
                    new AllTrue(allVisited()),
                    new AllFalse(allBooms()));

    private Observable<Boolean> allVisited() {
        return mineField().visited();
    }

    private Observable<Boolean> allBooms() {
        return mineField().boom;
    }

    public FieldElement mineField() {
        return mineField;
    }

    public ObservableState isFinished() {
        return gameFinished;
    }

    public ObservableState isWon() {
        return won;
    }

    public static class FieldElement {
        SimpleState mine = new SimpleState(false);
        private SimpleState visited = new SimpleState(false);

        public ObservableState boom = new AndState(visited, mine);

        public ObservableState visited() {
            return visited;
        }

        public void moveOn() {
            visited.set(true);
        }
    }

}
