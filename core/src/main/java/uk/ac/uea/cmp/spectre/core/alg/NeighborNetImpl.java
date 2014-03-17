package uk.ac.uea.cmp.spectre.core.alg;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import uk.ac.uea.cmp.spectre.core.ds.Identifier;
import uk.ac.uea.cmp.spectre.core.ds.IdentifierList;
import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.spectre.core.ds.distance.FlexibleDistanceMatrix;
import uk.ac.uea.cmp.spectre.core.ds.split.CompatibleSplitSystem;
import uk.ac.uea.cmp.spectre.core.ds.split.SimpleSplitSystem;
import uk.ac.uea.cmp.spectre.core.ds.split.SplitSystem;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.Stack;

/**
 * Created by dan on 27/02/14.
 */
public class NeighborNetImpl implements NeighborNet {

    protected Stack<VertexTriplet> stackedVertexTriplets;
    protected Component2VertexSetMap c2vsMap;
    protected DistanceMatrix c2c;
    protected DistanceMatrix c2v;
    protected DistanceMatrix v2v;
    protected NeighborNetParams params;

    public NeighborNetImpl() {
        this.stackedVertexTriplets = null;
        this.c2vsMap = null;
        this.c2c = null;
        this.c2v = null;
        this.v2v = null;
        this.params = null;
    }


