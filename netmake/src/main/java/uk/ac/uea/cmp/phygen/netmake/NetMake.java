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

package uk.ac.uea.cmp.phygen.netmake;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import uk.ac.uea.cmp.phygen.core.ds.Tableau;
import uk.ac.uea.cmp.phygen.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.phygen.core.ds.split.CircularOrdering;
import uk.ac.uea.cmp.phygen.core.ds.split.CircularSplitSystem;
import uk.ac.uea.cmp.phygen.core.ds.split.CompatibleSplitSystem;
import uk.ac.uea.cmp.phygen.netmake.weighting.GreedyMEWeighting;
import uk.ac.uea.cmp.phygen.netmake.weighting.TreeWeighting;
import uk.ac.uea.cmp.phygen.netmake.weighting.Weighting;

import java.util.ArrayList;

/**
 * Performs the NeighborNet algorithm to retrieve a split system and a circular
 * order.
 *
 * @author Sarah Bastkowski
 *         See Sarah Bastkowski, 2010:
 *         <I>Algorithmen zum Finden von Bäumen in Neighbor Net Netzwerken</I>
 */
public class NetMake {

    // Input class variables
    private final DistanceMatrix distanceMatrix;
    private Weighting weighting1;
    private Weighting weighting2;
    private final RunMode mode;
    private final int NB_TAXA;

    // Temporary state variables.
    private Tableau<Integer> components;

    /**
     * Creates a new NetMake object with a distance matrix and a single weighting.
     *
     * @param distanceMatrix Distance matrix, which defines distanceMatrix between taxa
     * @param weighting      Weighting system to be applied
     */
    public NetMake(final DistanceMatrix distanceMatrix, Weighting weighting) {
        this(distanceMatrix, weighting, null);
    }

    /**
     * Creates a new NetMake object with a distance matrix and two weightings, for use
     * in a "hybrid" mode.
     *
     * @param distanceMatrix Distance matrix, which defines distanceMatrix between taxa
     * @param weighting1     First weighting system to be applied
     * @param weighting2     Second weighting system to be applied
     */
    public NetMake(final DistanceMatrix distanceMatrix, Weighting weighting1, Weighting weighting2) {
        if (distanceMatrix == null) {
            throw new NullPointerException("Must specify a Distance matrix to work with.");
        }

        this.mode = determineRunMode(weighting1, weighting2);

        if (this.mode == RunMode.UNKNOWN)
            throw new IllegalArgumentException("Unknown run mode configuration.  Please ensure the weighting configuration is valid.");

        // Set input variables
        this.distanceMatrix = distanceMatrix;
        this.weighting1 = weighting1;
        this.weighting2 = weighting2;

        this.NB_TAXA = this.distanceMatrix.size();

        // Initialise state and output variables.
        reset();
    }

    protected enum RunMode {

        UNKNOWN {
            public NetMakeResult run(NetMake nn) {
                throw new UnsupportedOperationException("Run Mode was not known");
            }
        },
        NORMAL {
            public NetMakeResult run(NetMake nn) {
                return nn.runNN();
            }
        },
        HYBRID {
            public NetMakeResult run(NetMake nn) {
                return nn.runNN();
            }
        },
        HYBRID_GREEDYME {
            public NetMakeResult run(NetMake nn) {
                return nn.runNN();
            }
        };

        public boolean isHybrid() {
            return (this == HYBRID || this == HYBRID_GREEDYME);
        }

        public abstract NetMakeResult run(NetMake nn);
    }

    public String getRunMode() {
        return this.mode.toString();
    }


