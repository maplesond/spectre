/*
 * Suite of PhylogEnetiC Tools for Reticulate Evolution (SPECTRE)
 * Copyright (C) 2015  UEA School of Computing Sciences
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

package uk.ac.uea.cmp.spectre.core.ds.split.circular.ordering.nm;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import uk.ac.uea.cmp.spectre.core.ds.Identifier;
import uk.ac.uea.cmp.spectre.core.ds.IdentifierList;
import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.spectre.core.ds.distance.FlexibleDistanceMatrix;
import uk.ac.uea.cmp.spectre.core.ds.split.*;
import uk.ac.uea.cmp.spectre.core.ds.split.circular.ordering.CVMatrices;
import uk.ac.uea.cmp.spectre.core.ds.split.circular.ordering.CircularOrderingCreator;
import uk.ac.uea.cmp.spectre.core.ds.split.circular.ordering.nm.weighting.GreedyMEWeighting;
import uk.ac.uea.cmp.spectre.core.ds.split.circular.ordering.nm.weighting.Weighting;

import java.util.Map;

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

    protected CVMatrices mx;

    private SplitSystem treeSplits;

    public NetMakeCircularOrderer(Weighting weighting1, Weighting weighting2) {

        this.weighting1 = weighting1;
        this.weighting2 = weighting2;

        this.runMode = getRunMode(this.weighting1, this.weighting2);

        this.treeSplits = null;
    }


    @Override
    public IdentifierList createCircularOrdering(final DistanceMatrix distanceMatrix) {

        // Creates a simple split system with trivial splits from the given distance matrix
        this.treeSplits = new SpectreSplitSystem(distanceMatrix.getTaxa(), SplitUtils.createTrivialSplits(distanceMatrix.getTaxa(), 1.0));

        this.mx = new CVMatrices(distanceMatrix);

        // Initialise all weightings to 1.0
        this.weighting1.initialiseWeightings(this.mx.getVertices());
        if (this.weighting2 != null) {
            this.weighting2.initialiseWeightings(this.mx.getVertices());
        }

        // If required create a new GreedyME instance.
        GreedyMEWeighting gme = runMode == RunMode.HYBRID_GREEDYME ?
                new GreedyMEWeighting(distanceMatrix) :
                null;

        // Loop until components has at least two entries left.
        while (mx.getNbActiveComponents() > 2) {

            // The pair should be splits with the shortest tree length between them
            Pair<Identifier, Identifier> selectedComponents =
                    this.runMode == RunMode.HYBRID_GREEDYME ?
                        gme.makeMECherry(this.treeSplits, this.mx) :
                        this.selectionStep1(this.mx.getC2C());

            Pair<Identifier, Identifier> selectedVertices = this.selectionStep2(selectedComponents);

            // Merge of components
            Identifier mergedComponent = this.merge(selectedComponents, selectedVertices);

            // Add new component/split to Split list
            treeSplits.add(new SpectreSplit(new SpectreSplitBlock(mx.getVertices(mergedComponent).getIdsAsLinkedList()), distanceMatrix.getTaxa().size()));

            // Update component to component distanceMatrix (assuming not in GreedyME mode)
            if (runMode != RunMode.HYBRID_GREEDYME) {
                this.updateC2C(weighting1);
            }

            // Update the weightings for each vertex
            Weighting w = runMode.isHybrid() ? weighting2 : weighting1;
            w.updateWeightings(this.mx.getVertices());

            // Update component to vertex distanceMatrix (also updates weighting1 params)
            this.updateC2V(w);
        }

        // Remove last row (last row is the whole set and the last but one is not required)
        treeSplits.removeLastSplit();

        // Create ordering
        IdentifierList permutation = this.finalOrdering();

        // Translate and return
        return this.mx.reverseTranslate(permutation);
    }

    private IdentifierList finalOrdering() {

        final int finalActiveComponents = this.mx.getNbActiveComponents();

        if (finalActiveComponents == 0) {
            throw new IllegalStateException("Algorithm Error: 0 active components.  This should never happen.");
        }
        else if (finalActiveComponents == 1) {
            IdentifierList allVertices = new IdentifierList();

            // This loop should only go around once... just did it this way because it seemed to be the easiest way to
            // get out the information we need
            for(IdentifierList vertices : this.mx.getC2Vs().values()) {
                allVertices.addAll(vertices);
            }

            return allVertices;
        }
        else if (finalActiveComponents == 2) {

            Identifier sc1 = null;
            Identifier sc2 = null;

            int i = 1;
            for(Identifier sc : this.mx.getC2Vs().keySet()) {

                if (i == 1) {
                    sc1 = sc;
                }
                else if (i == 2) {
                    sc2 = sc;
                }

                i++;
            }

            // Run second selection step again
            Pair<Identifier, Identifier> selectedVertices = this.selectionStep2(new ImmutablePair<Identifier, Identifier>(sc1, sc2));

            return this.mergeVertices(selectedVertices, this.mx.getC2Vs().get(sc1), this.mx.getC2Vs().get(sc2));
        }
        else {
            throw new IllegalStateException("Algorithm Error: More than 2 active components.  This should never happen.");
        }
    }

    @Override
    public boolean createsTreeSplits() {
        return true;
    }

    @Override
    public SplitSystem getTreeSplits() {
        return this.treeSplits;
    }

    /**
     * Selects the two best components in the system.  Identical to NeighborNet selection step 1.
     * @param c2c The component 2 component distance matrix
     * @return The two best components in the matrix
     */
    protected Pair<Identifier, Identifier> selectionStep1(final DistanceMatrix c2c) {

        Pair<Identifier, Identifier> bestPair = null;
        double minQ = Double.MAX_VALUE;

        for (Map.Entry<Pair<Identifier, Identifier>, Double> entry : c2c.getMap().entrySet()) {

            Identifier id1 = entry.getKey().getLeft();
            Identifier id2 = entry.getKey().getRight();

            final double id1_2_id2 = entry.getValue();

            final double sumId1 = c2c.getDistances(id1, null).sum() - id1_2_id2;
            final double sumId2 = c2c.getDistances(id2, null).sum() - id1_2_id2;

            final double q = (c2c.size() - 2) * c2c.getDistance(id1, id2) - sumId1 - sumId2;

            if (q < minQ) {
                minQ = q;
                bestPair = entry.getKey();
            }
        }

        // Ensure we are in canonical form
        if (bestPair.getLeft().getId() > bestPair.getRight().getId()) {
            bestPair = new ImmutablePair<>(bestPair.getRight(), bestPair.getLeft());
        }

        return bestPair;
    }

    /**
     * Selects to two best vertices associated with the two best component groups.  Identical to NeighborNet selection
     * step 2
     * @param selectedComponents The 2 selected components
     * @return The two best vertices
     */
    protected Pair<Identifier, Identifier> selectionStep2(Pair<Identifier, Identifier> selectedComponents) {

        Pair<Identifier, Identifier> bestPair = null;
        double minQ = Double.MAX_VALUE;

        final DistanceMatrix v2v = this.mx.getV2V();

        // Both should be 1 or 2 components in length
        IdentifierList vertices1 = this.mx.getVertices(selectedComponents.getLeft());
        IdentifierList vertices2 = this.mx.getVertices(selectedComponents.getRight());

        IdentifierList vertexUnion = new IdentifierList();
        vertexUnion.addAll(vertices1);
        vertexUnion.addAll(vertices2);

        for (Identifier v1 : vertices1) {

            for (Identifier v2 : vertices2) {

                // Subtract the sum of distances from a given vertex in one of the selected components, from the distance between
                // the given vertex and the other component.
                final double sumV1 = this.mx.sumVertex2Components(v1) - this.mx.getDistance(selectedComponents.getRight(), v1);
                final double sumV2 = this.mx.sumVertex2Components(v2) - this.mx.getDistance(selectedComponents.getLeft(), v2);

                double sumVId1 = 0.0;
                double sumVId2 = 0.0;

                for (Identifier v : vertexUnion) {
                    sumVId1 += v2v.getDistance(v1, v);
                    sumVId2 += v2v.getDistance(v2, v);
                }

                double q = ((vertexUnion.size() - 4 + vertices1.size() + vertices2.size()) * v2v.getDistance(v1, v2)) -
                        sumV1 - sumV2 - sumVId1 - sumVId2;

                if (q < minQ) {
                    minQ = q;
                    bestPair = new ImmutablePair<>(v1, v2);
                }
            }
        }

        // Ensure we are in canonical form
        if (bestPair.getLeft().getId() > bestPair.getRight().getId()) {
            bestPair = new ImmutablePair<>(bestPair.getRight(), bestPair.getLeft());
        }

        return bestPair;
    }

    protected IdentifierList createCircularOrdering() {

        IdentifierList help = new IdentifierList();

        for (Identifier c : this.mx.getComponents()) {
            for (Identifier v : this.mx.getVertices(c)) {
                help.add(v);
            }
        }
        IdentifierList permutation = new IdentifierList();
        for (int i = 0; i < help.size(); i++) {
            permutation.add(help.get(i));
        }

        return this.mx.reverseTranslate(permutation);
    }


    private IdentifierList mergeVertices(Pair<Identifier, Identifier> selectedVertices, IdentifierList vl1, IdentifierList vl2) {

        Identifier sv1 = selectedVertices.getLeft();
        Identifier sv2 = selectedVertices.getRight();

        IdentifierList mv1Vertices = new IdentifierList();
        IdentifierList mv2Vertices = new IdentifierList();

        mv1Vertices.addAll(vl1);
        mv2Vertices.addAll(vl2);

        // Check which components the selected vertices belong
        boolean invert = !vl1.containsId(sv1.getId());

        Identifier firstVertex = invert ? sv2 : sv1;
        Identifier secondVertex = invert ? sv1 : sv2;

        // Order vertices
        if (vl1.getFirst() == firstVertex) {
            mv1Vertices = mv1Vertices.reverseOrdering();
        }

        if (vl2.getFirst() != secondVertex) {
            mv2Vertices = mv2Vertices.reverseOrdering();
        }

        // Merge vertices
        IdentifierList mv = new IdentifierList();
        mv.addAll(mv1Vertices);
        mv.addAll(mv2Vertices);

        return mv;
    }

    public Identifier merge(Pair<Identifier, Identifier> selectedComponents, Pair<Identifier, Identifier> selectedVertices) {

        Identifier sc1 = selectedComponents.getLeft();
        Identifier sc2 = selectedComponents.getRight();

        IdentifierList sc1Vertices = this.mx.getVertices(sc1);
        IdentifierList sc2Vertices = this.mx.getVertices(sc2);

        IdentifierList mv = this.mergeVertices(selectedVertices, sc1Vertices, sc2Vertices);

        // Create new component
        Identifier next = this.mx.createNextComponent();

        // Update C2Vs
        this.mx.getC2Vs().remove(sc1);
        this.mx.getC2Vs().remove(sc2);
        this.mx.getC2Vs().put(next, mv);

        return next;
    }

    private void updateC2C(Weighting w) {

        DistanceMatrix c2c = new FlexibleDistanceMatrix();

        for (Identifier componentR : this.mx.getC2Vs().keySet()) {
            for (Identifier componentS : this.mx.getC2Vs().keySet()) {
                if (componentR.equals(componentS)) {
                    c2c.setDistance(componentR, componentS, 0.0);
                } else {
                    double distance = 0.0;

                    for (Identifier vertexI : this.mx.getVertices(componentR)) {
                        for (Identifier vertexJ : this.mx.getVertices(componentS)) {
                            double vertexDistance = w.getWeightingParam(vertexI)
                                    * w.getWeightingParam(vertexJ)
                                    * this.mx.getV2V().getDistance(vertexI, vertexJ);

                            distance += vertexDistance;
                        }
                    }
                    c2c.setDistance(componentR, componentS, distance);
                }
            }
        }

        this.mx.setC2C(c2c);
    }

    private void updateC2V(Weighting w) {

        this.mx.getC2V().clear();

        for (Map.Entry<Identifier, IdentifierList> components : this.mx.getMapEntries()) {

            for (Identifier vertexI : this.mx.getVertices()) {

                double sum = 0.0;

                for (Identifier vertexX : components.getValue()) {

                    double vertexDistance = w.getWeightingParam(vertexI) * this.mx.getV2V().getDistance(vertexX, vertexI);

                    sum += vertexDistance;
                }

                this.mx.setDistance(components.getKey(), vertexI, sum);
            }
        }
    }

}
