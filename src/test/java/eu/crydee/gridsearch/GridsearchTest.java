/*
 * Copyright 2015 m09.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.crydee.gridsearch;

import java.util.stream.IntStream;
import static org.junit.Assert.*;

/**
 *
 * @author m09
 */
public class GridsearchTest {

    /**
     * Test of estimate method, of class Gridsearch.
     */
    @org.junit.Test
    public void testEstimate() {
        Gridsearch gridsearch = new Gridsearch();
        final int zones = 10,
                steps = 3;
        final double[] mins = new double[]{0d, 0d, 0d},
                maxs = new double[]{4d, 5d, 6d},
                precisions = IntStream.range(0, mins.length)
                .mapToDouble(i
                        -> (maxs[i] - mins[i]) / Math.pow(zones / 2, steps) / 2)
                .toArray(),
                gold = new double[]{4d, 0d, 2d};
        final double[] result = gridsearch.estimate(
                maxs,
                mins,
                zones,
                steps,
                s -> s[0] - s[1] - Math.pow(s[2] - 2, 2),
                Gridsearch.Objective.MAXIMIZE);
        IntStream.range(0, result.length)
                .forEach(i -> assertTrue(
                                Math.abs(gold[i] - result[i])
                                <= precisions[i]));
    }
}
