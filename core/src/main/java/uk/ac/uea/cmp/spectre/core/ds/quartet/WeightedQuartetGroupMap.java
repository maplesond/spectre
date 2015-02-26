/*
 * Suite of PhylogEnetiC Tools for Reticulate Evolution (SPECTRE)
 * Copyright (C) 2014  UEA School of Computing Sciences
 *
 * This program is free software: you can redistribute it and/or modify it under the term of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package uk.ac.uea.cmp.spectre.core.ds.quartet;

import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dan on 04/12/13.
 */
public class WeightedQuartetGroupMap extends HashMap<Quartet, QuartetWeights> {

    /**
     * Creates an empty weighted quartet map
     */
    public WeightedQuartetGroupMap() {
        super();
    }

    /**
     * Creates a weighted quartet group map from a map of canonical quartets to weights
     *
     * @param qMap The map of canonical quartets mapped to single weights
     */
    public WeightedQuartetGroupMap(CanonicalWeightedQuartetMap qMap) {

        for (Map.Entry<Quartet, Double> entry : qMap.entrySet()) {

            this.put(entry.getKey(), entry.getValue());
        }

    }

    /**
     * Adds a weighted quartet into the hash map.
     *
     * @param quartet Quartet to put
     * @param weight Weight of Quartet
     * @return The previous value for this quartet (or null, if there wasn't anything)
     */
    public QuartetWeights put(Quartet quartet, double weight) {

        Pair<Quartet, Integer> keys = quartet.getGroupKeys();

        return this.put(keys.getLeft(), keys.getRight(), weight);
    }

    protected QuartetWeights put(Quartet sorted, int index, double weight) {

        QuartetWeights weights = this.get(sorted);

        if (weights != null) {
            weights.set(index, weight);
            return weights;
        } else {
            QuartetWeights qw = new QuartetWeights();
            qw.set(index, weight);

            return super.put(sorted, qw);
        }
    }


    /**
     * Already given the weights, so just dump these where they should be in the hash
     *
     * @param quartet Quartet to put
     * @param weights Weights for quartet
     */
    @Override
    public QuartetWeights put(Quartet quartet, QuartetWeights weights) {

        Quartet sorted = quartet.createSortedCopy();

        super.put(sorted, weights);

        return weights;
    }


    public void incrementWeight(Quartet q, double increment) {

        Pair<Quartet, Integer> keys = q.getGroupKeys();
        Quartet sorted = keys.getLeft();
        int index = keys.getRight();

        if (this.containsKey(sorted)) {
            this.put(sorted, this.getWeight(sorted, index) + increment);
        } else {
            this.put(sorted, index, increment);
        }
    }

    /**
     * Gets the length of a specific quartet.  If the quartet is not in the hash then simply return 0.0.
     *
     * @param q The quartet to find in the hash.
     * @return The weight of the specified quartet if found in the hash, 0.0 otherwise.
     */
    public double getWeight(Quartet q) {

        Pair<Quartet, Integer> keys = q.getGroupKeys();

        return this.getWeight(keys.getLeft(), keys.getRight());
    }

    public double getWeight(Quartet sorted, int index) {

        return this.containsKey(sorted) ? this.get(sorted).get(index + 1) : 0.0;
    }

    /**
     * Normalises all the quartet weights in this hash map
     *
     * @param logscale Normalise using natural log scale, or not
     * @param useMax   Use max or min
     */
    public void normalize(boolean logscale, boolean useMax) {

        for (QuartetWeights quartetWeights : this.values()) {
            quartetWeights.normalise(logscale, useMax);
        }
    }

}
