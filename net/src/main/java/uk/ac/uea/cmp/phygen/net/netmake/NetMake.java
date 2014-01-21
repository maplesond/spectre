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

package uk.ac.uea.cmp.phygen.net.netmake;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.phygen.core.ds.Tableau;
import uk.ac.uea.cmp.phygen.core.ds.Taxa;
import uk.ac.uea.cmp.phygen.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.phygen.core.ds.split.*;
import uk.ac.uea.cmp.phygen.core.io.PhygenDataType;
import uk.ac.uea.cmp.phygen.core.io.PhygenReaderFactory;
import uk.ac.uea.cmp.phygen.core.ui.gui.RunnableTool;
import uk.ac.uea.cmp.phygen.net.netmake.weighting.GreedyMEWeighting;
import uk.ac.uea.cmp.phygen.net.netmake.weighting.TreeWeighting;
import uk.ac.uea.cmp.phygen.net.netmake.weighting.Weighting;
import uk.ac.uea.cmp.phygen.net.netmake.weighting.Weightings;

import java.util.ArrayList;

/**
 * Performs the NeighborNet algorithm to retrieve a split system and a circular
 * order.
 *
 * @author Sarah Bastkowski
 *         See Sarah Bastkowski, 2010:
 *         <I>Algorithmen zum Finden von Bäumen in Neighbor Net Netzwerken</I>
 */
public class NetMake extends RunnableTool {

    private static Logger log = LoggerFactory.getLogger(NetMake.class);



    private NetMakeOptions options;

    public NetMake() {
        this(new NetMakeOptions());
    }

    public NetMake(NetMakeOptions options) {
        this.options = options;
    }

    public NetMakeOptions getOptions() {
        return options;
    }

    public void setOptions(NetMakeOptions options) {
        this.options = options;
    }

    public NetMakeResult runNN(NetMakeOptions options) {

        final DistanceMatrix distanceMatrix = options.getDistanceMatrix();
        final Taxa taxa = options.getDistanceMatrix().getTaxa();

        // Creates a set of trivial splits for ...
        SplitSystem components = new SimpleSplitSystem(taxa, SplitUtils.createTrivialSplits(taxa, 1.0));

        // Creates a simple split system with trivial splits from the given distance matrix
        SplitSystem treeSplits = new SimpleSplitSystem(taxa, SplitUtils.createTrivialSplits(taxa, 1.0));

        // DistanceMatrix between components (make deep copy from initial distance matrix)
        DistanceMatrix c2c = new DistanceMatrix(distanceMatrix);

        //DistanceMatrix between components and vertices (make deep copy from initial distance matrix)
        DistanceMatrix c2v = new DistanceMatrix(distanceMatrix);

        // If required create a new GreedyME instance.
        GreedyMEWeighting gme = options.getRunMode() == NetMakeOptions.RunMode.HYBRID_GREEDYME ?
                new GreedyMEWeighting(distanceMatrix) :
                null;

        // Loop until components has only one entry left.
        while (components.getNbSplits() > 2) {

            // The pair should be splits with the shortest tree length between them
            Pair<Integer, Integer> selectedComponents = options.getRunMode() == NetMakeOptions.RunMode.HYBRID_GREEDYME ?
                    gme.makeMECherry(treeSplits, components) :
                    selectionStep1(c2c, components);

            int sc1 = selectedComponents.getLeft();
            int sc2 = selectedComponents.getRight();

            Pair<Integer, Integer> selectedVerticies = selectionStep2(c2v, selectedComponents, components, distanceMatrix);

            int sv1 = selectedVerticies.getLeft();
            int sv2 = selectedVerticies.getRight();

            SplitBlock sc1Split = components.getSplitAt(sc1).getASide();
            SplitBlock sc2Split = components.getSplitAt(sc2).getASide();

            // Merging of components
            if (sc1Split.getFirst() == sv1) {
                sc1Split.reverse();
            }

            if (sc2Split.getFirst() == sv2) {
                components.mergeSplits(sc1, sc2);
            }
            else {
                sc2Split.reverse();
                components.mergeSplits(sc1, sc2);
            }

            // Add new component/split to Split list
            treeSplits.addSplit(components.getSplitAt(sc1).copy());

            // Update component to component distanceMatrix (assuming not in GreedyME mode)
            if (options.getRunMode() != NetMakeOptions.RunMode.HYBRID_GREEDYME)
                updateC2C(
                        c2c,
                        options.getWeighting1(),
                        components,
                        distanceMatrix);

            // Update component to vertex distanceMatrix (also updates weighting1 params)
            updateC2V(
                    c2v,
                    options.getRunMode().isHybrid() ? options.getWeighting2() : options.getWeighting1(),
                    selectedComponents,
                    components,
                    distanceMatrix);
        }

        // Remove last row (last row is the whole set and the last but one is not required)
        treeSplits.removeLastSplit();

        // Create ordering
        CircularOrdering permutation = this.createCircularOrdering(components);
        organiseSplits(treeSplits, permutation);

        // Set tree split system
        CompatibleSplitSystem tree = new CompatibleSplitSystem(treeSplits.getSplits(), distanceMatrix, permutation);
        CircularSplitSystem network = new CircularSplitSystem(distanceMatrix, permutation);

        return new NetMakeResult(tree, network);
    }

