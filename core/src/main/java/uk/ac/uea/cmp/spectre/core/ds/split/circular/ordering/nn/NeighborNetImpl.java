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

package uk.ac.uea.cmp.spectre.core.ds.split.circular.ordering.nn;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import uk.ac.uea.cmp.spectre.core.ds.Identifier;
import uk.ac.uea.cmp.spectre.core.ds.IdentifierList;
import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.spectre.core.ds.distance.FlexibleDistanceMatrix;
import uk.ac.uea.cmp.spectre.core.ds.split.SplitSystem;
import uk.ac.uea.cmp.spectre.core.ds.split.circular.ordering.CVMatrices;
import uk.ac.uea.cmp.spectre.core.ds.split.circular.ordering.CircularOrderingCreator;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.Stack;

/**
 * Created by dan on 27/02/14.
 */
public class NeighborNetImpl implements CircularOrderingCreator {

    protected Stack<VertexTriplet> stackedVertexTriplets;

    protected CVMatrices mx;
    protected NeighborNetParams params;

    public NeighborNetImpl() {
        this.stackedVertexTriplets = new Stack<>();
        this.mx = new CVMatrices();
        this.params = new NeighborNetParams();
    }

    @Override
    public IdentifierList createCircularOrdering(DistanceMatrix distanceMatrix) {

        // Setup all mx and temporary data structure required for NN
        this.mx = new CVMatrices(distanceMatrix);

        // Reduce down to a max of 3 vertices
        while (this.mx.getNbVertices() > 3) {

            // Choose a pair of components from c2c that minimise the Q criterion
            Pair<Identifier, Identifier> selectedComponents = this.selectionStep1(this.mx.getC2C());

            // Choose a pair of vertices that minimise the Q criterion
            Pair<Identifier, Identifier> selectedVertices = this.selectionStep2(selectedComponents);

            // Reduces vertices contained within selected components to a pair.  Using the selected vertices to determine
            // which vertices to reduce.  Automatically updates V2V matrix if necessary removing reduced verticies and
            // adding new ones
            Pair<Identifier, Identifier> newVertices = this.reduce(selectedComponents, selectedVertices);

            // Merge selected components
            this.merge(selectedComponents, newVertices);

            // Update the distances in the managed data structures now that some vertices have been removed and replaced
            // with new ones
            this.update();
        }

        // Expand back to taxa to get circular ordering and translate back to original nomenclature
        IdentifierList circularOrdering = this.expand(this.stackedVertexTriplets);

        // Sanity check
        if (!this.stackedVertexTriplets.empty()) {
            throw new IllegalStateException("Vertex triplet stack still contains entries.  Something went wrong in the NN algorithm.");
        }

        return circularOrdering;
    }

    @Override
    public boolean createsTreeSplits() {
        return false;
    }

    @Override
    public SplitSystem getTreeSplits() {
        return null;
    }


