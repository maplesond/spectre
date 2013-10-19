/*
 * Phylogenetics Tool suite
 * Copyright (C) 2013  UEA CMP Phylogenetics Group
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
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

package uk.ac.uea.cmp.phygen.netmake.weighting;

import org.apache.commons.lang3.StringUtils;
import uk.ac.uea.cmp.phygen.core.ds.distance.DistanceMatrix;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sarah_2
 * Date: 24/04/13
 * Time: 15:49
 * To change this template use File | Settings | File Templates.
 */
public enum Weightings {

    TSP {
        @Override
        public Weighting create(int size) {
            return new TSPWeighting(size);
        }

        public Weighting create(final DistanceMatrix inputData, final double weightingParam) {
            return new TSPWeighting(inputData.size());
        }
    },
    TREE {
        @Override
        public Weighting create(int size) {
            return new TreeWeighting(size);
        }

        public Weighting create(final DistanceMatrix inputData, final double weightingParam) {
            return new TreeWeighting(inputData.size(), weightingParam);
        }
    },
    EQUAL {
        @Override
        public Weighting create(int size) {
            return new EqualWeighting(size);
        }

        public Weighting create(final DistanceMatrix inputData, final double weightingParam) {
            return new EqualWeighting(inputData.size());
        }
    },
    PARABOLA {
        @Override
        public Weighting create(int size) {
            return new ParabolaWeighting(size);
        }

        public Weighting create(final DistanceMatrix inputData, final double weightingParam) {
            return new ParabolaWeighting(inputData.size());
        }
    },
    GREEDY_ME {
        @Override
        public Weighting create(int size) {
            throw new UnsupportedOperationException("Can't create GreedyME in this way");
        }

        public Weighting create(final DistanceMatrix inputData, final double weightingParam) {
            return new GreedyMEWeighting(inputData);
        }
    };

    public abstract Weighting create(final int size);

    public abstract Weighting create(final DistanceMatrix inputData, final double weightingParam);


    public static Weighting createWeighting(String weightingType, DistanceMatrix distanceMatrix, double weightingParam, final boolean greedyMEAllowed) {
        Weightings w = Weightings.valueOf(weightingType);

        if (!greedyMEAllowed && w == GREEDY_ME)
            return null;

        Weighting weighting = w.create(distanceMatrix, weightingParam);

        return weighting;
    }

    public static List<Weightings> getValuesExceptGreedyME() {

        List<Weightings> weightingsList = new ArrayList<>();

        for (Weightings weighting : Weightings.values()) {
            if (weighting != GREEDY_ME) {
                weightingsList.add(weighting);
            }
        }

        return weightingsList;
    }

    public static String toListString() {
        List<String> list = new ArrayList<>();

        for (Weightings prf : Weightings.values()) {
            list.add(prf.toString());
        }

        return "[" + StringUtils.join(list, ", ") + "]";
    }
}