    protected Pair<Integer, Integer> selectionStep1(final DistanceMatrix c2c, final SplitSystem components) {

        final int nbTaxa = c2c.size();
        final int nbSplits = components.getNbSplits();

        double min1 = Double.POSITIVE_INFINITY;
        double[] sum1 = new double[nbTaxa];
        double[] sum2 = new double[nbTaxa];

        int sc1 = -1;
        int sc2 = -1;

        for (int i = 0; i < nbSplits; i++) {
            sum1[i] = 0;
            sum2[i] = 0;
            for (int j = 0; j < nbSplits; j++) {
                if (j != i) {
                    sum1[i] = sum1[i] + c2c.getDistance(i, j);
                    sum2[i] = sum2[i] + c2c.getDistance(j, i);
                }
            }
        }

        // first Selection step for Components
        for (int i = 0; i < nbSplits; i++) {
            for (int j = i + 1; j < nbSplits; j++) {
                double qDist = (nbSplits - 2)
                        * c2c.getDistance(i, j)
                        - sum1[i] - sum2[j];

                if (qDist < min1) {
                    min1 = qDist;
                    sc1 = i;
                    sc2 = j;
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

        SplitBlock splitBlockSc1 = components.getSplitAt(sc1).getASide();
        SplitBlock splitBlockSc2 = components.getSplitAt(sc2).getASide();

        /*
         * second selection step for vertices with
         * ComponentsVerticesSistances
         */
        for (int i = 0; i < splitBlockSc1.size(); i++) {
            for (int j = 0; j < splitBlockSc2.size();
                 j++) {
                double sum1 = 0;
                double sum2 = 0;
                double sum3 = 0;
                double sum4 = 0;
                int vertex_last1 = 0;
                int vertex_last2 = 0;

                for (int k = 0; k < components.getNbSplits(); k++) {
                    if ((k != sc1)
                            && (k != sc2)) {
                        sum1 += c2v.getDistance(
                                splitBlockSc1.get(i)-1, k);
                    }

                    if ((k != sc1)
                            && (k != sc2)) {
                        sum2 += c2v.getDistance(
                                splitBlockSc2.get(j)-1, k);
                    }
                }

                for (int k = 0; k < splitBlockSc2.size();
                     k++) {
                    sum3 += distanceMatrix.getDistance(
                            splitBlockSc1.get(i)-1,
                            splitBlockSc2.get(k)-1);

                    if (splitBlockSc2.get(k)
                            != splitBlockSc2.get(j)) {
                        vertex_last2 = splitBlockSc2.get(k)-1;
                    }
                }

                for (int k = 0; k < splitBlockSc1.size();
                     k++) {
                    int vertex1 = splitBlockSc1.get(k)-1;

                    if (vertex1 != splitBlockSc1.get(i)-1) {
                        vertex_last1 = vertex1;
                    }
                }

                sum3 += distanceMatrix.getDistance(splitBlockSc2.get(j)-1, vertex_last1);
                sum4 += distanceMatrix.getDistance(splitBlockSc1.get(i)-1, vertex_last2);

                int outerVertices1 = 0;
                if (splitBlockSc1.size() == 1) {
                    outerVertices1 = 1;
                } else {
                    outerVertices1 = 2;
                }

                int outerVertices2 = 0;
                if (splitBlockSc2.size() == 1) {
                    outerVertices2 = 1;
                } else {
                    outerVertices2 = 2;
                }

                double qDist = (components.getNbSplits() - 4 + outerVertices1
                        + outerVertices2) * distanceMatrix.getDistance(splitBlockSc1.get(i)-1,
                        splitBlockSc2.get(j)-1)
                        - sum1 - sum2 - sum3 - sum4;

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

        final int nbSplits = components.getNbSplits();

        for (int i = 0; i < nbSplits; i++) {
            for (int j = 0; j < nbSplits; j++) {
                if (i == j) {
                    c2c.setDistance(i, j, 0.0);
                } else {
                    double aComponentDistance = 0.0;

                    SplitBlock sbI = components.getSplitAt(i).getASide();
                    SplitBlock sbJ = components.getSplitAt(j).getASide();

                    for (int k = 0; k < sbI.size(); k++) {
                        for (int m = 0; m < sbJ.size(); m++) {
                            int vertex1 = sbI.get(k) - 1;
                            int vertex2 = sbJ.get(m) - 1;
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

    protected void updateC2V(DistanceMatrix c2v, Weighting w, Pair<Integer, Integer> selectedComponents, SplitSystem components, DistanceMatrix distanceMatrix) {

        int position = -1;
        final int sc1 = selectedComponents.getKey();

        SplitBlock sb1 = components.getSplitAt(sc1).getASide();
        int componentSplitPosition = sb1.size();

        for (int i = 0; i < distanceMatrix.size(); i++) {
            for (int j = 0; j < sb1.size(); j++) {
                if (i == sb1.get(j)) {
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
            for (int j = 0; j < components.getNbSplits(); j++) {
                double aComponentVertexDistance = 0.;
                int k = 0;
                SplitBlock sbJ = components.getSplitAt(j).getASide();

                while (k < sbJ.size()) {
                    if (sbJ.get(k) == i) {
                        aComponentVertexDistance = 0.;
                        k = sbJ.size();
                    } else {
                        int vertex1 = i;
                        int vertex2 = sbJ.get(k) - 1;
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

    protected void organiseSplits(SplitSystem splits, CircularOrdering permutation) {

        CircularOrdering permutationInvert = permutation.invertOrdering();

        for (int i = 0; i < splits.getNbSplits(); i++) {

            SplitBlock sb = splits.getSplitAt(i).getASide();

            int k = permutationInvert.getIndexAt(sb.getFirst()-1);
            int l = permutationInvert.getIndexAt(sb.getLast()-1);

            if (l < k) {
                sb.reverse();
            }
        }
    }

    protected CircularOrdering createCircularOrdering(SplitSystem components) {

        ArrayList<Integer> help = new ArrayList<>();
        for (int j = 0; j < 2; j++) {
            SplitBlock sbJ = components.getSplitAt(j).getASide();
            for (int i = 0; i < sbJ.size(); i++) {
                help.add(sbJ.get(i));
            }
        }
        int[] permutation = new int[help.size()];
        for (int i = 0; i < help.size(); i++) {
            permutation[i] = help.get(i);
        }

        return new CircularOrdering(permutation);
    }

    @Override
    public void run() {

        try {
            this.runNN(this.options);
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
            this.setError(e);
            this.trackerFinished(false);
        }
        finally {
            this.notifyListener();
        }
    }
}