    @Override
    public CompatibleSplitSystem execute(DistanceMatrix distanceMatrix, NeighborNetParams params) {

        // We store intermediary triplets of nodes on a stack
        this.stackedVertexTriplets = new Stack<>();

        // Store params in this class so we can access it from anywhere
        this.params = params;

        // Creates a simple split system with trivial splits from the given distance matrix
        SplitSystem treeSplits = new SimpleSplitSystem(distanceMatrix.getTaxa());

        // Setup the component to vertexset map.  Initialise using a one to one mapping of each taxon to itself.
        this.c2vsMap = new Component2VertexSetMap(distanceMatrix.getTaxa());

        // DistanceMatrix between components (make deep copy from initial distance matrix)
        this.c2c = new FlexibleDistanceMatrix(distanceMatrix);

        // DistanceMatrix between components and vertices (make deep copy from initial distance matrix)
        this.c2v = new FlexibleDistanceMatrix(distanceMatrix);

        // DistanceMatrix between vertices and vertices (make deep copy from initial distance matrix)
        this.v2v = new FlexibleDistanceMatrix(distanceMatrix);

        // Reduce down to a max of 3 nodes
        while (c2vsMap.size() > 3) {

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

        return new CompatibleSplitSystem(null, distanceMatrix, circularOrdering);
    }

    protected IdentifierList expand() {

        LinkedList<Integer> order = new LinkedList<>();

        for (Identifier i : c2vsMap.keySet()) {
            order.add(i.getId());
        }

        while (!this.stackedVertexTriplets.isEmpty()) {

            Integer max = Collections.max(order);
            int indexOfMax = order.indexOf(max);

            order.remove(max);

            VertexTriplet vt = this.stackedVertexTriplets.pop();

            order.add(indexOfMax, vt.vertex1.getId());
            order.add(indexOfMax + 1, vt.vertex2.getId());
            order.add(indexOfMax + 2, vt.vertex3.getId());
        }

        IdentifierList orderedTaxa = new IdentifierList();

        for (Integer i : order) {
            orderedTaxa.add(c2c.getTaxa().getById(i));
        }

        return orderedTaxa;
    }


    protected void updateC2C() {

        for (Map.Entry<Identifier, IdentifierList> components1 : c2vsMap.entrySet()) {

            for (Map.Entry<Identifier, IdentifierList> components2 : c2vsMap.entrySet()) {

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

    protected void updateC2V() {

        IdentifierList activeTaxa = v2v.getTaxa();

        for (Map.Entry<Identifier, IdentifierList> components1 : c2vsMap.entrySet()) {

            for (Identifier activeTaxon : activeTaxa) {

                double sum1 = 0.0;

                for (Identifier id1 : components1.getValue()) {
                    sum1 += v2v.getDistance(id1, activeTaxon);
                }

                c2v.setDistance(components1.getKey(), activeTaxon,
                        1.0 / components1.getValue().size() * sum1);
            }
        }
    }


    protected Pair<Identifier, Identifier> reduce(Pair<Identifier, Identifier> selectedComponents, Pair<Identifier, Identifier> selectedVertices) {

        // Both should be 1 or 2 components in length
        IdentifierList vertices1 = c2vsMap.get(selectedComponents.getLeft());
        IdentifierList vertices2 = c2vsMap.get(selectedComponents.getRight());

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

                if (vertices2.get(0) == selectedVertices.getLeft() || vertices2.get(0) == selectedVertices.getRight()) {
                    second = vertices2.get(0);
                    third = vertices2.get(1);
                } else {
                    second = vertices2.get(1);
                    third = vertices2.get(0);
                }
            } else {
                first = vertices2.get(0);

                if (vertices1.get(0) == selectedVertices.getLeft() || vertices1.get(0) == selectedVertices.getRight()) {
                    second = vertices1.get(0);
                    third = vertices1.get(1);
                } else {
                    second = vertices1.get(1);
                    third = vertices1.get(0);
                }
            }

            return vertexTripletReduction(new VertexTriplet(first, second, third));
        } else { // Should be 4

            Identifier first = vertices1.get(0) == selectedVertices.getLeft() ?
                    vertices1.get(1) :
                    vertices1.get(0);

            Identifier second = selectedVertices.getLeft();
            Identifier third = selectedVertices.getRight();

            Identifier fourth = vertices2.get(0) == selectedVertices.getRight() ?
                    vertices2.get(1) :
                    vertices2.get(0);

            Pair<Identifier, Identifier> newVertices = vertexTripletReduction(new VertexTriplet(first, second, third));

            return vertexTripletReduction(new VertexTriplet(first, second, third));
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
        IdentifierList vertices1 = c2vsMap.get(selectedComponents.getLeft());
        IdentifierList vertices2 = c2vsMap.get(selectedComponents.getRight());

        IdentifierList vertexUnion = new IdentifierList();
        vertexUnion.addAll(vertices1);
        vertexUnion.addAll(vertices2);

        for (Identifier id1 : vertices1) {

            for (Identifier id2 : vertices2) {

                final double sumId1 = c2v.getDistances(id1, null).sum() - c2v.getDistance(id1, selectedComponents.getRight());
                final double sumId2 = c2v.getDistances(id2, null).sum() - c2v.getDistance(id2, selectedComponents.getLeft());

                double sumVId1 = 0.0;
                double sumVId2 = 0.0;

                for (Identifier id3 : vertexUnion) {
                    sumVId1 += c2v.getDistance(id1, id3);
                    sumVId2 += c2v.getDistance(id2, id3);
                }

                double q = ((vertexUnion.size() - 4 + vertices1.size() + vertices2.size()) * v2v.getDistance(id1, id2)) -
                        sumId1 - sumId2 - sumVId1 - sumVId2;

                if (q < minQ) {
                    minQ = q;
                    bestPair = new ImmutablePair<>(id1, id2);
                }
            }
        }

        return bestPair;
    }


    protected void updateDataStructures(Pair<Identifier, Identifier> selectedComponents, Pair<Identifier, Identifier> newVertices) {

        // Remove the selected components from step 1 from connected components
        c2vsMap.remove(selectedComponents.getLeft());
        c2vsMap.remove(selectedComponents.getRight());

        // Add the grouped vertices to the c2vsmap under a new component
        IdentifierList mergedSet = new IdentifierList();
        mergedSet.add(newVertices.getLeft());
        mergedSet.add(newVertices.getRight());
        c2vsMap.put(c2vsMap.createNextIdentifier(), mergedSet);

        // Update c2c and c2v matrices with new c2vsMap info
        this.updateC2C();
        this.updateC2V();
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

        // Create two new taxa for the reduced
        Identifier newVertex1 = c2vsMap.createNextIdentifier();
        Identifier newVertex2 = c2vsMap.createNextIdentifier();

        v2v.getTaxa().add(newVertex1);
        v2v.getTaxa().add(newVertex2);

        // Setup shortcuts
        final Identifier vertex1 = selectedVertices.vertex1;
        final Identifier vertex2 = selectedVertices.vertex2;
        final Identifier vertex3 = selectedVertices.vertex3;

        // Iterate over all active vertices
        for (Identifier v : v2v.getTaxa()) {

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
}