    /**
     * Choose a pair of components that minimise the Q criterion from c2c
     *
     * @param c2c Component to component distance matrix
     * @return a pair of components that minimise the Q criterion
     */
    protected Pair<Identifier, Identifier> selectionStep1(final DistanceMatrix c2c) {

        Pair<Identifier, Identifier> bestPair = null;
        double minQ = Double.MAX_VALUE;

        for (Map.Entry<Pair<Identifier, Identifier>, Double> entry : c2c.getMap().entrySet()) {

            Identifier id1 = entry.getKey().getLeft();
            Identifier id2 = entry.getKey().getRight();


            final double id1_2_id2 = c2c.getDistance(id1, id2);

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


    protected Pair<Identifier, Identifier> selectionStep2(Pair<Identifier, Identifier> selectedComponents) {

        Pair<Identifier, Identifier> bestPair = null;
        double minQ = Double.MAX_VALUE;

        DistanceMatrix v2v = this.mx.getV2V();

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

    protected Pair<Identifier, Identifier> reduce(Pair<Identifier, Identifier> selectedComponents, Pair<Identifier, Identifier> selectedVertices) {

        // Both should be 1 or 2 components in length
        IdentifierList vertices1 = this.mx.getVertices(selectedComponents.getLeft());
        IdentifierList vertices2 = this.mx.getVertices(selectedComponents.getRight());

        IdentifierList vertexUnion = new IdentifierList();
        vertexUnion.addAll(vertices1);
        vertexUnion.addAll(vertices2);

        final int nbVerticies = vertexUnion.size();

        IdentifierList mergedComponent = new IdentifierList();

        if (nbVerticies == 2) {
            return new ImmutablePair<>(vertexUnion.get(0), vertexUnion.get(1));
        } else if (nbVerticies == 3) {

            Identifier first = null;
            Identifier second = null;
            Identifier third = null;

            if (vertices1.size() == 1) {

                first = vertices1.get(0);

                if (vertices2.get(0).equals(selectedVertices.getLeft()) || vertices2.get(0).equals(selectedVertices.getRight())) {
                    second = vertices2.get(0);
                    third = vertices2.get(1);
                } else {
                    second = vertices2.get(1);
                    third = vertices2.get(0);
                }
            } else {
                first = vertices2.get(0);

                if (vertices1.get(0).equals(selectedVertices.getLeft()) || vertices1.get(0).equals(selectedVertices.getRight())) {
                    second = vertices1.get(0);
                    third = vertices1.get(1);
                } else {
                    second = vertices1.get(1);
                    third = vertices1.get(0);
                }
            }

            return vertexTripletReduction(new VertexTriplet(first, second, third));
        } else if (nbVerticies == 4) {

            Identifier first = vertices1.get(0).equals(selectedVertices.getLeft()) ?
                    vertices1.get(1) :
                    vertices1.get(0);

            Identifier second = selectedVertices.getLeft();
            Identifier third = selectedVertices.getRight();

            Identifier fourth = vertices2.get(0).equals(selectedVertices.getRight()) ?
                    vertices2.get(1) :
                    vertices2.get(0);

            Pair<Identifier, Identifier> newVertices = this.vertexTripletReduction(new VertexTriplet(first, second, third));

            return this.vertexTripletReduction(new VertexTriplet(newVertices.getLeft(), newVertices.getRight(), fourth));
        }
        else {
            throw new IllegalArgumentException("Number of vertices must be >= 2 || <= 4.  Found " + nbVerticies + " vertices");
        }
    }


    /**
     * Reduces the 3 selected vertices down to 2 new merged vertices.  These changes are reflected in the v2v matrix and
     * the 2 new merged vertices are returned
     *
     * @param selectedVertices The three selected vertices to reduce
     * @return The two new vertices that represent the reduced input
     */
    protected Pair<Identifier, Identifier> vertexTripletReduction(final VertexTriplet selectedVertices) {

        // Add the selected vertices to reduce to the stack (we'll need these later)
        this.stackedVertexTriplets.push(selectedVertices);

        // Let the matrix class handle the V2V update, return the two new vertices created from merging the input triplet
        return this.updateV2V(selectedVertices);
    }


    protected static class VertexTriplet {
        protected Identifier vertex1;
        protected Identifier vertex2;
        protected Identifier vertex3;

        public VertexTriplet(Identifier vertex1, Identifier vertex2, Identifier vertex3) {
            this.vertex1 = vertex1;
            this.vertex2 = vertex2;
            this.vertex3 = vertex3;
        }
    }

    public IdentifierList expand(Stack<VertexTriplet> stackedVertexTriplets) {

        LinkedList<Integer> order = this.mx.getOrder();

        while (!stackedVertexTriplets.isEmpty()) {

            Integer max = Collections.max(order);
            int indexOfMax = order.indexOf(max);
            order.remove(max);

            Integer max2 = Collections.max(order);
            int indexOfMax2 = order.indexOf(max2);
            order.remove(max2);

            VertexTriplet vt = stackedVertexTriplets.pop();

            order.add(indexOfMax2, vt.vertex1.getId());
            order.add(indexOfMax2 + 1, vt.vertex2.getId());
            order.add(indexOfMax2 + 2, vt.vertex3.getId());
        }

        IdentifierList orderedTaxa = new IdentifierList();

        for (Integer i : order) {
            orderedTaxa.add(this.mx.getVertex(i));
        }

        return this.mx.reverseTranslate(orderedTaxa);
    }

    private void merge(Pair<Identifier, Identifier> selectedComponents, Pair<Identifier, Identifier> newVertices) {

        // Add the grouped vertices to the c2vsmap under a new component
        IdentifierList mergedSet = new IdentifierList();
        mergedSet.add(newVertices.getLeft());
        mergedSet.add(newVertices.getRight());
        Identifier newComponent = this.mx.createNextComponent();

        // Remove the selected components from step 1 from connected components
        this.mx.getC2Vs().remove(selectedComponents.getLeft());
        this.mx.getC2Vs().remove(selectedComponents.getRight());
        this.mx.getC2Vs().put(newComponent, mergedSet);
    }

    public void update() {

        this.updateC2V();
        this.updateC2C();
    }

    private final void updateC2C() {

        // Simpler to clear the c2c matrix and start again... check to make sure this doesn't become a performance issue
        // when the number of taxa is large
        DistanceMatrix c2c = new FlexibleDistanceMatrix();

        for (Map.Entry<Identifier, IdentifierList> components1 : this.mx.getMapEntries()) {

            for (Map.Entry<Identifier, IdentifierList> components2 : this.mx.getMapEntries()) {

                double sum1 = 0.0;

                for (Identifier id1 : components1.getValue()) {
                    for (Identifier id2 : components2.getValue()) {
                        sum1 += this.mx.getV2V().getDistance(id1, id2);
                    }
                }

                c2c.setDistance(components1.getKey(), components2.getKey(),
                        1.0 / (components1.getValue().size() * components2.getValue().size()) * sum1);
            }
        }

        this.mx.setC2C(c2c);
    }

    private final Pair<Identifier, Identifier> updateV2V(VertexTriplet selectedVertices) {

        DistanceMatrix v2v = this.mx.getV2V();

        // Create two new taxa for the reduced
        Identifier newVertex1 = this.mx.createNextVertex();
        v2v.getTaxa().add(newVertex1);

        Identifier newVertex2 = this.mx.createNextVertex();
        v2v.getTaxa().add(newVertex2);

        // Setup shortcuts
        final Identifier vertex1 = selectedVertices.vertex1;
        final Identifier vertex2 = selectedVertices.vertex2;
        final Identifier vertex3 = selectedVertices.vertex3;

        // Iterate over all active vertices to set the new distances
        for (Identifier v : this.mx.getVertices()) {

            // Only process this vertex if it is not in the selected vertex list
            if (!v.equals(vertex1) && !v.equals(vertex2) && !v.equals(vertex3)) {

                v2v.setDistance(newVertex1, v,
                        ((params.getAlpha() + params.getBeta()) * v2v.getDistance(vertex1, v)) +
                                (params.getGamma() * v2v.getDistance(vertex2, v)));

                v2v.setDistance(newVertex2, v,
                        (params.getAlpha() * v2v.getDistance(vertex2, v)) +
                                ((1 - params.getAlpha()) * v2v.getDistance(vertex3, v)));

                v2v.setDistance(newVertex1, newVertex2,
                        (params.getAlpha() * v2v.getDistance(vertex1, vertex2)) +
                                (params.getBeta() * v2v.getDistance(vertex1, vertex3)) +
                                (params.getGamma() * v2v.getDistance(vertex2, vertex3)));
            }
        }

        // Remove the selected vertices from v2v
        v2v.removeTaxon(vertex1);
        v2v.removeTaxon(vertex2);
        v2v.removeTaxon(vertex3);

        return new ImmutablePair<>(newVertex1, newVertex2);
    }


    private final void updateC2V() {

        this.mx.getC2V().clear();

        for (Map.Entry<Identifier, IdentifierList> components : this.mx.getMapEntries()) {

            for (Identifier v : this.mx.getVertices()) {

                double sum1 = 0.0;

                for (Identifier selectedVertex : components.getValue()) {
                    sum1 += this.mx.getV2V().getDistance(selectedVertex, v);
                }

                this.mx.setDistance(components.getKey(), v,
                        1.0 / components.getValue().size() * sum1);
            }
        }

    }

}
