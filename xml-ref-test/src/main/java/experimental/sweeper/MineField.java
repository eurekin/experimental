package experimental.sweeper;

import pl.eurekin.experimental.state.SimpleState;

import java.util.ArrayList;
import java.util.List;

import static experimental.sweeper.SweeperController.FieldElement;
import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * @author greg.matoga@gmail.com
 */
public class MineField {

    private final int rows, cols;
    private FieldElement[][] elements;
    private List<FieldElement> allElements;

    public MineField(int x, int y) {
        rows = x;
        cols = y;
        elements = new FieldElement[x][];
        allElements = new ArrayList<FieldElement>(x * y);
        for (int i = 0; i < x; i++) {
            elements[i] = new FieldElement[y];
            for (int j = 0; j < y; j++) {
                final int ii = i, jj = j;
                FieldElement fieldElement = new FieldElement() {
                    @Override
                    public String toString() {
                        return "["+ii+", " + jj + "]";
                    }
                };
                elements[i][j] = fieldElement;
                allElements.add(fieldElement);
            }
        }

        for (int i = 0; i < x; i++)
            for (int j = 0; j < y; j++)
                elements[i][j].initiateNeighbourCount(getNeighboursOf(i, j));

        for(FieldElement eachElement : allElements)
            eachElement.initiateStateWhichDependsOnNeighbourhood();
    }

    public static int clampToRange(int minimum, int value, int maximum) {
        return min(max(minimum, value), maximum);
    }

    public FieldElement get(int x, int y) {
        return elements[x][y];
    }

    public List<FieldElement> allFields() {
        return allElements;
    }

    public SimpleState mineAt(int x, int y) {
        return elements[x][y].mine;
    }

    public List<FieldElement> getNeighboursOf(int x, int y) {
        Integer neighbourhoodRadius = 1;
        int adjustedXMin = clampToRange(0, x - neighbourhoodRadius, rows - 1);
        int adjustedXMax = clampToRange(0, x + neighbourhoodRadius, rows - 1);

        int adjustedYMin = clampToRange(0, y - neighbourhoodRadius, cols - 1);
        int adjustedYMax = clampToRange(0, y + neighbourhoodRadius, cols - 1);

        Integer precomputedSize = (adjustedXMax - adjustedXMin + 1) * (adjustedYMax - adjustedYMin + 1);
        List<FieldElement> neighbourhood = new ArrayList<FieldElement>(precomputedSize);

        for (int row = adjustedXMin; row <= adjustedXMax; row++)
            for (int col = adjustedYMin; col <= adjustedYMax; col++)
                if (!(row == x && col == y))
                    neighbourhood.add(elements[row][col]);

        return neighbourhood;
    }
}
