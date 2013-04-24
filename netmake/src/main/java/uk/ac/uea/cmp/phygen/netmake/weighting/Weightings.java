package uk.ac.uea.cmp.phygen.netmake.weighting;

import uk.ac.uea.cmp.phygen.core.ds.Distances;

/**
 * Created with IntelliJ IDEA.
 * User: Sarah_2
 * Date: 24/04/13
 * Time: 15:49
 * To change this template use File | Settings | File Templates.
 */
public enum Weightings {

    TSP
            {
                public Weighting create(final Distances inputData, final double weightingParam)
                {
                    return new TSPWeighting(inputData.size());
                }
            },
    TREE
            {
                public Weighting create(final Distances inputData, final double weightingParam)
                {
                    return new TreeWeighting(inputData.size(), weightingParam);
                }
            },
    EQUAL
            {
                public Weighting create(final Distances inputData, final double weightingParam)
                {
                    return new EqualWeighting(inputData.size());
                }
            },
    PARABOLA
            {
                public Weighting create(final Distances inputData, final double weightingParam)
                {
                    return new ParabolaWeighting(inputData.size());
                }
            },
    GREEDY_ME
            {
                public Weighting create(final Distances inputData, final double weightingParam)
                {
                    return new GreedyMEWeighting(inputData);
                }
            };

    public abstract Weighting create(final Distances inputData, final double weightingParam);


    public static Weighting createWeighting(String weightingType, Distances distances, double weightingParam, final boolean greedyMEAllowed)
    {
        Weightings w = Weightings.valueOf(weightingType);

        if (!greedyMEAllowed && w == GREEDY_ME)
            return null;

        Weighting weighting = w.create(distances, weightingParam);

        return weighting;
    }
}