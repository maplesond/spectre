package uk.ac.uea.cmp.phygen.netmake.weighting;

import uk.ac.uea.cmp.phygen.core.ds.distance.DistanceMatrix;

import java.util.ArrayList;
import java.util.Arrays;
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
}