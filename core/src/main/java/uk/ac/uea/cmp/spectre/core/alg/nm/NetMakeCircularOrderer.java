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

package uk.ac.uea.cmp.spectre.core.alg.nm;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import uk.ac.uea.cmp.spectre.core.alg.CircularOrderingCreator;
import uk.ac.uea.cmp.spectre.core.alg.nm.weighting.GreedyMEWeighting;
import uk.ac.uea.cmp.spectre.core.alg.nm.weighting.TreeWeighting;
import uk.ac.uea.cmp.spectre.core.alg.nm.weighting.Weighting;
import uk.ac.uea.cmp.spectre.core.ds.IdentifierList;
import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.spectre.core.ds.distance.FlexibleDistanceMatrix;
import uk.ac.uea.cmp.spectre.core.ds.split.SpectreSplitSystem;
import uk.ac.uea.cmp.spectre.core.ds.split.SplitBlock;
import uk.ac.uea.cmp.spectre.core.ds.split.SplitSystem;
import uk.ac.uea.cmp.spectre.core.ds.split.SplitUtils;

import java.util.ArrayList;

/**
 * Created by dan on 07/09/14.
 */
public class NetMakeCircularOrderer implements CircularOrderingCreator {

    protected enum RunMode {

        UNKNOWN,
        NORMAL,
        HYBRID,
        HYBRID_GREEDYME;

        public boolean isHybrid() {
            return (this == HYBRID || this == HYBRID_GREEDYME);
        }
    }

    protected static boolean isGreedyMEWeighting(Weighting w) {
        if (w == null)
            return false;

        return (w instanceof GreedyMEWeighting);
    }

    public static RunMode getRunMode(Weighting weighting1, Weighting weighting2) {

        if (weighting1 == null) {
            return RunMode.UNKNOWN;
        } else if (weighting2 == null && !isGreedyMEWeighting(weighting1)) {
            return RunMode.NORMAL;
        } else if (isGreedyMEWeighting(weighting1) && !isGreedyMEWeighting(weighting2)) {
            return RunMode.HYBRID_GREEDYME;
        } else if (!isGreedyMEWeighting(weighting1) && weighting2 != null) {
            return RunMode.HYBRID;
        }

        return RunMode.UNKNOWN;
    }

    private Weighting weighting1;
    private Weighting weighting2;
    private RunMode runMode;

    private SplitSystem treeSplits;

    public NetMakeCircularOrderer(Weighting weighting1, Weighting weighting2) {

        this.weighting1 = weighting1;
        this.weighting2 = weighting2;

        this.runMode = getRunMode(this.weighting1, this.weighting2);

        this.treeSplits = null;
    }


    @Override
    public IdentifierList createCircularOrdering(final DistanceMatrix distanceMatrix) {

        final IdentifierList taxa = distanceMatrix.getTaxa();


        // Creates a set of trivial splits for ...
        SplitSystem components = new SpectreSplitSystem(taxa, SplitUtils.createTrivialSplits(taxa, 1.0));

        // Creates a simple split system with trivial splits from the given distance matrix
        this.treeSplits = new SpectreSplitSystem(taxa, SplitUtils.createTrivialSplits(taxa, 1.0));

        // DistanceMatrix between components (make deep copy from initial distance matrix)
        DistanceMatrix c2c = new FlexibleDistanceMatrix(distanceMatrix);

        //DistanceMatrix between components and vertices (make deep copy from initial distance matrix)
        DistanceMatrix c2v = new FlexibleDistanceMatrix(distanceMatrix);

        // If required create a new GreedyME instance.
        GreedyMEWeighting gme = runMode == RunMode.HYBRID_GREEDYME ?
                new GreedyMEWeighting(distanceMatrix) :
                null;

        // Loop until components has only one entry left.
        while (components.size() > 2) {

            // The pair should be splits with the shortest tree length between them
            Pair<Integer, Integer> selectedComponents = runMode == RunMode.HYBRID_GREEDYME ?
                    gme.makeMECherry(treeSplits, components) :
                    selectionStep1(c2c, components);

            int sc1 = selectedComponents.getLeft();
            int sc2 = selectedComponents.getRight();

            Pair<Integer, Integer> selectedVerticies = selectionStep2(c2v, selectedComponents, components, distanceMatrix);

            int sv1 = selectedVerticies.getLeft();
            int sv2 = selectedVerticies.getRight();

            SplitBlock sc1Split = components.get(sc1).getASide();
            SplitBlock sc2Split = components.get(sc2).getASide();

            // Merging of components
            if (sc1Split.getFirst() == sv1) {
                sc1Split.reverse();
            }

            if (sc2Split.getFirst() == sv2) {
                components.mergeSplits(sc1, sc2);
            } else {
                sc2Split.reverse();
                components.mergeSplits(sc1, sc2);
            }

            // Add new component/split to Split list
            treeSplits.add(components.get(sc1).copy());

            // Update component to component distanceMatrix (assuming not in GreedyME mode)
            if (runMode != RunMode.HYBRID_GREEDYME)
                updateC2C(
                        c2c,
                        weighting1,
                        components,
                        distanceMatrix);

            // Update component to vertex distanceMatrix (also updates weighting1 params)
            updateC2V(
                    c2v,
                    runMode.isHybrid() ? weighting2 : weighting1,
                    selectedComponents,
                    components,
                    distanceMatrix);
        }

        // Remove last row (last row is the whole set and the last but one is not required)
        treeSplits.removeLastSplit();

        // Create ordering
        IdentifierList permutation = this.createCircularOrdering(components);

        return permutation;
    }