    /**
     * Helper method that returns the type of run mode configuration the client
     * has requested.  If unknown, configuration then RunMode.UNKNOWN is returned.
     *
     * @param w1 First weighting to be applied.
     * @param w2 Second weighting system to be applied.
     * @return The RunMode determined by the supplied weighting systems.
     */
    private RunMode determineRunMode(Weighting w1, Weighting w2) {
        if (w1 == null) {
            return RunMode.UNKNOWN;
        } else if (w2 == null && !isGreedyMEWeighting(w1)) {
            return RunMode.NORMAL;
        } else if (isGreedyMEWeighting(w1) && !isGreedyMEWeighting(w2)) {
            return RunMode.HYBRID_GREEDYME;
        } else if (!isGreedyMEWeighting(w1) && w2 != null) {
            return RunMode.HYBRID;
        }

        return RunMode.UNKNOWN;
    }

    protected boolean isGreedyMEWeighting(Weighting w) {
        if (w == null)
            return false;

        return (w instanceof GreedyMEWeighting);
    }

    /**
     * Resets the output class variables, so the user can re-run NeighbourNet.
     */
    public final void reset() {
        this.components = initialiseComponents(this.NB_TAXA);
    }

    /**
     * Create a Tree SplitSystem and a Network SplitSystem by running the NeighborNet
     * algorithm.
     *
     * @return A tree split system
     */
    public NetMakeResult process() {

        return this.mode.run(this);
    }

    protected NetMakeResult runNN() {

        // Contains the result of the neighbornet process.
        Tableau<Integer> treeSplits = new Tableau<Integer>();
        addTrivialSplits(treeSplits);

        // DistanceMatrix between components (make deep copy from initial distance matrix)
        DistanceMatrix c2c = new DistanceMatrix(this.distanceMatrix);

        //DistanceMatrix between components and vertices (make deep copy from initial distance matrix)
        DistanceMatrix c2v = new DistanceMatrix(this.distanceMatrix);

        // If required create a new GreedyME instance.
        GreedyMEWeighting gme = this.mode == RunMode.HYBRID_GREEDYME ? new GreedyMEWeighting(this.distanceMatrix) : null;

        // Loop until components has only one entry left.
        while (components.rows() > 2) {

            Pair<Integer, Integer> selectedComponents = this.mode == RunMode.HYBRID_GREEDYME ?
                    gme.makeMECherry(treeSplits, this.components) :
                    selectionStep1(c2c);

            int sc1 = selectedComponents.getLeft();
            int sc2 = selectedComponents.getRight();

            Pair<Integer, Integer> selectedVerticies = selectionStep2(c2v, selectedComponents);

            int sv1 = selectedVerticies.getLeft();
            int sv2 = selectedVerticies.getRight();


            //merging of components
            if (components.get(sc1, 0) == sv1) {
                components.reverseRow(sc1);
            }

            if (components.get(sc2, 0) == sv2) {
                components.mergeRows(sc1, sc2);
            } else {
                components.reverseRow(sc2);
                components.mergeRows(sc1, sc2);
            }

            //add new component/split to Split list
            treeSplits.addRow((ArrayList<Integer>) components.getRow(sc1).clone());

            // Update component to component distanceMatrix (assuming not in GreedyME mode)
            if (this.mode != RunMode.HYBRID_GREEDYME)
                updateC2C(c2c, weighting1);

            // Update component to vertex distanceMatrix (also updates weighting1 params)
            updateC2V(c2v, this.mode.isHybrid() ? weighting2 : weighting1, selectedComponents);
        }

        // Remove last two rows (last row is the whole set and the last but
        // one is not required)
        // treeSplits.removeRow(treeSplits.rows() - 1);
        treeSplits.removeRow(treeSplits.rows() - 1);

        // Create ordering
        CircularOrdering permutation = this.createCircularOrdering();
        organiseSplits(treeSplits, permutation);

        // Set tree split system
        CompatibleSplitSystem tree = treeSplits.convertToSplitSystem(this.distanceMatrix, permutation);
        CircularSplitSystem network = new CircularSplitSystem(this.distanceMatrix, permutation);

        return new NetMakeResult(tree, network);
    }

