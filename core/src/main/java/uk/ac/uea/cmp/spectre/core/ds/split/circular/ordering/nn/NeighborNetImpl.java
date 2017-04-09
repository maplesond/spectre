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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.spectre.core.ds.Identifier;
import uk.ac.uea.cmp.spectre.core.ds.IdentifierList;
import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.spectre.core.ds.distance.FlexibleDistanceMatrix;
import uk.ac.uea.cmp.spectre.core.ds.split.SplitSystem;
import uk.ac.uea.cmp.spectre.core.ds.split.circular.ordering.CVMatrices;
import uk.ac.uea.cmp.spectre.core.ds.split.circular.ordering.CircularOrderingCreator;

import java.util.*;

/**
 * Created by dan on 27/02/14.
 */
public class NeighborNetImpl implements CircularOrderingCreator {

    private static Logger log = LoggerFactory.getLogger(NeighborNetImpl.class);


    protected Stack<VertexTriplet> stackedVertexTriplets;

    protected CVMatrices mx;
    protected NeighborNetParams params;
    protected HashMap<Identifier, Pair<Identifier, Identifier>> expansionMap;

    public NeighborNetImpl() {
        this.stackedVertexTriplets = new Stack<>();
        this.mx = new CVMatrices();
        this.params = new NeighborNetParams();
        this.expansionMap = new HashMap<>();
    }

    @Override
    public IdentifierList createCircularOrdering(DistanceMatrix distanceMatrix) {

        // Setup all mx and temporary data structure required for NN
        this.mx = new CVMatrices(distanceMatrix);

        // Reduce down to a max of 3 vertices
        while (this.mx.getNbVertices() > 3) {

            // Choose a pair of clusters from c2c that minimise the Q criterion
            Pair<Identifier, Identifier> selectedClusters = this.selectionStep1(this.mx.getC2C());

            // Choose a pair of vertices that minimise the Q criterion
            Pair<Identifier, Identifier> selectedVertices = this.selectionStep2(selectedClusters);

            // Reduces vertices contained within selected clusters to a pair.  Using the selected vertices to determine
            // which vertices to reduce.  Automatically updates V2V matrix if necessary removing reduced verticies and
            // adding new ones
            Pair<Identifier, Identifier> newVertices = this.reduce(selectedClusters, selectedVertices);

            // Merge selected clusters containing the new vertices
            this.merge(selectedClusters, newVertices);
        }

        // Expand back to taxa to get circular ordering and translate back to original nomenclature
        IdentifierList circularOrdering = this.expand(this.stackedVertexTriplets);

        // Sanity check
        if (!this.stackedVertexTriplets.empty()) {
            throw new IllegalStateException("Vertex triplet stack still contains entries.  Something went wrong in the NN algorithm.");
        }

        log.info("NeighborNet produced the following circular ordering:");
        log.info("... By ID  : " + circularOrdering.toString(IdentifierList.IdentifierFormat.BY_ID));
        log.info("... By Name: " + circularOrdering.toString(IdentifierList.IdentifierFormat.BY_NAME));

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
     * Choose a pair of clusters that minimise the Q criterion from c2c
     *
     * @param c2c Cluster to cluster distance matrix
     * @return a pair of clusters that minimise the Q criterion
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

            final double q = (c2c.size() - 2) * id1_2_id2 - sumId1 - sumId2;

            if (q < minQ) {
                minQ = q;
                bestPair = entry.getKey();
            }
        }