    @Override
    public boolean createsTreeSplits() {
        return true;
    }

    @Override
    public SplitSystem getTreeSplits() {
        return this.treeSplits;
    }

    protected Pair<Integer, Integer> selectionStep1(final DistanceMatrix c2c, final SplitSystem components) {

        final int nbTaxa = c2c.size();
        final int nbSplits = components.size();

        double min1 = Double.POSITIVE_INFINITY;
        double[] sum1 = new double[nbTaxa];
        double[] sum2 = new double[nbTaxa];

        int sc1 = 0;
        int sc2 = 0;

        for (int i = 0; i < nbSplits; i++) {
            sum1[i] = 0;
            sum2[i] = 0;
            for (int j = 0; j < nbSplits; j++) {
                if (j != i) {
                    sum1[i] = sum1[i] + c2c.getDistance(i + 1, j + 1);
                    sum2[i] = sum2[i] + c2c.getDistance(j + 1, i + 1);
                }
            }
        }

        // first Selection step for Components
        for (int i = 0; i < nbSplits; i++) {
            for (int j = i + 1; j < nbSplits; j++) {
                double qDist = (nbSplits - 2)
                        * c2c.getDistance(i + 1, j + 1)
                        - sum1[i] - sum2[j];

                if (qDist < min1) {
                    min1 = qDist;
                    sc1 = i + 1;
                    sc2 = j + 1;
                }
            }
        }

        return new ImmutablePair<>(sc1, sc2);
    }

    protected Pair<Integer, Integer> selectionStep2(DistanceMatrix c2v,
                                                    Pair<Integer, Integer> selectedComponents,
                                                    final SplitSystem components,
                                                    final DistanceMatrix distanceMatrix) {
        double min2 = Double.POSITIVE_INFINITY;
        int selectedVertex1 = -1;
        int selectedVertex2 = -1;

        int sc1 = selectedComponents.getKey();
        int sc2 = selectedComponents.getValue();

        SplitBlock splitBlockSc1 = components.get(sc1).getASide();
        SplitBlock splitBlockSc2 = components.get(sc2).getASide();

        /*
         * second selection step for vertices with
         * ComponentsVerticesDistances
         */
        for (int i = 0; i < splitBlockSc1.size(); i++) {
            for (int j = 0; j < splitBlockSc2.size(); j++) {

                double sum1 = 0;
                double sum2 = 0;
                double sum3 = 0;
                double sum4 = 0;
                int vertex_last1 = 1;
                int vertex_last2 = 1;

                for (int k = 0; k < components.size(); k++) {
                    if ((k != sc1) && (k != sc2)) {
                        sum1 += c2v.getDistance(splitBlockSc1.get(i), k + 1);
                    }

                    if ((k != sc1) && (k != sc2)) {
                        sum2 += c2v.getDistance(splitBlockSc2.get(j), k + 1);
                    }
                }

                for (int k = 0; k < splitBlockSc2.size(); k++) {
                    sum3 += distanceMatrix.getDistance(
                            splitBlockSc1.get(i),
                            splitBlockSc2.get(k));

                    if (splitBlockSc2.get(k) != splitBlockSc2.get(j)) {
                        vertex_last2 = splitBlockSc2.get(k);
                    }
                }

                for (int k = 0; k < splitBlockSc1.size(); k++) {
                    int vertex1 = splitBlockSc1.get(k);

                    if (vertex1 != splitBlockSc1.get(i)) {
                        vertex_last1 = vertex1;
                    }
                }

                sum3 += distanceMatrix.getDistance(splitBlockSc2.get(j), vertex_last1);
                sum4 += distanceMatrix.getDistance(splitBlockSc1.get(i), vertex_last2);

                int outerVertices1 = splitBlockSc1.size() == 1 ? 1 : 2;
                int outerVertices2 = splitBlockSc2.size() == 1 ? 1 : 2;

                final double totalSum = sum1 - sum2 - sum3 - sum4;
                final double topPart = components.size() - 4 + outerVertices1
                        + outerVertices2;

                double qDist = topPart * distanceMatrix.getDistance(splitBlockSc1.get(i), splitBlockSc2.get(j)) - totalSum;

                if (qDist < min2) {
                    min2 = qDist;
                    selectedVertex1 = splitBlockSc1.get(i);
                    selectedVertex2 = splitBlockSc2.get(j);
                }

                j = (j == splitBlockSc2.size() - 1) ? splitBlockSc2.size() : splitBlockSc2.size() - 2;
            }

            i = (i == splitBlockSc1.size() - 1) ? splitBlockSc1.size() : splitBlockSc1.size() - 2;
        }

        return new ImmutablePair<>(selectedVertex1, selectedVertex2);
    }