    protected Pair<Integer, Integer> selectionStep1(DistanceMatrix c2c) {
        double min1 = Double.POSITIVE_INFINITY;
        double[] sum_1 = new double[this.NB_TAXA];
        double[] sum_2 = new double[this.NB_TAXA];

        int sc1 = -1;
        int sc2 = -1;

        for (int i = 0; i < components.rows(); i++) {
            sum_1[i] = 0;
            sum_2[i] = 0;
            for (int j = 0; j < components.rows(); j++) {
                if (j != i) {
                    sum_1[i] = sum_1[i] + c2c.getDistance(i, j);
                    sum_2[i] = sum_2[i] + c2c.getDistance(j, i);
                }
            }
        }

        // first Selection step for Components
        for (int i = 0; i < components.rows(); i++) {
            for (int j = i + 1; j < components.rows(); j++) {
                double qDist = (components.rows() - 2)
                        * c2c.getDistance(i, j)
                        - sum_1[i] - sum_2[j];

                if (qDist < min1) {
                    min1 = qDist;
                    sc1 = i;
                    sc2 = j;
                }
            }
        }

        return new ImmutablePair<Integer, Integer>(sc1, sc2);
    }

    protected Pair<Integer, Integer> selectionStep2(DistanceMatrix c2v, Pair<Integer, Integer> selectedComponents) {
        double min2 = Double.POSITIVE_INFINITY;
        int selectedVertex1 = -1;
        int selectedVertex2 = -1;

        int sc1 = selectedComponents.getKey();
        int sc2 = selectedComponents.getValue();


        /*
         * second selection step for vertices with
         * ComponentsVerticesSistances
         */
        for (int i = 0; i < components.rowSize(sc1); i++) {
            for (int j = 0; j < components.rowSize(sc2);
                 j++) {
                double sum1 = 0;
                double sum2 = 0;
                double sum3 = 0;
                double sum4 = 0;
                int vertex_last1 = 0;
                int vertex_last2 = 0;

                for (int k = 0; k < components.rows(); k++) {
                    if ((k != sc1)
                            && (k != sc2)) {
                        sum1 += c2v.getDistance(
                                components.get(sc1, i), k);
                    }

                    if ((k != sc1)
                            && (k != sc2)) {
                        sum2 += c2v.getDistance(
                                components.get(sc2, j), k);
                    }
                }

                for (int k = 0; k < components.rowSize(sc2);
                     k++) {
                    sum3 += distanceMatrix.getDistance(
                            components.get(sc1, i),
                            components.get(sc2, k));

                    if (components.get(sc2, k)
                            != components.get(sc2, j)) {
                        vertex_last2 = components.get(sc2, k);
                    }
                }

                for (int k = 0; k < components.rowSize(sc1);
                     k++) {
                    int vertex1 = components.get(sc1, k);

                    if (vertex1 != components.get(sc1, i)) {
                        vertex_last1 = vertex1;
                    }
                }

                sum3 += distanceMatrix.getDistance(components.get(sc2, j), vertex_last1);
                sum4 += distanceMatrix.getDistance(components.get(sc1, i), vertex_last2);

                int outerVertices1 = 0;
                if (components.rowSize(sc1) == 1) {
                    outerVertices1 = 1;
                } else {
                    outerVertices1 = 2;
                }

                int outerVertices2 = 0;
                if (components.rowSize(sc2) == 1) {
                    outerVertices2 = 1;
                } else {
                    outerVertices2 = 2;
                }

                double qDist = (components.rows() - 4 + outerVertices1
                        + outerVertices2) * distanceMatrix.getDistance(components.get(sc1, i),
                        components.get(sc2, j))
                        - sum1 - sum2 - sum3 - sum4;

                if (qDist < min2) {
                    min2 = qDist;
                    selectedVertex1 = components.get(sc1, i);
                    selectedVertex2 = components.get(sc2, j);
                }

                j = (j == components.rowSize(sc2) - 1) ? components.rowSize(sc2) : components.rowSize(sc2) - 2;
            }

            i = (i == components.rowSize(sc1) - 1) ? components.rowSize(sc1) : components.rowSize(sc1) - 2;
        }

        return new ImmutablePair<Integer, Integer>(selectedVertex1, selectedVertex2);
    }

