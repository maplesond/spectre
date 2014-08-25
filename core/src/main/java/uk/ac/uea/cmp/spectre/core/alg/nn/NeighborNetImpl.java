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
import uk.ac.uea.cmp.spectre.core.ds.Identifier;
import uk.ac.uea.cmp.spectre.core.ds.IdentifierList;
import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.spectre.core.ds.distance.FlexibleDistanceMatrix;
import uk.ac.uea.cmp.spectre.core.ds.split.SpectreSplitSystem;
import uk.ac.uea.cmp.spectre.core.ds.split.SplitSystem;

import java.util.*;

/**
 * Created by dan on 27/02/14.
 */
public class NeighborNetImpl implements NeighborNet {

    protected Stack<VertexTriplet> stackedVertexTriplets;
    //protected Component2VertexSetMap c2vsMap;
    protected DistanceMatrix c2c;
    protected C2VMatrix c2v;
    protected DistanceMatrix v2v;
    protected NeighborNetParams params;

    public NeighborNetImpl() {
        this.stackedVertexTriplets = null;
        //this.c2vsMap = null;
        this.c2c = null;
        this.c2v = null;
        this.v2v = null;
        this.params = null;
    }

    private void setupCandV(DistanceMatrix distanceMatrix) {

        // Constructor should handle all the setup
        this.c2v = new C2VMatrix(distanceMatrix);

        this.c2c = new FlexibleDistanceMatrix();

        IdentifierList components = c2v.getComponents();

        for(Identifier i : components) {
            for(Identifier j : components) {
                this.c2c.setDistance(i , j, distanceMatrix.getDistance(i.getId(), j.getId()));
            }
        }

        // DistanceMatrix between vertices and vertices (make deep copy from initial distance matrix)
        this.v2v = new FlexibleDistanceMatrix();

        IdentifierList vertices = c2v.getVertices();

        for(Identifier i : vertices) {
            for(Identifier j : vertices) {
                this.v2v.setDistance(i , j, distanceMatrix.getDistance(i.getId(), j.getId()));
            }
        }
    }

    @Override
    public SplitSystem execute(DistanceMatrix distanceMatrix, NeighborNetParams params) {

        // We store intermediary triplets of nodes on a stack
        this.stackedVertexTriplets = new Stack<>();

        // Store params in this class so we can access it from anywhere
        this.params = params;

        // Creates a simple split system with trivial splits from the given distance matrix
        SplitSystem treeSplits = new SpectreSplitSystem(distanceMatrix.getTaxa());

        // DistanceMatrix between components (make deep copy from initial distance matrix and alter the names)
        setupCandV(distanceMatrix);


        // Reduce down to a max of 3 nodes
        while (v2v.size() > 3) {

            // Choose a pair of components from c2c that minimise the Q criterion
            Pair<Identifier, Identifier> selectedComponents = this.selectionStep1();

            // Choose a pair of vertices that minimise the Q criterion
            Pair<Identifier, Identifier> selectedVertices = this.selectionStep2(selectedComponents);

            // Reduces vertices contained within selected components to a pair.  Using the selected vertices to determine
            // which vertices to reduce
            Pair<Identifier, Identifier> newVertices = this.reduce(selectedComponents, selectedVertices);

            // Update the managed data structures now that some vertices have been removed and replaced with new ones
            this.updateDataStructures(selectedComponents, newVertices);
        }

        // Expand back to taxa to get circular ordering
        IdentifierList circularOrdering = expand();

        return new SpectreSplitSystem(distanceMatrix, circularOrdering, SpectreSplitSystem.LeastSquaresCalculator.TREE_IN_CYCLE);
    }

    protected IdentifierList expand() {

        LinkedList<Integer> order = this.c2v.getOrder();

        while (!this.stackedVertexTriplets.isEmpty()) {

            Integer max = Collections.max(order);
            int indexOfMax = order.indexOf(max);
            order.remove(max);

            Integer max2 = Collections.max(order);
            int indexOfMax2 = order.indexOf(max2);
            order.remove(max2);

            VertexTriplet vt = this.stackedVertexTriplets.pop();

            order.add(indexOfMax2, vt.vertex1.getId());
            order.add(indexOfMax2 + 1, vt.vertex2.getId());
            order.add(indexOfMax2 + 2, vt.vertex3.getId());
        }

        IdentifierList orderedTaxa = new IdentifierList();

        for (Integer i : order) {
            orderedTaxa.add(v2v.getTaxa().getById(i));
        }

        return orderedTaxa;
    }

