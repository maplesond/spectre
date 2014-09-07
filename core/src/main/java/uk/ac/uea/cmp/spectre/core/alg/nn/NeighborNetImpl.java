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

package uk.ac.uea.cmp.spectre.core.alg.nn;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import uk.ac.uea.cmp.spectre.core.alg.CircularOrderingCreator;
import uk.ac.uea.cmp.spectre.core.ds.Identifier;
import uk.ac.uea.cmp.spectre.core.ds.IdentifierList;
import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.spectre.core.ds.distance.FlexibleDistanceMatrix;
import uk.ac.uea.cmp.spectre.core.ds.split.SplitSystem;

import java.util.*;

/**
 * Created by dan on 27/02/14.
 */
public class NeighborNetImpl implements CircularOrderingCreator {

    protected Stack<VertexTriplet> stackedVertexTriplets;

    protected CVMatrices matrices;
    protected NeighborNetParams params;

    public NeighborNetImpl() {
        this.stackedVertexTriplets = new Stack<>();
        this.matrices = new CVMatrices();
        this.params = new NeighborNetParams();
    }

    @Override
    public IdentifierList createCircularOrdering(DistanceMatrix distanceMatrix) {

        // Setup all matrices and temporary data structure required for NN
        this.matrices = new CVMatrices(distanceMatrix, params);

        // Reduce down to a max of 3 nodes
        while (this.matrices.v2v.size() > 3) {

            // Choose a pair of components from c2c that minimise the Q criterion
            Pair<Identifier, Identifier> selectedComponents = this.selectionStep1();

            // Choose a pair of vertices that minimise the Q criterion
            Pair<Identifier, Identifier> selectedVertices = this.selectionStep2(selectedComponents);

            // Reduces vertices contained within selected components to a pair.  Using the selected vertices to determine
            // which vertices to reduce
            Pair<Identifier, Identifier> newVertices = this.reduce(selectedComponents, selectedVertices);

            // Update the managed data structures now that some vertices have been removed and replaced with new ones
            this.matrices.update(selectedComponents, newVertices);
        }

        // Expand back to taxa to get circular ordering and translate back to original nomenclature
        IdentifierList circularOrdering = this.matrices.expand(this.stackedVertexTriplets);

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
     * @return a pair of components that minimise the Q criterion
     */
    protected Pair<Identifier, Identifier> selectionStep1() {

        Pair<Identifier, Identifier> bestPair = null;
        double minQ = Double.MAX_VALUE;

        DistanceMatrix c2c = this.matrices.c2c;

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

        DistanceMatrix v2v = this.matrices.v2v;

        // Both should be 1 or 2 components in length
        IdentifierList vertices1 = this.matrices.c2vs.get(selectedComponents.getLeft());
        IdentifierList vertices2 = this.matrices.c2vs.get(selectedComponents.getRight());

        IdentifierList vertexUnion = new IdentifierList();
        vertexUnion.addAll(vertices1);
        vertexUnion.addAll(vertices2);

        for (Identifier v1 : vertices1) {

            for (Identifier v2 : vertices2) {

                // Subtract the sum of distances from a given vertex in one of the selected components, from the distance between
                // the given vertex and the other component.
                final double sumV1 = this.matrices.sumVertex2Components(v1) - this.matrices.getDistance(selectedComponents.getRight(), v1);
                final double sumV2 = this.matrices.sumVertex2Components(v2) - this.matrices.getDistance(selectedComponents.getLeft(), v2);

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
        IdentifierList vertices1 = this.matrices.c2vs.get(selectedComponents.getLeft());
        IdentifierList vertices2 = this.matrices.c2vs.get(selectedComponents.getRight());

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
        return this.matrices.updateV2V(selectedVertices);
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


    protected static class CVMatrices {

        private IdentifierList components;
        private IdentifierList vertices;

        private Map<Integer, Identifier> componentIdMap;
        private Map<Integer, Identifier> vertexIdMap;

        private Map<Identifier, Identifier> vertexTranslation;          // User 2 canonical
        private Map<Identifier, Identifier> reverseVertexTranslation;   // Canonical 2 user
        private Map<Identifier, Identifier> componentTranslation;

        private DistanceMatrix c2c;
        private DistanceMatrix v2v;

        private Map<Pair<Identifier, Identifier>, Double> c2v;
        private Map<Identifier, IdentifierList> c2vs;
        private Map<Identifier, Identifier> v2c;

        private NeighborNetParams params;


        public CVMatrices() {

            this.components = new IdentifierList();
            this.vertices = new IdentifierList();

            this.componentIdMap = new HashMap<>();
            this.vertexIdMap = new HashMap<>();

            this.vertexTranslation = new HashMap<>();
            this.reverseVertexTranslation = new HashMap<>();
            this.componentTranslation = new HashMap<>();

            this.v2v = new FlexibleDistanceMatrix();
            this.c2c = new FlexibleDistanceMatrix();
            this.c2v = new HashMap<>();
            this.c2vs = new HashMap<>();
            this.v2c = new HashMap<>();

            this.params = new NeighborNetParams();
        }

        /**
         * Assumes id of vertex can be translated to component id
         * @param userMatrix
         */
        public CVMatrices(DistanceMatrix userMatrix, NeighborNetParams params) {

            this();

            // Do vertexTranslation from user taxa and create lookup maps
            this.setupMaps(userMatrix);

            // Setup the component to component matrix (derived form user matrix)
            this.setupC2C(userMatrix);

            // Setup the vertex to vertex matrix (also derived from user matrix)
            this.setupV2V(userMatrix);

            // Setup the linkage between components and vertices
            this.setupC2V();

            // Reset the params with the user specified settings
            this.params = params;
        }

        /**
         * Translate the vertices to normal form (makes it easier to debug!)
         * Also create vertexTranslation and lookup maps
         * @param userMatrix The user distance matrix
         */
        private final void setupMaps(DistanceMatrix userMatrix) {

            int i = 1;
            for(Identifier user : userMatrix.getTaxa()) {
                Identifier vertexCanonical = new Identifier("V" + i, i);
                Identifier componentCanonical = new Identifier("C" + i, i);
                this.vertexTranslation.put(user, vertexCanonical);
                this.reverseVertexTranslation.put(vertexCanonical, user);
                componentTranslation.put(user, componentCanonical);
                this.componentIdMap.put(componentCanonical.getId(), componentCanonical);
                this.vertexIdMap.put(vertexCanonical.getId(), vertexCanonical);
                this.components.add(componentCanonical);
                this.vertices.add(vertexCanonical);
                i++;
            }
        }

        /**
         * Create the initial vertex -> vertex distance matrix derived from the user matrix but using canonical taxa
         * @param userMatrix The user distance matrix
         */
        private final void setupV2V(DistanceMatrix userMatrix) {
            for(Identifier x : userMatrix.getTaxa()) {
                Identifier xCanonical = this.vertexTranslation.get(x);
                for(Identifier y : userMatrix.getTaxa()) {
                    Identifier yCanonical = this.vertexTranslation.get(y);
                    this.v2v.setDistance(xCanonical, yCanonical, userMatrix.getDistance(x, y));
                }
            }
        }

        /**
         * Create the initial component -> component distance matrix derived from the user matrix but using canonical taxa
         * @param userMatrix The user distance matrix
         */
        private final void setupC2C(DistanceMatrix userMatrix) {
            for(Identifier x : userMatrix.getTaxa()) {
                Identifier xCanonical = componentTranslation.get(x);
                for(Identifier y : userMatrix.getTaxa()) {
                    Identifier yCanonical = componentTranslation.get(y);
                    this.c2c.setDistance(xCanonical , yCanonical, userMatrix.getDistance(x, y));
                }
            }
        }


        /**
         * Creates the linkage between components and vertices.  Manages the distances between them.
         */
        private final void setupC2V() {

            this.c2vs = new HashMap<>();
            for(Identifier c : this.c2c.getTaxa()) {
                IdentifierList vs = new IdentifierList();
                vs.add(this.vertexIdMap.get(c.getId()));
                this.c2vs.put(c, vs);
            }

            this.v2c = new HashMap<>();
            for(Identifier v : this.v2v.getTaxa()) {
                this.v2c.put(v, this.componentIdMap.get(v.getId()));
            }


            for(Identifier c : c2c.getTaxa()) {
                for(Identifier v : v2v.getTaxa()) {

                    // Will initially be the same as C2c and V2v
                    this.setDistance(c, v, v2v.getDistance(c.getId(), v.getId()));
                }
            }
        }

        public IdentifierList getComponents() {
            return components;
        }

        public IdentifierList getVertices() {
            return vertices;
        }

        public Identifier createNextComponent() {

            int maxId = 0;

            for (Identifier t : this.components) {
                if (maxId < t.getId()) {
                    maxId = t.getId();
                }
            }

            maxId++;

            return new Identifier("C" + Integer.toString(maxId), maxId);
        }

        public Identifier createNextVertex() {

            int maxId = 0;

            for (Identifier t : this.vertices) {
                if (maxId < t.getId()) {
                    maxId = t.getId();
                }
            }

            maxId++;

            return new Identifier("V" + Integer.toString(maxId), maxId);
        }

        public double setDistance(Identifier component, Identifier vertex, final double value) {

            if (component == null || vertex == null)
                throw new IllegalArgumentException("Need two valid taxa to set a distance");

            Pair<Identifier, Identifier> pair = new ImmutablePair<>(component, vertex);
            Double oldVal = this.c2v.get(pair);
            this.c2v.put(pair, value);

            return oldVal == null ? 0.0 : oldVal;
        }

        public double getDistance(final Identifier component, final Identifier vertex) {

            if (component == null || vertex == null)
                throw new IllegalArgumentException("Need two valid taxa to get a distance");

            Double val = this.c2v.get(new ImmutablePair<>(component, vertex));

            return val == null ? 0.0 : val;
        }

        public IdentifierList getVertices(Identifier component) {
            return this.c2vs.get(component);
        }


        public LinkedList<Integer> getOrder() {
            LinkedList<Integer> order = new LinkedList<>();

            for (Map.Entry<Identifier, IdentifierList> entry : this.c2vs.entrySet()) {

                for (Identifier i : entry.getValue()) {
                    order.add(i.getId());
                }
            }
            return order;
        }

        public Set<Map.Entry<Identifier, IdentifierList>> getMapEntries() {

            return this.c2vs.entrySet();
        }

        public int sumVertex2Components(Identifier v) {

            int sum = 0;

            for (Map.Entry<Identifier, Identifier> entry : this.v2c.entrySet()) {
                sum += this.getDistance(entry.getValue(), v);
            }

            return sum;
        }

        public IdentifierList expand(Stack<VertexTriplet> stackedVertexTriplets) {

            LinkedList<Integer> order = this.getOrder();

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
                orderedTaxa.add(vertexIdMap.get(i));
            }

            return this.reverseTranslate(orderedTaxa);
        }

        protected IdentifierList reverseTranslate(IdentifierList circularOrdering) {

            IdentifierList translatedTaxa = new IdentifierList();

            for (Identifier i : circularOrdering) {
                translatedTaxa.add(this.reverseVertexTranslation.get(i));
            }

            return translatedTaxa;
        }

        public void update(Pair<Identifier, Identifier> selectedComponents, Pair<Identifier, Identifier> newVertices) {

            // Remove the selected components from step 1 from connected components
            this.c2vs.remove(selectedComponents.getLeft());
            this.c2vs.remove(selectedComponents.getRight());

            this.components.remove(selectedComponents.getLeft());
            this.components.remove(selectedComponents.getRight());


            // Add the grouped vertices to the c2vsmap under a new component
            IdentifierList mergedSet = new IdentifierList();
            mergedSet.add(newVertices.getLeft());
            mergedSet.add(newVertices.getRight());
            Identifier newComponent = this.createNextComponent();
            this.c2vs.put(newComponent, mergedSet);
            this.components.add(newComponent);

            this.c2v = new HashMap<>();

            for (Map.Entry<Identifier, IdentifierList> components1 : this.getMapEntries()) {

                for (Identifier v : v2v.getTaxa()) {

                    double sum1 = 0.0;

                    for (Identifier selectedVertex : components1.getValue()) {
                        sum1 += v2v.getDistance(selectedVertex, v);
                    }

                    this.setDistance(components1.getKey(), v,
                            1.0 / components1.getValue().size() * sum1);
                }
            }

            // Update C2C using the recently updated C2V info
            this.updateC2C();
        }

        public final Pair<Identifier, Identifier> updateV2V(VertexTriplet selectedVertices) {

            // Create two new taxa for the reduced
            Identifier newVertex1 = this.createNextVertex();
            vertices.add(newVertex1);

            Identifier newVertex2 = this.createNextVertex();
            vertices.add(newVertex2);

            // Setup shortcuts
            final Identifier vertex1 = selectedVertices.vertex1;
            final Identifier vertex2 = selectedVertices.vertex2;
            final Identifier vertex3 = selectedVertices.vertex3;

            // Iterate over all active vertices to set the new distances
            for (Identifier v : this.vertices) {

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

            vertices.remove(vertex1);
            vertices.remove(vertex2);
            vertices.remove(vertex3);

            return new ImmutablePair<>(newVertex1, newVertex2);
        }

        private final void updateC2C() {

            // Simpler to clear the c2c matrix and start again... check to make sure this doesn't become a performance issue
            // when the number of taxa is large
            c2c = new FlexibleDistanceMatrix();

            for (Map.Entry<Identifier, IdentifierList> components1 : this.c2vs.entrySet()) {

                for (Map.Entry<Identifier, IdentifierList> components2 : this.c2vs.entrySet()) {

                    double sum1 = 0.0;

                    for (Identifier id1 : components1.getValue()) {
                        for (Identifier id2 : components2.getValue()) {
                            sum1 += v2v.getDistance(id1, id2);
                        }
                    }

                    c2c.setDistance(components1.getKey(), components2.getKey(),
                            1.0 / (components1.getValue().size() * components2.getValue().size()) * sum1);
                }
            }
        }

        public DistanceMatrix getC2C() {
            return c2c;
        }

        public DistanceMatrix getV2V() {
            return v2v;
        }

        public void setC2C(DistanceMatrix c2c) {
            this.c2c = c2c;
        }

        public void setV2V(DistanceMatrix v2v) {
            this.v2v = v2v;
        }
    }
}