        return bestPair;
    }


    protected Pair<Identifier, Identifier> selectionStep2(Pair<Identifier, Identifier> selectedClusters) {

        Pair<Identifier, Identifier> bestPair = null;
        double minQ = Double.MAX_VALUE;

        DistanceMatrix v2v = this.mx.getV2V();

        // Both should be 1 or 2 vertices in length
        IdentifierList vertices1 = this.mx.getVertices(selectedClusters.getLeft());
        IdentifierList vertices2 = this.mx.getVertices(selectedClusters.getRight());

        // First check if we only have one vertex in each list... in which case just return those
        if (vertices1.size() == 1 && vertices2.size() == 1) {
            return new ImmutablePair<>(vertices1.get(0), vertices2.get(0));
        }

        IdentifierList vertexUnion = new IdentifierList();
        vertexUnion.addAll(vertices1);
        vertexUnion.addAll(vertices2);

        final double mhat = this.mx.getNbClusters() + vertexUnion.size() - 2;

        for (Identifier v1 : vertices1) {

            for (Identifier v2 : vertices2) {

                double q = 0.0;

                if (false) {

                    // This implementation of selection step 2 is as described in Sarah's thesis.
                    // Gets the sum of distances from the selected vertex to all other clusters except the cluster associated with itself.
                    final double sumV1 = this.mx.sumVertex2Clusters(v1) - this.mx.getDistance(selectedClusters.getRight(), v1);
                    final double sumV2 = this.mx.sumVertex2Clusters(v2) - this.mx.getDistance(selectedClusters.getLeft(), v2);

                    double sumVId1 = 0.0;
                    double sumVId2 = 0.0;

                    for (Identifier v : vertexUnion) {
                        sumVId1 += v2v.getDistance(v1, v);
                        sumVId2 += v2v.getDistance(v2, v);
                    }

                    final double dist = v2v.getDistance(v1, v2);
                    final double mult = mhat - 2;

                    q = (mult * dist) - sumV1 - sumV2 - sumVId1 - sumVId2;
                }
                else {
                    // This implementation of selection step 2 is as described in the original neighbornet paper.
                    // Gets the sum of distances from the selected vertex to all other clusters except the cluster associated with itself.
                    final double sumV1 = this.mx.sumVertex2Clusters(v1);
                    final double sumV2 = this.mx.sumVertex2Clusters(v2);

                    double sumVId1 = 0.0;
                    double sumVId2 = 0.0;

                    final double dist = v2v.getDistance(v1, v2);
                    final double mult = mhat - 2;

                    q = (mult * dist) - sumV1 - sumV2;
                }

                if (q < minQ) {
                    minQ = q;
                    bestPair = new ImmutablePair<>(v1, v2);
                }
            }
        }

        return bestPair;
    }

    protected Pair<Identifier, Identifier> reduce(Pair<Identifier, Identifier> selectedClusters, Pair<Identifier, Identifier> selectedVertices) {

        // Both should be 1 or 2 vertices in length
        IdentifierList c1v = this.mx.getVertices(selectedClusters.getLeft());
        IdentifierList c2v = this.mx.getVertices(selectedClusters.getRight());

        Identifier sv1 = selectedVertices.getLeft();
        Identifier sv2 = selectedVertices.getRight();

        final int nbVerticies = c1v.size() + c2v.size();

        if (nbVerticies == 2) {
            return new ImmutablePair<>(c1v.get(0), c2v.get(0));
        } else if (nbVerticies == 3) {

            // Work out order of the three vertices across the two clusters
            Identifier first = c1v.size() != 1 && (c1v.get(0).equals(sv1) || c1v.get(0).equals(sv2)) ? c1v.get(1) : c1v.get(0);
            Identifier second = c1v.size() == 1 ? (c2v.get(0).equals(sv1) || c2v.get(0).equals(sv2)) ? c2v.get(0) : c2v.get(1) :
                    (c1v.get(0).equals(sv1) || c1v.get(0).equals(sv2)) ? c1v.get(0) : c1v.get(1);
            Identifier third = c1v.size() == 1 && (c2v.get(0).equals(sv1) || c2v.get(0).equals(sv2)) ? c2v.get(1) : c2v.get(0);

            // Put triplet into canonical form (i.e. the first vertex's ID should be lower than third vertex's ID
            //final boolean reverse = first.getId() > third.getId();
            final boolean reverse = false;
            final VertexTriplet triplet = reverse ? new VertexTriplet(third, second, first) : new VertexTriplet(first, second, third);

            // Add triplet to stack; Reduce triplet of vertices to two new vertices; Update V2V matrix; Return the two new vertices.
            return vertexTripletReduction(triplet);

        } else if (nbVerticies == 4) {

            if (c1v.size() != 2 && c2v.size() != 2) {
                throw new IllegalStateException("Found two clusters with 4 vertices but was expecting each cluster to have 2 vertices each.  First cluster contains " + c1v.size() + " vertices.  Second cluster contains " + c2v.size() + " vertices.");
            }

            Identifier first = c1v.get(0).equals(sv1) ?
                    c1v.get(1) :
                    c1v.get(0);

            Identifier second = sv1;
            Identifier third = sv2;

            Identifier fourth = c2v.get(0).equals(sv2) ?
                    c2v.get(1) :
                    c2v.get(0);

            // Put triplet into canonical form (i.e. the first vertex's ID should be lower than third vertex's ID
            //final boolean reverse = first.getId() > fourth.getId();
            final boolean reverse = false;

            // Because we have 4 vertices we need to do two triplet reductions
            if (reverse) {
                Pair<Identifier, Identifier> newVertices1 = this.vertexTripletReduction(new VertexTriplet(first, second, third));
                Pair<Identifier, Identifier> newVertices2 = this.vertexTripletReduction(new VertexTriplet(newVertices1.getLeft(), newVertices1.getRight(), fourth));
                return newVertices2;
            }
            else {
                Pair<Identifier, Identifier> newVertices1 = this.vertexTripletReduction(new VertexTriplet(fourth, third, second));
                Pair<Identifier, Identifier> newVertices2 = this.vertexTripletReduction(new VertexTriplet(newVertices1.getLeft(), newVertices1.getRight(), first));
                return newVertices2;
            }
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

            int maxval1 = -1;
            int maxpos1 = -1;
            for(int i = 0; i < order.size(); i++) {
                int v = order.get(i);
                if (v > maxval1) {
                    maxval1 = v;
                    maxpos1 = i;
                }
            }
            int maxval2 = -1;
            int maxpos2 = -1;
            for(int i = 0; i < order.size(); i++) {
                int v = order.get(i);
                if (v != maxval1 && v > maxval2) {
                    maxval2 = v;
                    maxpos2 = i;
                }
            }

            if (Math.abs(maxpos1 - maxpos2) != 1) {
                throw new IllegalStateException("Top two vertex IDs are not located next to each other. VertexID:" + maxval1 + "(pos=" + maxpos1 + "); VertexID:" + maxval2 + "(pos=" + maxpos2 + ")");
            }

            boolean flip = maxpos1 < maxpos2;

            order.remove(maxpos1);
            order.remove(flip ? maxpos2 - 1 : maxpos2);

            VertexTriplet vt = stackedVertexTriplets.pop();

            if (flip) {
                int p = maxpos2 - 1;
                order.add(p, vt.vertex3.getId());
                order.add(p + 1, vt.vertex2.getId());
                order.add(p + 2, vt.vertex1.getId());
            }
            else {
                order.add(maxpos2, vt.vertex1.getId());
                order.add(maxpos2 + 1, vt.vertex2.getId());
                order.add(maxpos2 + 2, vt.vertex3.getId());
            }
        }

        IdentifierList orderedTaxa = new IdentifierList();

        for (Integer i : order) {
            orderedTaxa.add(this.mx.getVertex(i));
        }

        return this.mx.reverseTranslate(orderedTaxa);
    }

    private void merge(Pair<Identifier, Identifier> selectedClusters, Pair<Identifier, Identifier> newVertices) {

        // Add the grouped vertices to the c2vsmap under a new cluster
        IdentifierList mergedSet = new IdentifierList();
        mergedSet.add(newVertices.getLeft());
        mergedSet.add(newVertices.getRight());
        Identifier newCluster = this.mx.createNextCluster();

        // Remove the selected clusters and add the new cluster containing the merged vertices
        this.mx.getC2Vs().remove(selectedClusters.getLeft());
        this.mx.getC2Vs().remove(selectedClusters.getRight());
        this.mx.getC2Vs().put(newCluster, mergedSet);

        // Update V2C links
        this.mx.linkV2C(newVertices.getLeft(), newCluster);
        this.mx.linkV2C(newVertices.getRight(), newCluster);

        // Update matrices
        this.updateC2V();
        this.updateC2C();
    }

    private final void updateC2C() {

        // Simpler to clear the c2c matrix and start again... check to make sure this doesn't become a performance issue
        // when the number of taxa is large
        DistanceMatrix c2c = new FlexibleDistanceMatrix();

        for (Map.Entry<Identifier, IdentifierList> clusterGroup1 : this.mx.getMapEntries()) {

            for (Map.Entry<Identifier, IdentifierList> clusterGroup2 : this.mx.getMapEntries()) {

                double sum1 = 0.0;

                for (Identifier id1 : clusterGroup1.getValue()) {
                    for (Identifier id2 : clusterGroup2.getValue()) {
                        sum1 += this.mx.getV2V().getDistance(id1, id2);
                    }
                }

                c2c.setDistance(clusterGroup1.getKey(), clusterGroup2.getKey(),
                        1.0 / (clusterGroup1.getValue().size() * clusterGroup2.getValue().size()) * sum1);
            }
        }

        this.mx.setC2C(c2c);
    }

    private final Pair<Identifier, Identifier> updateV2V(VertexTriplet selectedVertices) {

        DistanceMatrix v2v = this.mx.getV2V();

        // Create two new taxa for the reduced (implicitly add them to v2v matrix)
        Identifier newVertex1 = this.mx.createNextVertex();
        Identifier newVertex2 = this.mx.createNextVertex();

        // Setup shortcuts
        final Identifier vertex1 = selectedVertices.vertex1;
        final Identifier vertex2 = selectedVertices.vertex2;
        final Identifier vertex3 = selectedVertices.vertex3;

        // Switch order to be consistent with original vertices
        Pair<Identifier, Identifier> newPair = new ImmutablePair<>(newVertex1, newVertex2);

        // Iterate over all active vertices to set the new distances
        for (Identifier v : this.mx.getVertices()) {

            // Only process this vertex if it is not in the selected vertex list nor one of the new verticies
            if (!v.equals(vertex1) && !v.equals(vertex2) && !v.equals(vertex3)
                    && !v.equals(newVertex1) && !v.equals(newVertex2)) {

                // NOTE:
                // We've modified this part from what is described in the original neighbornet paper
                // by Bryant and Moulton as in their distance reduction formula the parameters alpha
                // beta and gamma do not all multiply up to one in the case of d(u,a) and d(v,a).
                // We fix this by ensuring all 3 params are used for each new distance calculation.
                // These changes are described in Bastkowski's thesis.

                v2v.setDistance(newPair.getLeft(), v,
                        ((params.getAlpha() + params.getBeta()) * v2v.getDistance(vertex1, v)) +
                                (params.getGamma() * v2v.getDistance(vertex2, v)));

                v2v.setDistance(newPair.getRight(), v,
                        (params.getAlpha() * v2v.getDistance(vertex2, v)) +
                                ((params.getBeta() + params.getGamma()) * v2v.getDistance(vertex3, v)));
            }
        }

        // Set distance between the two new vertices
        v2v.setDistance(newPair.getLeft(), newPair.getRight(),
                (params.getAlpha() * v2v.getDistance(vertex1, vertex2)) +
                        (params.getBeta() * v2v.getDistance(vertex1, vertex3)) +
                        (params.getGamma() * v2v.getDistance(vertex2, vertex3)));

        // Remove the selected vertices from v2v
        this.mx.removeVertexTriplet(vertex1, vertex2, vertex3);

        return newPair;
    }


    private final void updateC2V() {

        this.mx.getC2V().clear();

        for (Map.Entry<Identifier, IdentifierList> clusters : this.mx.getMapEntries()) {

            for (Identifier v : this.mx.getVertices()) {

                    boolean found = false;
                    double sum1 = 0.0;

                    for (Identifier selectedVertex : clusters.getValue()) {
                        if (selectedVertex.equals(v)) {
                            found = true;
                            break;
                        }

                        sum1 += this.mx.getV2V().getDistance(selectedVertex, v);
                    }

                    if (!found) {
                        this.mx.setDistance(clusters.getKey(), v,
                                1.0 / clusters.getValue().size() * sum1);
                    }
            }
        }

    }

}
