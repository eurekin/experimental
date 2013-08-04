package experimental.sweeper;

import pl.eurekin.experimental.Observable;
import pl.eurekin.experimental.state.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author greg.matoga@gmail.com
 */
public class SweeperController {

    private MineField mineField;
    private ObservableState gameFinished;
    private ObservableState won;

    public SweeperController(MineField mineField) {
        this.mineField = mineField;
        gameFinished = new OrState(
                new AnyTrue(allBooms()),
                new AllTrue(allVisited()));
        won = new AndState(
                new AllTrue(allVisited()),
                new AllFalse(allBooms()));
    }


    private List<Observable<Boolean>> allVisited() {
        List<Observable<Boolean>> observables = new ArrayList<Observable<Boolean>>();
        for(FieldElement e : mineField.allFields())
            observables.add(e.visited());
        return observables;
    }

    private List<Observable<Boolean>>  allBooms() {
        List<Observable<Boolean>> observables = new ArrayList<Observable<Boolean>>();
        for(FieldElement e : mineField.allFields())
            observables.add(e.boom);
        return observables;
    }

    public MineField mineField() {
        return mineField;
    }

    public ObservableState isFinished() {
        return gameFinished;
    }

    public ObservableState isWon() {
        return won;
    }

    public void moveOnFieldAt(int row, int column) {
        mineField.get(row, column).visited.set(true);
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