    protected Pair<Identifier, Identifier> reduce(Pair<Identifier, Identifier> selectedComponents, Pair<Identifier, Identifier> selectedVertices) {

        // Both should be 1 or 2 components in length
        IdentifierList vertices1 = c2v.getVertices(selectedComponents.getLeft());
        IdentifierList vertices2 = c2v.getVertices(selectedComponents.getRight());

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
        } else { // Should be 4

            Identifier first = vertices1.get(0).equals(selectedVertices.getLeft()) ?
                    vertices1.get(1) :
                    vertices1.get(0);

            Identifier second = selectedVertices.getLeft();
            Identifier third = selectedVertices.getRight();

            Identifier fourth = vertices2.get(0).equals(selectedVertices.getRight()) ?
                    vertices2.get(1) :
                    vertices2.get(0);

            Pair<Identifier, Identifier> newVertices = vertexTripletReduction(new VertexTriplet(first, second, third));

            return vertexTripletReduction(new VertexTriplet(newVertices.getLeft(), newVertices.getRight(), fourth));
        }

        //throw new IllegalArgumentException("Number of vertices must be >= 2 || <= 4.  Found " + nbVerticies + " vertices");
    }

    /**
     * Choose a pair of components that minimise the Q criterion from c2c
     *
     * @return a pair of components that minimise the Q criterion
     */
    protected Pair<Identifier, Identifier> selectionStep1() {

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

        return bestPair;
    }


    protected Pair<Identifier, Identifier> selectionStep2(Pair<Identifier, Identifier> selectedComponents) {

        Pair<Identifier, Identifier> bestPair = null;
        double minQ = Double.MAX_VALUE;

        // Both should be 1 or 2 components in length
        IdentifierList vertices1 = this.c2v.getVertices(selectedComponents.getLeft());
        IdentifierList vertices2 = this.c2v.getVertices(selectedComponents.getRight());

        IdentifierList vertexUnion = new IdentifierList();
        vertexUnion.addAll(vertices1);
        vertexUnion.addAll(vertices2);

        for (Identifier v1 : vertices1) {

            for (Identifier v2 : vertices2) {

                // Subtract the sum of distances from a given vertex in one of the selected components, from the distance between
                // the given vertex and the other component.
                final double sumV1 = c2v.sumVertex2Components(v1) - c2v.getDistance(selectedComponents.getRight(), v1);
                final double sumV2 = c2v.sumVertex2Components(v2) - c2v.getDistance(selectedComponents.getLeft(), v2);

                double sumVId1 = 0.0;
                double sumVId2 = 0.0;

                for (Identifier v : vertexUnion) {
                    sumVId1 += c2v.getDistance(v1, v);
                    sumVId2 += c2v.getDistance(v2, v);
                }

                double q = ((vertexUnion.size() - 4 + vertices1.size() + vertices2.size()) * v2v.getDistance(v1, v2)) -
                        sumV1 - sumV2 - sumVId1 - sumVId2;

                if (q < minQ) {
                    minQ = q;
                    bestPair = new ImmutablePair<>(v1, v2);
                }
            }
        }

        return bestPair;
    }


    protected void updateDataStructures(Pair<Identifier, Identifier> selectedComponents, Pair<Identifier, Identifier> newVertices) {


        this.c2v.update(selectedComponents, newVertices, this.v2v);

        // Simpler to clear the c2c matrix and start again... check to make sure this doesn't become a performance issue
        // when the number of taxa is large
        c2c = new FlexibleDistanceMatrix();

        for (Map.Entry<Identifier, IdentifierList> components1 : this.c2v.getMapEntries()) {

            for (Map.Entry<Identifier, IdentifierList> components2 : this.c2v.getMapEntries()) {

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

        IdentifierList taxa = v2v.getTaxa();

        // Create two new taxa for the reduced
        Identifier newVertex1 = taxa.createNextIdentifier();
        newVertex1.setName("V" + newVertex1.getId());
        taxa.add(newVertex1);

        Identifier newVertex2 = taxa.createNextIdentifier();
        newVertex2.setName("V" + newVertex2.getId());
        taxa.add(newVertex2);

        // Setup shortcuts
        final Identifier vertex1 = selectedVertices.vertex1;
        final Identifier vertex2 = selectedVertices.vertex2;
        final Identifier vertex3 = selectedVertices.vertex3;

        // Iterate over all active vertices
        for (Identifier v : taxa) {

            // Only process this vertex if it is not in the selected vertex list
            if (v != vertex1 && v != vertex2 && v != vertex3) {

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

    protected static class Component2VertexSetMap extends HashMap<Identifier, IdentifierList> {

        public Component2VertexSetMap(IdentifierList taxa) {

            for (Identifier t : taxa) {
                IdentifierList newTaxa = new IdentifierList();
                newTaxa.add(t);
                this.put(t, newTaxa);
            }
        }

        public Identifier createNextIdentifier() {

            int maxId = 0;

            for (Identifier t : this.keySet()) {
                if (maxId < t.getId()) {
                    maxId = t.getId();
                }
            }

            return new Identifier(maxId + 1);
        }
    }

    protected static class C2VMatrix {

        private IdentifierList components;
        private IdentifierList vertices;

        private Map<Pair<Identifier, Identifier>, Double> matrix;
        private Map<Identifier, IdentifierList> c2vs;
        private Map<Identifier, IdentifierList> v2cs;


        public C2VMatrix() {
            this.components = new IdentifierList();
            this.vertices = new IdentifierList();
            this.matrix = new HashMap<>();
            this.c2vs = new HashMap<>();
            this.v2cs = new HashMap<>();
        }

        /**
         * Assumes id of vertex can be translated to component id
         * @param v2v
         */
        public C2VMatrix(DistanceMatrix v2v) {

            this();

            for(Identifier i : v2v.getTaxa()) {
                for(Identifier j : v2v.getTaxa()) {

                    Identifier c = new Identifier("C" + Integer.toString(i.getId()), i.getId());
                    Identifier v = new Identifier("V" + Integer.toString(j.getId()), j.getId());

                    if (!components.contains(c)) {
                        this.addComponent(c);
                    }

                    if (!vertices.contains(v)) {
                        this.addVertex(v);
                    }

                    this.setDistance(c, v, v2v.getDistance(i, j));

                    if (i.getId() != j.getId()) {
                        this.addVertex(c, v);
                        this.addComponent(v, c);
                    }
                }
            }
        }

        public IdentifierList getComponents() {
            return components;
        }

        public IdentifierList getVertices() {
            return vertices;
        }

        public void addComponent(Identifier c) {

            if (!components.contains(c)) {
                this.components.add(c);
            }
        }

        public void addVertex(Identifier v) {

            if (!vertices.contains(v)) {
                this.vertices.add(v);
            }
        }

        public void addVertex(Identifier c, Identifier v) {

            if (this.c2vs.get(c) == null) {
                IdentifierList vs = new IdentifierList();
                vs.add(v);
                this.c2vs.put(c, vs);
            }
            else {
                this.c2vs.get(c).add(v);
            }
        }

        public void addComponent(Identifier v, Identifier c) {

            if (this.v2cs.get(v) == null) {
                IdentifierList cs = new IdentifierList();
                cs.add(v);
                this.v2cs.put(v, cs);
            }
            else {
                this.v2cs.get(v).add(c);
            }
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

        public double setDistance(Identifier component, Identifier vertex, final double value) {

            if (component == null || vertex == null)
                throw new IllegalArgumentException("Need two valid taxa to set a distance");

            Pair<Identifier, Identifier> pair = new ImmutablePair<>(component, vertex);
            Double oldVal = this.matrix.get(pair);
            this.matrix.put(pair, value);

            return oldVal == null ? 0.0 : oldVal;
        }

        public double getDistance(final Identifier component, final Identifier vertex) {

            if (component == null || vertex == null)
                throw new IllegalArgumentException("Need two valid taxa to get a distance");

            Double val = this.matrix.get(new ImmutablePair<>(component, vertex));

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

            for (Map.Entry<Identifier, IdentifierList> entry : this.v2cs.entrySet()) {
                for (Identifier c : entry.getValue()) {
                    sum += this.getDistance(c, v);
                }
            }

            return sum;
        }

        public void update(Pair<Identifier, Identifier> selectedComponents, Pair<Identifier, Identifier> newVertices,
                           DistanceMatrix v2v) {

            // Remove the selected components from step 1 from connected components
            this.c2vs.remove(selectedComponents.getLeft());
            this.c2vs.remove(selectedComponents.getRight());

            this.vertices.add(newVertices.getLeft());
            this.vertices.add(newVertices.getRight());

            // Add the grouped vertices to the c2vsmap under a new component
            IdentifierList mergedSet = new IdentifierList();
            mergedSet.add(newVertices.getLeft());
            mergedSet.add(newVertices.getRight());
            Identifier newComponent = this.createNextComponent();
            this.c2vs.put(newComponent, mergedSet);
            this.components.add(newComponent);

            this.matrix = new HashMap<>();

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

        }
    }
}
