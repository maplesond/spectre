/*
 * Suite of PhylogEnetiC Tools for Reticulate Evolution (SPECTRE)
 * Copyright (C) 2017  UEA School of Computing Sciences
 *
 * This program is free software: you can redistribute it and/or modify it under the term of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package uk.ac.uea.cmp.spectre.core.ds.split.circular.ordering.nm.weighting;

import org.apache.commons.lang3.StringUtils;
import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceMatrix;

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
        public Weighting create() {
            return new TSPWeighting();
        }

        public Weighting create(final DistanceMatrix inputData, final double weightingParam) {
            return new TSPWeighting();
        }
    },
    TREE {
        @Override
        public Weighting create() {
            return new TreeWeighting(0.5);
        }

        public Weighting create(final DistanceMatrix inputData, final double weightingParam) {
            return new TreeWeighting(weightingParam);
        }
    },
    EQUAL {
        @Override
        public Weighting create() {
            return new EqualWeighting();
        }

        public Weighting create(final DistanceMatrix inputData, final double weightingParam) {
            return new EqualWeighting();
        }
    },
    PARABOLA {
        @Override
        public Weighting create() {
            return new ParabolaWeighting();
        }

        public Weighting create(final DistanceMatrix inputData, final double weightingParam) {
            return new ParabolaWeighting();
        }
    },
    GREEDY_ME {
        @Override
        public Weighting create() {
            throw new UnsupportedOperationException("Can't create GreedyME in this way");
        }

        public Weighting create(final DistanceMatrix inputData, final double weightingParam) {
            return new GreedyMEWeighting(inputData);
        }
    };

    public abstract Weighting create();

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

    public static String[] stringValuesWithNone() {

        String[] values = new String[values().length + 1];

        values[0] = "NONE";

        int i = 1;
        for (Weightings weighting : values()) {
            values[i++] = weighting.toString();
        }

        return values;
    }
}