    protected void updateC2C(DistanceMatrix c2c, Weighting w) {
        for (int i = 0; i < components.rows(); i++) {
            for (int j = 0; j < components.rows(); j++) {
                if (i == j) {
                    c2c.setDistance(i, j, 0.);
                } else {
                    double aComponentDistance = 0.;

                    for (int k = 0; k < components.rowSize(i); k++) {
                        for (int m = 0; m < components.rowSize(j); m++) {
                            int vertex1 = components.get(i, k);
                            int vertex2 = components.get(j, m);
                            double vertexDistance = w.getWeightingParam(vertex1)
                                    * w.getWeightingParam(vertex2)
                                    * distanceMatrix.getDistance(vertex1, vertex2);

                            aComponentDistance += vertexDistance;
                        }
                    }
                    c2c.setDistance(i, j, aComponentDistance);
                }
            }
        }
    }

    protected void updateC2V(DistanceMatrix c2v, Weighting w, Pair<Integer, Integer> selectedComponents) {

        int position = -1;
        int sc1 = selectedComponents.getKey();
        int componentSplitPosition = components.rowSize(sc1);

        for (int i = 0; i < distanceMatrix.size(); i++) {
            for (int j = 0; j < components.rowSize(sc1);
                 j++) {
                if (i == components.get(sc1, j)) {
                    position = j;

                    if (w instanceof TreeWeighting) {
                        w.updateWeightingParam(i, position,
                                componentSplitPosition);
                    } else {
                        w.updateWeightingParam(i, position,
                                components.rowSize(sc1));
                    }
                }
            }
            for (int j = 0; j < components.rows(); j++) {
                double aComponentVertexDistance = 0.;
                int k = 0;

                while (k < components.rowSize(j)) {
                    if (components.get(j, k) == i) {
                        aComponentVertexDistance = 0.;
                        k = components.rowSize(j);
                    } else {
                        int vertex1 = i;
                        int vertex2 = components.get(j, k);
                        double vertexDistance = w.getWeightingParam(vertex2)
                                * distanceMatrix.getDistance(vertex1, vertex2);

                        aComponentVertexDistance += vertexDistance;
                        k++;
                    }
                }
                c2v.setDistance(i, j, aComponentVertexDistance);
            }
        }
    }

    protected void organiseSplits(Tableau<Integer> splits, CircularOrdering permutation) {

        CircularOrdering permutationInvert = permutation.invertOrdering();

        for (int i = 0; i < splits.rows(); i++) {
            int k = permutationInvert.getAt(splits.get(i, 0));
            int l = permutationInvert.getAt(splits.get(i, splits.rowSize(i) - 1));

            if (l < k) {
                splits.reverseRow(i);
            }
        }
    }

    protected final Tableau<Integer> initialiseComponents(final int size) {
        Tableau<Integer> c = new Tableau<Integer>();

        for (int i = 0; i < size; i++) {
            c.addRow(i);
        }

        return c;
    }

    protected CircularOrdering createCircularOrdering() {

        ArrayList<Integer> help = new ArrayList<Integer>();
        for (int j = 0; j < 2; j++) {
            for (int i = 0; i < components.rowSize(j); i++) {
                help.add(components.get(j, i));
            }
        }
        int[] permutation = new int[help.size()];
        for (int i = 0; i < help.size(); i++) {
            permutation[i] = help.get(i) + 1;
        }

        return new CircularOrdering(permutation);
    }

    protected void addTrivialSplits(Tableau<Integer> splits) {
        for (int i = 0; i < this.NB_TAXA; i++) {
            splits.addRow(i);
        }
    }

}
