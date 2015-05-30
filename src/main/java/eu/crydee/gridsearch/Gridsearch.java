package eu.crydee.gridsearch;

import java.util.Arrays;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Class to perform a grid search to estimate optimal parameters given an
 * objective function.
 *
 * @author Hugo “m09” Mougard
 */
public class Gridsearch {

    private final static Logger logger = Logger.getLogger(
            Gridsearch.class.getName());

    /**
     * Entry point. Performs a grid search given the necessary information. See
     * the README or the unit test for a basic example of how to optimize f = aX
     * - bY + -c(Y - 2)² with a in [0, 4], b in [0, 5] and c in [0, 6]
     *
     * @param max the maximum of each parameter
     * @param min the minimum of each parameter
     * @param zones the number of zones to split each dimension in at each step
     * @param steps the number of iterations to run before returning a result
     * @param scorer the scoring function
     * @param objective whether we should minimize or maximize the objective
     * @return the grid search guess for the parameters
     */
    public double[] estimate(
            double[] max,
            double[] min,
            int zones,
            int steps,
            Function<double[], Double> scorer,
            Objective objective) {
        if (max == null || min == null) {
            throw new IllegalArgumentException("You have to provide non-null "
                    + "max & min boudaries for each parameter.");
        }
        if (max.length != min.length) {
            throw new IllegalArgumentException("Max & min boudaries should be "
                    + "of equal lenghts.");
        }
        int l = max.length;
        double[] currentSteps = new double[l],
                currentMin = new double[l],
                currentMax = new double[l],
                bestValues = new double[l],
                values = new double[l];
        int[] state = new int[l],
                maxState = new int[l];
        Arrays.fill(maxState, zones);
        System.arraycopy(min, 0, currentMin, 0, l);
        System.arraycopy(max, 0, currentMax, 0, l);
        double bestScore = objective == Objective.MAXIMIZE
                ? Double.NEGATIVE_INFINITY
                : Double.POSITIVE_INFINITY;
        for (int s = 0; s < steps; s++) {
            // compute our steps
            for (int i = 0; i < l; i++) {
                currentSteps[i] = (currentMax[i] - currentMin[i]) / zones;
            }
            logger.log(
                    Level.INFO,
                    "State at start of step {0}: {1}",
                    new Object[]{
                        s,
                        IntStream.range(0, l)
                        .mapToObj(i -> Stream.of(
                                        currentMin[i],
                                        currentMax[i],
                                        currentSteps[i])
                                .map(d -> Double.toString(d))
                                .collect(Collectors.joining(", ", "(", ")")))
                        .collect(Collectors.joining(", ", "[", "]"))
                    });
            // initial state of the search during this step
            Arrays.fill(state, 0);
            // looping to go through all states to do in this step
            while (true) {
                // computing the values we obtain at this state
                for (int i = 0; i < l; i++) {
                    values[i] = currentMin[i] + currentSteps[i] * state[i];
                }
                // score those values
                double score = scorer.apply(values);
                // and compare them with our previous best guess
                if (objective == Objective.MAXIMIZE
                        && score > bestScore
                        || objective == Objective.MINIMIZE
                        && score < bestScore) {
                    bestScore = score;
                    System.arraycopy(values, 0, bestValues, 0, l);
                }
                // we then compute the next state to consider
                // it's order dependent, move with care
                if (Arrays.equals(state, maxState)) {
                    break;
                }
                for (int j = l - 1; j >= 0; j--) {
                    if (state[j] != zones) {
                        state[j] = state[j] + 1;
                        for (int k = j + 1; k < l; k++) {
                            state[k] = 0;
                        }
                        break;
                    }
                }
            }
            // once we're finished for a particular step, we set up the next
            // boundaries
            for (int i = 0; i < l; i++) {
                currentMin[i] = Math.max(
                        min[i],
                        bestValues[i] - currentSteps[i]);
                currentMax[i] = Math.min(
                        max[i],
                        bestValues[i] + currentSteps[i]);
            }
        }
        return bestValues;
    }

    /**
     * Enum to represent the two types of objective functions we manage.
     */
    public static enum Objective {

        /**
         * Minimization objective.
         */
        MINIMIZE,
        /**
         * Maximization objective.
         */
        MAXIMIZE
    }
}
