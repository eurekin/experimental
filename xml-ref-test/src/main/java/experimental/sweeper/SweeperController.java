package experimental.sweeper;

import pl.eurekin.experimental.ChangedPropertyListener;
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
                new AllTrue(allNoMineFieldsUncovered()));
        won = new AndState(
                new AllTrue(allNoMineFieldsUncovered()),
                new AllFalse(allBooms()));
    }

    private List<Observable<Boolean>> allNoMineFieldsUncovered() {
        List<Observable<Boolean>> observables = new ArrayList<Observable<Boolean>>();
        for (FieldElement e : mineField.allFields())
            observables.add(
                    new XorState(e.uncovered(), e.mine))
                    ;
        return observables;
    }

    private List<Observable<Boolean>> allBooms() {
        List<Observable<Boolean>> observables = new ArrayList<Observable<Boolean>>();
        for (FieldElement e : mineField.allFields())
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
        mineField.get(row, column).moveOn();
    }

    public static class FieldElement {
        private final SimpleState zeroMinesInNeighbourhood = new SimpleState(true);
        public ObservableState boom;
        SimpleState mine = new SimpleState(false);
        private SimpleState visited;
        private SimpleState noMineNeighbourUncovered = new SimpleState(false);
        private ObservableState uncovered;
        private StatefulObservable<Integer> neighboringMineCount;
        private FieldElement.Updater updaterCallback;
        private List<FieldElement> neighboringElements;
        private AnyTrue atLeastOneNeighbourIsUncoveredAndWithoutMines;
        private boolean imBeingMovedOn;

        public FieldElement() {
            neighboringMineCount = new StatefulObservable<Integer>(0);
            updaterCallback = new Updater();
            visited = new SimpleState(false);
            uncovered = new OrState(visited,
                    noMineNeighbourUncovered);
            boom = new AndState(visited, mine);
        }

        public void initiateNeighbourCount(List<FieldElement> neighboringElements) {
            this.neighboringElements = neighboringElements;
            for (FieldElement neighbour : neighboringElements)
                neighbour.mine.registerChangeListener(updaterCallback);
        }

        public void initiateStateWhichDependsOnNeighbourhood() {
            List<Observable<Boolean>> neighbourStates = new ArrayList<Observable<Boolean>>();
            for (FieldElement neighbour : neighboringElements)
                neighbourStates.add(
                       new AndState(neighbour.uncovered, neighbour.zeroMinesInNeighbourhood)

                );
            atLeastOneNeighbourIsUncoveredAndWithoutMines = new AnyTrue(neighbourStates);
            atLeastOneNeighbourIsUncoveredAndWithoutMines.registerChangeListener(new ChangedPropertyListener<Boolean>() {
                @Override
                public void beginNotifying() {
                }

                @Override
                public void propertyChanged(Boolean oldValue, Boolean newValue) {
                    if (newValue != oldValue)
                        noMineNeighbourUncovered.set(atLeastOneNeighbourIsUncoveredAndWithoutMines.get());
                }

                @Override
                public void finishNotifying() {
                }
            });
        }

        public ObservableState uncovered() {
            return uncovered;
        }

        private void updateCount(Boolean increment) {
            Integer currentValue = neighboringMineCount.get();
            int factor = increment ? 1 : -1;
            Integer newValue = currentValue + factor;
            neighboringMineCount.notifyOfStateChange(newValue);
            boolean zeroMinesHere = newValue == 0;
            zeroMinesInNeighbourhood.set(zeroMinesHere);
        }

        public ObservableState visited() {
            return visited;
        }

        public void moveOn() {
            visited.set(true);
        }

        public Observable<Integer> countMinesInNeighborhood() {
            return neighboringMineCount;
        }

        private class Updater implements ChangedPropertyListener<Boolean> {
            @Override
            public void beginNotifying() {

            }

            @Override
            public void propertyChanged(Boolean oldValue, Boolean newValue) {
                updateCount(newValue);
            }

            @Override
            public void finishNotifying() {
            }
        }
    }

}