    protected void updateC2C(DistanceMatrix c2c, Weighting w, SplitSystem components, DistanceMatrix distanceMatrix) {

        final int nbSplits = components.size();

        for (int i = 0; i < nbSplits; i++) {
            for (int j = 0; j < nbSplits; j++) {
                if (i == j) {
                    c2c.setDistance(i + 1, j + 1, 0.0);
                } else {
                    double aComponentDistance = 0.0;

                    SplitBlock sbI = components.get(i).getASide();
                    SplitBlock sbJ = components.get(j).getASide();

                    for (int k = 0; k < sbI.size(); k++) {
                        for (int m = 0; m < sbJ.size(); m++) {
                            int vertex1 = sbI.get(k);
                            int vertex2 = sbJ.get(m);
                            double vertexDistance = w.getWeightingParam(vertex1 - 1)
                                    * w.getWeightingParam(vertex2 - 1)
                                    * distanceMatrix.getDistance(vertex1, vertex2);

                            aComponentDistance += vertexDistance;
                        }
                    }
                    c2c.setDistance(i + 1, j + 1, aComponentDistance);
                }
            }
        }
    }

    protected void updateC2V(DistanceMatrix c2v, Weighting w, Pair<Integer, Integer> selectedComponents, SplitSystem components, DistanceMatrix distanceMatrix) {

        int position = -1;
        final int sc1 = selectedComponents.getKey();

        SplitBlock sb1 = components.get(sc1).getASide();
        int componentSplitPosition = sb1.size();

        for (int i = 0; i < distanceMatrix.size(); i++) {
            for (int j = 0; j < sb1.size(); j++) {
                if (i == sb1.get(j) - 1) {
                    position = j;

                    if (w instanceof TreeWeighting) {
                        w.updateWeightingParam(i, position,
                                componentSplitPosition);
                    } else {
                        w.updateWeightingParam(i, position,
                                sb1.size());
                    }
                }
            }
            for (int j = 0; j < components.size(); j++) {
                double aComponentVertexDistance = 0.;
                int k = 0;
                SplitBlock sbJ = components.get(j).getASide();

                while (k < sbJ.size()) {
                    if (sbJ.get(k) - 1 == i) {
                        aComponentVertexDistance = 0.;
                        k = sbJ.size();
                    } else {
                        int vertex1 = i + 1;
                        int vertex2 = sbJ.get(k);
                        double vertexDistance = w.getWeightingParam(vertex2 - 1)
                                * distanceMatrix.getDistance(vertex1, vertex2);

                        aComponentVertexDistance += vertexDistance;
                        k++;
                    }
                }
                c2v.setDistance(i + 1, j + 1, aComponentVertexDistance);
            }
        }
    }

    protected IdentifierList createCircularOrdering(SplitSystem components) {

        ArrayList<Integer> help = new ArrayList<>();
        for (int j = 0; j < 2; j++) {
            SplitBlock sbJ = components.get(j).getASide();
            for (int i = 0; i < sbJ.size(); i++) {
                help.add(sbJ.get(i));
            }
        }
        int[] permutation = new int[help.size()];
        for (int i = 0; i < help.size(); i++) {
            permutation[i] = help.get(i);
        }

        return new IdentifierList(permutation);
    }
}
