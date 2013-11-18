/*
 * Phylogenetics Tool suite
 * Copyright (C) 2013  UEA CMP Phylogenetics Group
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

package uk.ac.uea.cmp.phygen.core.ds.split;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 18/11/13
 * Time: 13:39
 * To change this template use File | Settings | File Templates.
 */
public class SplitUtils {

    public static List<Split> createWeightedSplitList(List<SplitBlock> splitBlocks, List<Double> weights, final int nbTaxa) {

        if (splitBlocks.size() != weights.size())
            throw new IllegalArgumentException("Split blocks and weights are of different lengths.");

        List<Split> splits = new ArrayList<>();

        int i = 0;
        for(SplitBlock splitBlock : splitBlocks) {
            splits.add(new Split(splitBlock, nbTaxa, weights.get(i++)));
        }

        return splits;
    }


    public static double sumOfWeights(List<Split> splits) {

        double sum = 0.0;

        for (Split split : splits) {
            sum += split.getWeight();
        }

        return sum;
    }

    public static double meanOfWeights(List<Split> splits) {

        return sumOfWeights(splits) / (double) splits.size();
    }
}
